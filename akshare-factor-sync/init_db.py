from __future__ import annotations

from config.settings import DB_CONFIG
from sync.fund_basic import DDL as FUND_BASIC_DDL
from sync.fund_net_value import DDL as FUND_NET_VALUE_DDL
from utils.db_helper import ENGINE, ensure_tables, execute_sql, get_schema_prefix


def ensure_schema() -> None:
    schema = DB_CONFIG.get('schema')
    if not schema:
        return
    execute_sql(f'CREATE SCHEMA IF NOT EXISTS "{schema}"')
    execute_sql(f'SET search_path TO "{schema}"')


def ensure_extensions() -> None:
    # 金仓环境一般不需要额外扩展，这里保留入口方便后续按需补充
    return


def main() -> None:
    ensure_schema()
    ensure_extensions()
    ensure_tables([*FUND_BASIC_DDL, *FUND_NET_VALUE_DDL])
    print(f"Database initialized successfully under schema: {DB_CONFIG.get('schema')}")


if __name__ == '__main__':
    main()
