from __future__ import annotations

from datetime import datetime

import akshare as ak
from sqlalchemy import text

from sync.base import ensure_core_schema, save_raw_payload
from sync.common import now_ts, safe_date, safe_num, safe_text
from utils.db_helper import ENGINE

TABLE_NAME = 'ak_macro_indicator'
TASK_CODE = 'macro_indicator'


def sync_macro_indicator() -> int:
    ensure_core_schema()
    if not hasattr(ak, 'macro_china_lpr'):
        return 0
    df = ak.macro_china_lpr()
    if df is None or df.empty:
        return 0

    rows = []
    now = now_ts()
    for _, row in df.iterrows():
        indicator_date = safe_date(row.get('日期') or row.get('indicator_date'))
        if not indicator_date:
            continue
        payload = row.to_dict()
        save_raw_payload(TASK_CODE, f"lpr:{indicator_date}", payload)
        rows.append({
            'indicator_code': 'china_lpr',
            'indicator_name': '贷款市场报价利率(LPR)',
            'indicator_date': indicator_date,
            'indicator_value': safe_num(row.get('1年') or row.get('indicator_value')),
            'unit': '%',
            'source_system': 'akshare',
            'created_at': now,
            'updated_at': now,
        })

    if not rows:
        return 0
    with ENGINE.begin() as conn:
        for r in rows:
            conn.execute(text('''
                INSERT INTO ak_macro_indicator (indicator_code, indicator_name, indicator_date, indicator_value, unit, source_system, created_at, updated_at)
                VALUES (:indicator_code, :indicator_name, :indicator_date, :indicator_value, :unit, :source_system, :created_at, :updated_at)
                ON CONFLICT (indicator_code, indicator_date) DO UPDATE SET
                    indicator_name=EXCLUDED.indicator_name, indicator_value=EXCLUDED.indicator_value,
                    unit=EXCLUDED.unit, updated_at=EXCLUDED.updated_at
            '''), r)
    return len(rows)
