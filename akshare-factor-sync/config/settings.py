from pathlib import Path

BASE_DIR = Path(__file__).parent.parent

DB_CONFIG = {
    'dialect': 'postgresql',
    'driver': 'psycopg2',
    'host': '127.0.0.1',
    'port': 54321,
    'user': 'system',
    'password': 'ysb18316923897',
    'database': 'kingbase',
    'schema': 'biz_factor',
}

SYNC_CONFIG = {
    'sync_mode': 'full',
    'lookback_days': 3650,
    'source_system': 'akshare',
}
