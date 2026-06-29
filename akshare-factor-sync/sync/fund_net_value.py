from __future__ import annotations

from datetime import datetime, timedelta
from typing import Any

import akshare as ak
import pandas as pd
from sqlalchemy import text

from utils.db_helper import ENGINE, ensure_tables, get_schema_prefix

TABLE_NAME = f"{get_schema_prefix()}fund_net_value"

DDL = [
    f'''
    CREATE TABLE IF NOT EXISTS {TABLE_NAME} (
        fund_code VARCHAR(32) NOT NULL,
        fund_name VARCHAR(255),
        trade_date DATE NOT NULL,
        net_value NUMERIC(24, 8),
        accumulated_value NUMERIC(24, 8),
        daily_growth_rate NUMERIC(24, 8),
        source_system VARCHAR(64) NOT NULL DEFAULT 'akshare',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_fund_net_value PRIMARY KEY (fund_code, trade_date)
    )
    ''',
]


def _safe_num(value: Any):
    if value is None or (isinstance(value, float) and pd.isna(value)):
        return None
    try:
        return float(value)
    except Exception:
        return None


def sync_fund_net_value(days_back: int = 90) -> int:
    ensure_tables(DDL)
    end_date = datetime.now().date()
    start_date = end_date - timedelta(days=days_back)
    df = ak.fund_etf_hist_em()
    if df is None or df.empty:
        return 0

    inserted = 0
    now = datetime.now()
    with ENGINE.begin() as conn:
        for _, row in df.iterrows():
            trade_date = row.get('日期') or row.get('trade_date')
            fund_code = row.get('基金代码') or row.get('fund_code') or row.get('代码')
            if not trade_date or not fund_code:
                continue
            conn.execute(
                text(
                    f'''
                    MERGE INTO {TABLE_NAME} AS t
                    USING (
                        SELECT
                            :fund_code AS fund_code,
                            :fund_name AS fund_name,
                            :trade_date AS trade_date,
                            :net_value AS net_value,
                            :accumulated_value AS accumulated_value,
                            :daily_growth_rate AS daily_growth_rate,
                            :updated_at AS updated_at
                    ) AS s
                    ON t.fund_code = s.fund_code AND t.trade_date = s.trade_date
                    WHEN MATCHED THEN UPDATE SET
                        fund_name = s.fund_name,
                        net_value = s.net_value,
                        accumulated_value = s.accumulated_value,
                        daily_growth_rate = s.daily_growth_rate,
                        updated_at = s.updated_at
                    WHEN NOT MATCHED THEN INSERT (
                        fund_code, fund_name, trade_date, net_value, accumulated_value,
                        daily_growth_rate, source_system, created_at, updated_at
                    ) VALUES (
                        s.fund_code, s.fund_name, s.trade_date, s.net_value, s.accumulated_value,
                        s.daily_growth_rate, 'akshare', s.updated_at, s.updated_at
                    )
                    '''
                ),
                {
                    'fund_code': str(fund_code).strip(),
                    'fund_name': str(row.get('基金简称') or row.get('fund_name') or '').strip() or None,
                    'trade_date': pd.to_datetime(trade_date).date(),
                    'net_value': _safe_num(row.get('单位净值') or row.get('net_value')),
                    'accumulated_value': _safe_num(row.get('累计净值') or row.get('accumulated_value')),
                    'daily_growth_rate': _safe_num(row.get('日增长率') or row.get('daily_growth_rate')),
                    'updated_at': now,
                },
            )
            inserted += 1
    return inserted
