from __future__ import annotations

from datetime import datetime

import akshare as ak
from sqlalchemy import text

from sync.base import ensure_core_schema, save_raw_payload
from sync.common import now_ts, safe_date, safe_num, safe_text
from utils.db_helper import ENGINE

TABLE_NAME = 'ak_fund_company'
TASK_CODE = 'fund_company'


def sync_fund_company() -> int:
    ensure_core_schema()
    if not hasattr(ak, 'fund_aum_em'):
        return 0
    df = ak.fund_aum_em()
    if df is None or df.empty:
        return 0

    rows = []
    now = now_ts()
    for _, row in df.iterrows():
        company_name = safe_text(row.get('基金公司') or row.get('company_name'))
        if not company_name:
            continue
        payload = row.to_dict()
        save_raw_payload(TASK_CODE, company_name, payload)
        rows.append({
            'company_name': company_name,
            'company_code': safe_text(row.get('公司代码') or row.get('company_code')),
            'established_date': safe_date(row.get('成立日期') or row.get('established_date')),
            'fund_count': safe_num(row.get('基金数量') or row.get('fund_count')),
            'manager_count': safe_num(row.get('基金经理数量') or row.get('manager_count')),
            'source_system': 'akshare',
            'created_at': now,
            'updated_at': now,
        })

    if not rows:
        return 0
    with ENGINE.begin() as conn:
        for r in rows:
            conn.execute(text('''
                INSERT INTO ak_fund_company (company_name, company_code, established_date, fund_count, manager_count, source_system, created_at, updated_at)
                VALUES (:company_name, :company_code, :established_date, :fund_count, :manager_count, :source_system, :created_at, :updated_at)
                ON CONFLICT (company_name) DO UPDATE SET
                    company_code=EXCLUDED.company_code, established_date=EXCLUDED.established_date,
                    fund_count=EXCLUDED.fund_count, manager_count=EXCLUDED.manager_count,
                    updated_at=EXCLUDED.updated_at
            '''), r)
    return len(rows)
