from __future__ import annotations

from dataclasses import dataclass
from datetime import datetime
from hashlib import sha256
import json
from typing import Any, Iterable

import pandas as pd
from sqlalchemy import text

from sync.schema import CORE_DDL
from utils.db_helper import ENGINE, ensure_tables


@dataclass(frozen=True)
class SyncResult:
    task_code: str
    task_group: str
    total: int
    success: int
    failed: int
    message: str = ''


def ensure_core_schema() -> None:
    ensure_tables(CORE_DDL)


def safe_str(value: Any) -> str | None:
    if value is None or (isinstance(value, float) and pd.isna(value)):
        return None
    value = str(value).strip()
    return value or None


def safe_num(value: Any):
    if value is None or (isinstance(value, float) and pd.isna(value)):
        return None
    try:
        return float(value)
    except Exception:
        return None


def safe_date(value: Any):
    if value is None or (isinstance(value, float) and pd.isna(value)):
        return None
    try:
        parsed = pd.to_datetime(value, errors='coerce')
        if pd.isna(parsed):
            return None
        return parsed.date()
    except Exception:
        return None


def payload_hash(payload: Any) -> str:
    raw = json.dumps(payload, ensure_ascii=False, sort_keys=True, default=str)
    return sha256(raw.encode('utf-8')).hexdigest()


def upsert_many(table_sql: str, rows: list[dict], key_cols: list[str]) -> int:
    if not rows:
        return 0
    cols = list(rows[0].keys())
    update_cols = [c for c in cols if c not in key_cols]
    insert_cols = ', '.join(cols)
    placeholders = ', '.join(f':{c}' for c in cols)
    key_condition = ', '.join(key_cols)
    update_clause = ', '.join(f'{c} = EXCLUDED.{c}' for c in update_cols) if update_cols else None
    sql = f"""
    INSERT INTO {table_sql} ({insert_cols})
    VALUES ({placeholders})
    ON CONFLICT ({key_condition}) DO UPDATE SET {update_clause}
    """
    total = 0
    with ENGINE.begin() as conn:
        for row in rows:
            conn.execute(text(sql), row)
            total += 1
    return total


def save_raw_payload(task_code: str, business_key: str, payload: Any) -> None:
    payload_json = json.dumps(payload, ensure_ascii=False, default=str)
    with ENGINE.begin() as conn:
        conn.execute(
            text(
                '''
                INSERT INTO ak_source_raw_payload (source_name, task_code, business_key, payload_json, payload_hash, fetched_at, updated_at)
                VALUES (:source_name, :task_code, :business_key, :payload_json, :payload_hash, :fetched_at, :updated_at)
                ON CONFLICT (source_name, task_code, business_key) DO UPDATE SET
                    payload_json = EXCLUDED.payload_json,
                    payload_hash = EXCLUDED.payload_hash,
                    fetched_at = EXCLUDED.fetched_at,
                    updated_at = EXCLUDED.updated_at
                '''
            ),
            {
                'source_name': 'akshare',
                'task_code': task_code,
                'business_key': business_key,
                'payload_json': payload_json,
                'payload_hash': payload_hash(payload),
                'fetched_at': datetime.now(),
                'updated_at': datetime.now(),
            },
        )
