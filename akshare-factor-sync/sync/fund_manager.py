from __future__ import annotations

from datetime import datetime

import akshare as ak
from sqlalchemy import text

from sync.base import ensure_core_schema, save_raw_payload
from sync.common import now_ts, safe_date, safe_num, safe_text
from utils.db_helper import ENGINE

TABLE_NAME = 'ak_fund_manager'
TASK_CODE = 'fund_manager'


def sync_fund_manager() -> int:
    ensure_core_schema()
    if not hasattr(ak, 'fund_manager_em'):
        return 0
    df = ak.fund_manager_em()
    if df is None or df.empty:
        return 0

    rows = []
    now = now_ts()
    for _, row in df.iterrows():
        manager_name = safe_text(row.get('基金经理') or row.get('manager_name'))
        if not manager_name:
            continue
        payload = row.to_dict()
        save_raw_payload(TASK_CODE, manager_name, payload)
        rows.append({
            'manager_name': manager_name,
            'company_name': safe_text(row.get('基金公司') or row.get('company_name')),
            'start_date': safe_date(row.get('任职日期') or row.get('start_date')),
            'manage_fund_count': safe_num(row.get('在管基金数') or row.get('manage_fund_count')),
            'max_return': safe_num(row.get('最佳回报') or row.get('max_return')),
            'source_system': 'akshare',
            'created_at': now,
            'updated_at': now,
        })

    if not rows:
        return 0
    with ENGINE.begin() as conn:
        for r in rows:
            conn.execute(text('''
                INSERT INTO ak_fund_manager (manager_name, company_name, start_date, manage_fund_count, max_return, source_system, created_at, updated_at)
                VALUES (:manager_name, :company_name, :start_date, :manage_fund_count, :max_return, :source_system, :created_at, :updated_at)
                ON CONFLICT (manager_name, company_name) DO UPDATE SET
                    start_date=EXCLUDED.start_date, manage_fund_count=EXCLUDED.manage_fund_count,
                    max_return=EXCLUDED.max_return, updated_at=EXCLUDED.updated_at
            '''), r)
    return len(rows)
