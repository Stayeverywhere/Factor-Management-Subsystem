from __future__ import annotations

from datetime import datetime

import akshare as ak
from sqlalchemy import text

from sync.base import ensure_core_schema, save_raw_payload
from sync.common import now_ts, safe_date, safe_num, safe_text
from utils.db_helper import ENGINE

TABLE_NAME = 'ak_index_quote'
TASK_CODE = 'index_quote'


def sync_index_quote() -> int:
    ensure_core_schema()
    if not hasattr(ak, 'index_zh_a_hist'):
        return 0
    df = ak.index_zh_a_hist(symbol='000001', period='daily', start_date='20240101', end_date='20241231')
    if df is None or df.empty:
        return 0

    rows = []
    now = now_ts()
    for _, row in df.iterrows():
        trade_date = safe_date(row.get('日期') or row.get('trade_date'))
        if not trade_date:
            continue
        payload = row.to_dict()
        save_raw_payload(TASK_CODE, f"index_zh_a_hist:000001:{trade_date}", payload)
        rows.append({
            'symbol': '000001',
            'trade_date': trade_date,
            'open_price': safe_num(row.get('开盘') or row.get('open')),
            'close_price': safe_num(row.get('收盘') or row.get('close')),
            'high_price': safe_num(row.get('最高') or row.get('high')),
            'low_price': safe_num(row.get('最低') or row.get('low')),
            'volume': safe_num(row.get('成交量') or row.get('volume')),
            'turnover': safe_num(row.get('成交额') or row.get('turnover')),
            'source_system': 'akshare',
            'created_at': now,
            'updated_at': now,
        })

    if not rows:
        return 0
    with ENGINE.begin() as conn:
        for r in rows:
            conn.execute(text('''
                INSERT INTO ak_index_quote (symbol, trade_date, open_price, close_price, high_price, low_price, volume, turnover, source_system, created_at, updated_at)
                VALUES (:symbol, :trade_date, :open_price, :close_price, :high_price, :low_price, :volume, :turnover, :source_system, :created_at, :updated_at)
                ON CONFLICT (symbol, trade_date) DO UPDATE SET
                    open_price=EXCLUDED.open_price, close_price=EXCLUDED.close_price,
                    high_price=EXCLUDED.high_price, low_price=EXCLUDED.low_price,
                    volume=EXCLUDED.volume, turnover=EXCLUDED.turnover,
                    updated_at=EXCLUDED.updated_at
            '''), r)
    return len(rows)
