from __future__ import annotations

from datetime import datetime

import akshare as ak
import pandas as pd
from sqlalchemy import text

from sync.base import ensure_core_schema, save_raw_payload
from sync.common import now_ts, safe_num, safe_text
from utils.db_helper import ENGINE

TABLE_NAME = 'ak_fund_nav'
TASK_CODE = 'fund_nav'


def sync_fund_net_value() -> int:
    ensure_core_schema()
    df = ak.fund_etf_hist_em()
    if df is None or df.empty:
        return 0

    rows = []
    now = now_ts()
    for _, row in df.iterrows():
        trade_date = row.get('日期') or row.get('trade_date')
        fund_code = row.get('基金代码') or row.get('fund_code') or row.get('代码')
        if not trade_date or not fund_code:
            continue
        payload = row.to_dict()
        save_raw_payload(TASK_CODE, safe_text(fund_code) or 'unknown', payload)
        rows.append({
            'fund_code': safe_text(fund_code),
            'fund_name': safe_text(row.get('基金简称') or row.get('fund_name')),
            'trade_date': pd.to_datetime(trade_date).date(),
            'nav_value': safe_num(row.get('单位净值') or row.get('net_value')),
            'accum_nav_value': safe_num(row.get('累计净值') or row.get('accumulated_value')),
            'daily_growth_rate': safe_num(row.get('日增长率') or row.get('daily_growth_rate')),
            'source_system': 'akshare',
            'created_at': now,
            'updated_at': now,
        })

    if not rows:
        return 0
    with ENGINE.begin() as conn:
        for r in rows:
            conn.execute(text('''
                INSERT INTO ak_fund_nav (fund_code, fund_name, trade_date, nav_value, accum_nav_value, daily_growth_rate, source_system, created_at, updated_at)
                VALUES (:fund_code, :fund_name, :trade_date, :nav_value, :accum_nav_value, :daily_growth_rate, :source_system, :created_at, :updated_at)
                ON CONFLICT (fund_code, trade_date) DO UPDATE SET
                    fund_name=EXCLUDED.fund_name, nav_value=EXCLUDED.nav_value,
                    accum_nav_value=EXCLUDED.accum_nav_value, daily_growth_rate=EXCLUDED.daily_growth_rate,
                    updated_at=EXCLUDED.updated_at
            '''), r)
    return len(rows)
