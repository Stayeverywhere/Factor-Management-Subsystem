from __future__ import annotations

from datetime import datetime

import akshare as ak
from sqlalchemy import text

from sync.base import ensure_core_schema, save_raw_payload
from sync.common import now_ts, safe_date, safe_num, safe_text
from utils.db_helper import ENGINE

TABLE_NAME = 'ak_fund_dividend'
TASK_CODE = 'fund_dividend'


def sync_fund_dividend() -> int:
    ensure_core_schema()
    if not hasattr(ak, 'fund_announcement_dividend_em'):
        return 0
    df = ak.fund_announcement_dividend_em()
    if df is None or df.empty:
        return 0

    rows = []
    now = now_ts()
    for _, row in df.iterrows():
        fund_code = safe_text(row.get('基金代码') or row.get('fund_code'))
        dividend_date = safe_date(row.get('除息日') or row.get('分红日期') or row.get('dividend_date'))
        if not fund_code or not dividend_date:
            continue
        payload = row.to_dict()
        save_raw_payload(TASK_CODE, f'{fund_code}:{dividend_date}', payload)
        rows.append({
            'fund_code': fund_code,
            'dividend_date': dividend_date,
            'dividend_amount': safe_num(row.get('分红') or row.get('dividend_amount')),
            'split_ratio': safe_num(row.get('拆分比例') or row.get('split_ratio')),
            'dividend_type': safe_text(row.get('分红类型') or row.get('dividend_type')),
            'source_system': 'akshare',
            'created_at': now,
            'updated_at': now,
        })

    if not rows:
        return 0
    with ENGINE.begin() as conn:
        for r in rows:
            conn.execute(text('''
                INSERT INTO ak_fund_dividend (fund_code, dividend_date, dividend_amount, split_ratio, dividend_type, source_system, created_at, updated_at)
                VALUES (:fund_code, :dividend_date, :dividend_amount, :split_ratio, :dividend_type, :source_system, :created_at, :updated_at)
                ON CONFLICT (fund_code, dividend_date) DO UPDATE SET
                    dividend_amount=EXCLUDED.dividend_amount, split_ratio=EXCLUDED.split_ratio,
                    dividend_type=EXCLUDED.dividend_type, updated_at=EXCLUDED.updated_at
            '''), r)
    return len(rows)
