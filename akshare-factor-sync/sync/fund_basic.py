from __future__ import annotations

from datetime import datetime
from typing import Any

import akshare as ak
import pandas as pd
from sqlalchemy import text

from utils.db_helper import ENGINE, ensure_tables, get_schema_prefix

TABLE_NAME = f"{get_schema_prefix()}fund_basic"
SOURCE_TABLE_NAME = f"{get_schema_prefix()}fund_basic_source"

DDL = [
    f'''
    CREATE TABLE IF NOT EXISTS {TABLE_NAME} (
        fund_code VARCHAR(32) NOT NULL,
        fund_name VARCHAR(255) NOT NULL,
        fund_type VARCHAR(128),
        company VARCHAR(255),
        issue_date DATE,
        setup_date DATE,
        size VARCHAR(64),
        fee_rate VARCHAR(64),
        manager VARCHAR(255),
        source_url TEXT,
        source_system VARCHAR(64) NOT NULL DEFAULT 'akshare',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_fund_basic PRIMARY KEY (fund_code)
    )
    ''',
    f'''
    CREATE TABLE IF NOT EXISTS {SOURCE_TABLE_NAME} (
        fund_code VARCHAR(32) NOT NULL,
        payload TEXT,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_fund_basic_source PRIMARY KEY (fund_code)
    )
    ''',
]


def _safe_str(value: Any) -> str | None:
    if value is None or (isinstance(value, float) and pd.isna(value)):
        return None
    value = str(value).strip()
    return value or None


def sync_fund_basic() -> int:
    ensure_tables(DDL)
    df = ak.fund_name_em()
    if df is None or df.empty:
        return 0

    now = datetime.now()
    inserted = 0
    with ENGINE.begin() as conn:
        for _, row in df.iterrows():
            fund_code = _safe_str(row.get('基金代码') or row.get('fund_code'))
            fund_name = _safe_str(row.get('基金简称') or row.get('基金名称') or row.get('fund_name'))
            if not fund_code or not fund_name:
                continue

            payload = row.to_json(force_ascii=False)
            conn.execute(
                text(
                    f'''
                    MERGE INTO {TABLE_NAME} AS t
                    USING (
                        SELECT
                            :fund_code AS fund_code,
                            :fund_name AS fund_name,
                            :fund_type AS fund_type,
                            :company AS company,
                            :issue_date AS issue_date,
                            :setup_date AS setup_date,
                            :size AS size,
                            :fee_rate AS fee_rate,
                            :manager AS manager,
                            :source_url AS source_url,
                            :updated_at AS updated_at
                    ) AS s
                    ON t.fund_code = s.fund_code
                    WHEN MATCHED THEN UPDATE SET
                        fund_name = s.fund_name,
                        fund_type = s.fund_type,
                        company = s.company,
                        issue_date = s.issue_date,
                        setup_date = s.setup_date,
                        size = s.size,
                        fee_rate = s.fee_rate,
                        manager = s.manager,
                        source_url = s.source_url,
                        updated_at = s.updated_at
                    WHEN NOT MATCHED THEN INSERT (
                        fund_code, fund_name, fund_type, company, issue_date, setup_date,
                        size, fee_rate, manager, source_url, source_system, created_at, updated_at
                    ) VALUES (
                        s.fund_code, s.fund_name, s.fund_type, s.company, s.issue_date, s.setup_date,
                        s.size, s.fee_rate, s.manager, s.source_url, 'akshare', s.updated_at, s.updated_at
                    )
                    '''
                ),
                {
                    'fund_code': fund_code,
                    'fund_name': fund_name,
                    'fund_type': _safe_str(row.get('基金类型')),
                    'company': _safe_str(row.get('基金公司')),
                    'issue_date': _safe_str(row.get('成立日期')),
                    'setup_date': _safe_str(row.get('成立日期')),
                    'size': _safe_str(row.get('基金规模')),
                    'fee_rate': _safe_str(row.get('管理费率')),
                    'manager': _safe_str(row.get('基金经理')),
                    'source_url': None,
                    'updated_at': now,
                },
            )
            conn.execute(
                text(
                    f'''
                    MERGE INTO {SOURCE_TABLE_NAME} AS t
                    USING (
                        SELECT :fund_code AS fund_code, :payload AS payload, :updated_at AS updated_at
                    ) AS s
                    ON t.fund_code = s.fund_code
                    WHEN MATCHED THEN UPDATE SET payload = s.payload, updated_at = s.updated_at
                    WHEN NOT MATCHED THEN INSERT (fund_code, payload, updated_at)
                    VALUES (s.fund_code, s.payload, s.updated_at)
                    '''
                ),
                {'fund_code': fund_code, 'payload': payload, 'updated_at': now},
            )
            inserted += 1
    return inserted
