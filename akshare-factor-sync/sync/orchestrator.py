from __future__ import annotations

from datetime import datetime
import logging
import time

from sqlalchemy import text

from sync.base import ensure_core_schema
from sync.registry import TASKS
from utils.db_helper import ENGINE


def ensure_tasks_seeded() -> None:
    ensure_core_schema()
    now = datetime.now()
    with ENGINE.begin() as conn:
        for task in TASKS:
            conn.execute(
                text(
                    '''
                    INSERT INTO ak_source_task (task_code, task_name, task_group, source_name, enabled, full_refresh, retry_limit, retry_delay_seconds, updated_at)
                    VALUES (:task_code, :task_name, :task_group, 'akshare', TRUE, TRUE, :retry_limit, 30, :updated_at)
                    ON CONFLICT (task_code) DO UPDATE SET
                        task_name = EXCLUDED.task_name,
                        task_group = EXCLUDED.task_group,
                        enabled = EXCLUDED.enabled,
                        full_refresh = EXCLUDED.full_refresh,
                        retry_limit = EXCLUDED.retry_limit,
                        retry_delay_seconds = EXCLUDED.retry_delay_seconds,
                        updated_at = EXCLUDED.updated_at
                    '''
                ),
                {
                    'task_code': task.task_code,
                    'task_name': task.task_name,
                    'task_group': task.task_group,
                    'retry_limit': task.retry_limit,
                    'updated_at': now,
                },
            )


def run_all(logger: logging.Logger) -> None:
    ensure_tasks_seeded()
    for task in TASKS:
        last_err = None
        for attempt in range(1, task.retry_limit + 1):
            started_at = datetime.now()
            try:
                count = task.runner()
                finished_at = datetime.now()
                with ENGINE.begin() as conn:
                    conn.execute(
                        text(
                            '''
                            INSERT INTO ak_sync_audit(task_code, task_group, status, total_count, success_count, failed_count, started_at, finished_at, message)
                            VALUES (:task_code, :task_group, 'success', :total_count, :success_count, 0, :started_at, :finished_at, :message)
                            '''
                        ),
                        {
                            'task_code': task.task_code,
                            'task_group': task.task_group,
                            'total_count': count,
                            'success_count': count,
                            'started_at': started_at,
                            'finished_at': finished_at,
                            'message': f'attempt={attempt}',
                        },
                    )
                logger.info('task success: %s count=%s attempt=%s', task.task_code, count, attempt)
                break
            except Exception as exc:
                last_err = exc
                logger.exception('task failed: %s attempt=%s', task.task_code, attempt)
                if attempt < task.retry_limit:
                    time.sleep(3 * attempt)
                else:
                    finished_at = datetime.now()
                    with ENGINE.begin() as conn:
                        conn.execute(
                            text(
                                '''
                                INSERT INTO ak_sync_audit(task_code, task_group, status, total_count, success_count, failed_count, started_at, finished_at, message)
                                VALUES (:task_code, :task_group, 'failed', 0, 0, 1, :started_at, :finished_at, :message)
                                '''
                            ),
                            {
                                'task_code': task.task_code,
                                'task_group': task.task_group,
                                'started_at': started_at,
                                'finished_at': finished_at,
                                'message': str(last_err),
                            },
                        )
