from __future__ import annotations

from datetime import datetime

import akshare as ak
from sqlalchemy import text

from sync.base import ensure_core_schema, save_raw_payload
from sync.common import now_ts, safe_date, safe_num, safe_text
from utils.db_helper import ENGINE

TABLE_NAME = 'ak_factor_series'
TASK_CODE = 'factor_series'


def sync_factor_series() -> int:
    ensure_core_schema()
    if not hasattr(ak, 'fund_etf_hist_em'):
        return 0
    df = ak.fund_etf_hist_em()
    if df is None or df.empty:
        return 0

    rows = []
    now = now_ts()
    for _, row in df.iterrows():
        series_date = safe_date(row.get('日期') or row.get('trade_date'))
        if not series_date:
            continue
        payload = row.to_dict()
        save_raw_payload(TASK_CODE, f"factor:nav:{series_date}", payload)
        rows.append({
            'factor_code': 'fund_etf_nav',
            'factor_name': '基金ETF历史净值',
            'series_date': series_date,
            'series_value': safe_num(row.get('单位净值') or row.get('net_value')),
            'source_system': 'akshare',
            'created_at': now,
            'updated_at': now,
        })

    if not rows:
        return 0
    with ENGINE.begin() as conn:
        for r in rows:
            conn.execute(text('''
                INSERT INTO ak_factor_series (factor_code, factor_name, series_date, series_value, source_system, created_at, updated_at)
                VALUES (:factor_code, :factor_name, :series_date, :series_value, :source_system, :created_at, :updated_at)
                ON CONFLICT (factor_code, series_date) DO UPDATE SET
                    factor_name=EXCLUDED.factor_name, series_value=EXCLUDED.series_value,
                    updated_at=EXCLUDED.updated_at
            '''), r)
    return len(rows)
