from __future__ import annotations

import logging
from logging.handlers import RotatingFileHandler
from pathlib import Path

from sync.orchestrator import run_all

BASE_DIR = Path(__file__).resolve().parent
LOG_DIR = BASE_DIR / 'logs'
LOG_DIR.mkdir(exist_ok=True)

logger = logging.getLogger('akshare_factor_sync_once')
logger.setLevel(logging.INFO)
handler = RotatingFileHandler(LOG_DIR / 'run_once.log', maxBytes=10 * 1024 * 1024, backupCount=5, encoding='utf-8')
handler.setFormatter(logging.Formatter('%(asctime)s - %(levelname)s - %(message)s'))
logger.addHandler(handler)
logger.addHandler(logging.StreamHandler())


def main() -> None:
    run_all(logger)
    logger.info('run once completed')


if __name__ == '__main__':
    main()
