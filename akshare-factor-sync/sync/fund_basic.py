from __future__ import annotations

from datetime import datetime

import akshare as ak
from sqlalchemy import text

from sync.base import ensure_core_schema, save_raw_payload
from sync.common import safe_date, safe_text, now_ts
from utils.db_helper import ENGINE

TABLE_NAME = 'ak_fund_profile'
TASK_CODE = 'fund_profile'


def sync_fund_basic() -> int:
    ensure_core_schema()
    df = ak.fund_name_em()
    if df is None or df.empty:
        return 0

    rows = []
    now = now_ts()
    for _, row in df.iterrows():
        fund_code = safe_text(row.get('基金代码') or row.get('fund_code'))
        fund_name = safe_text(row.get('基金简称') or row.get('基金名称') or row.get('fund_name'))
        if not fund_code or not fund_name:
            continue
        payload = row.to_dict()
        save_raw_payload(TASK_CODE, fund_code, payload)
        rows.append({
            'fund_code': fund_code,
            'fund_name': fund_name,
            'fund_type': safe_text(row.get('基金类型')),
            'company_name': safe_text(row.get('基金公司')),
            'manager_name': safe_text(row.get('基金经理')),
            'issue_date': safe_date(row.get('成立日期')),
            'setup_date': safe_date(row.get('成立日期')),
            'fund_size': safe_text(row.get('基金规模')),
            'fee_rate': safe_text(row.get('管理费率')),
            'source_url': None,
            'source_system': 'akshare',
            'created_at': now,
            'updated_at': now,
        })

    if not rows:
        return 0
    with ENGINE.begin() as conn:
        for r in rows:
            conn.execute(text('''
                INSERT INTO ak_fund_profile (fund_code, fund_name, fund_type, company_name, manager_name, issue_date, setup_date, fund_size, fee_rate, source_url, source_system, created_at, updated_at)
                VALUES (:fund_code, :fund_name, :fund_type, :company_name, :manager_name, :issue_date, :setup_date, :fund_size, :fee_rate, :source_url, :source_system, :created_at, :updated_at)
                ON CONFLICT (fund_code) DO UPDATE SET
                    fund_name=EXCLUDED.fund_name, fund_type=EXCLUDED.fund_type, company_name=EXCLUDED.company_name,
                    manager_name=EXCLUDED.manager_name, issue_date=EXCLUDED.issue_date, setup_date=EXCLUDED.setup_date,
                    fund_size=EXCLUDED.fund_size, fee_rate=EXCLUDED.fee_rate, source_url=EXCLUDED.source_url,
                    updated_at=EXCLUDED.updated_at
            '''), r)
    return len(rows)
