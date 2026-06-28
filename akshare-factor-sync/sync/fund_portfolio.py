from __future__ import annotations

from datetime import datetime

import akshare as ak
from sqlalchemy import text

from sync.base import ensure_core_schema, save_raw_payload
from sync.common import now_ts, safe_date, safe_num, safe_text
from utils.db_helper import ENGINE

TABLE_NAME = 'ak_fund_portfolio'
TASK_CODE = 'fund_portfolio'


def sync_fund_portfolio() -> int:
    ensure_core_schema()
    if not hasattr(ak, 'fund_portfolio_hold_em'):
        return 0
    df = ak.fund_portfolio_hold_em()
    if df is None or df.empty:
        return 0

    rows = []
    now = now_ts()
    for _, row in df.iterrows():
        fund_code = safe_text(row.get('基金代码') or row.get('fund_code'))
        holding_name = safe_text(row.get('股票名称') or row.get('持仓股票') or row.get('holding_name'))
        report_date = safe_date(row.get('报告期') or row.get('report_date'))
        if not fund_code or not holding_name or not report_date:
            continue
        payload = row.to_dict()
        save_raw_payload(TASK_CODE, f'{fund_code}:{report_date}:{holding_name}', payload)
        rows.append({
            'fund_code': fund_code,
            'report_date': report_date,
            'holding_name': holding_name,
            'holding_code': safe_text(row.get('股票代码') or row.get('holding_code')),
            'holding_ratio': safe_num(row.get('占净值比例') or row.get('holding_ratio')),
            'holding_market_value': safe_num(row.get('持股市值') or row.get('holding_market_value')),
            'source_system': 'akshare',
            'created_at': now,
            'updated_at': now,
        })

    if not rows:
        return 0
    with ENGINE.begin() as conn:
        for r in rows:
            conn.execute(text('''
                INSERT INTO ak_fund_portfolio (fund_code, report_date, holding_name, holding_code, holding_ratio, holding_market_value, source_system, created_at, updated_at)
                VALUES (:fund_code, :report_date, :holding_name, :holding_code, :holding_ratio, :holding_market_value, :source_system, :created_at, :updated_at)
                ON CONFLICT (fund_code, report_date, holding_name) DO UPDATE SET
                    holding_code=EXCLUDED.holding_code, holding_ratio=EXCLUDED.holding_ratio,
                    holding_market_value=EXCLUDED.holding_market_value, updated_at=EXCLUDED.updated_at
            '''), r)
    return len(rows)
