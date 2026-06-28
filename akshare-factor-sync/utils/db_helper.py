from __future__ import annotations

import re
from contextlib import contextmanager
from typing import Iterable, Mapping

from sqlalchemy import create_engine, event, text
from sqlalchemy.engine import Engine
from sqlalchemy.orm import Session, sessionmaker

from config.settings import DB_CONFIG

# ── Kingbase 版本号补丁 ──────────────────────────────────────────────
# KingbaseES SELECT version() 返回 "KingbaseES V009R001C010"
# SQLAlchemy PostgreSQL 方言无法解析，需要手动转换成 (9, 0, 1)
import sqlalchemy.dialects.postgresql.base as pg_base

_orig_get_server_version = pg_base.PGDialect._get_server_version_info

def _kingbase_get_server_version(self, connection):
    """替换原方法：解析 Kingbase 版本号，避免 AssertionError"""
    try:
        return _orig_get_server_version(self, connection)
    except (AssertionError, Exception):
        pass
    # 从连接直接查版本
    cursor = connection.exec_driver_sql("SELECT version()")
    row = cursor.fetchone()
    raw = row[0] if row else ''
    m = re.search(r'V(\d+)[Rr](\d+)[Cc](\d+)', raw)
    if m:
        return tuple(int(x) for x in m.groups())
    return (9, 0, 1)  # fallback 兼容版本

pg_base.PGDialect._get_server_version_info = _kingbase_get_server_version
# ─────────────────────────────────────────────────────────────────────


def build_database_url() -> str:
    dialect = DB_CONFIG.get('dialect', 'postgresql')
    driver = DB_CONFIG.get('driver', 'psycopg2')
    return (
        f"{dialect}+{driver}://"
        f"{DB_CONFIG['user']}:{DB_CONFIG['password']}@{DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}"
    )


def get_engine() -> Engine:
    url = build_database_url()
    engine = create_engine(
        url,
        pool_pre_ping=True,
        future=True,
        connect_args={"options": f"-csearch_path={DB_CONFIG['schema']}"},
    )
    return engine


ENGINE = get_engine()
SessionLocal = sessionmaker(bind=ENGINE, autoflush=False, autocommit=False, future=True)


def get_schema_prefix() -> str:
    return ''


@contextmanager
def session_scope() -> Session:
    session = SessionLocal()
    try:
        yield session
        session.commit()
    except Exception:
        session.rollback()
        raise
    finally:
        session.close()


def execute_sql(sql: str, params: Mapping | None = None):
    with ENGINE.begin() as conn:
        return conn.execute(text(sql), params or {})


def ensure_tables(sql_statements: Iterable[str]) -> None:
    with ENGINE.begin() as conn:
        for ddl in sql_statements:
            conn.execute(text(ddl))
