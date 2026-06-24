from __future__ import annotations

from contextlib import contextmanager
from typing import Iterable, Mapping

from sqlalchemy import create_engine, text
from sqlalchemy.engine import Engine
from sqlalchemy.orm import Session, sessionmaker

from config.settings import DB_CONFIG


def build_database_url() -> str:
    return (
        f"{DB_CONFIG.get('dialect', 'kingbase')}+ksycopg2://"
        f"{DB_CONFIG['user']}:{DB_CONFIG['password']}@{DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}"
    )


def get_engine() -> Engine:
    return create_engine(
        build_database_url(),
        pool_pre_ping=True,
        future=True,
        connect_args={"options": f"-c search_path={DB_CONFIG['schema']}"},
    )


ENGINE = get_engine()
SessionLocal = sessionmaker(bind=ENGINE, autoflush=False, autocommit=False, future=True)


def get_schema_prefix() -> str:
    return ""


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