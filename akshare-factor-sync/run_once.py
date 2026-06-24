from __future__ import annotations

import logging
from logging.handlers import RotatingFileHandler
from pathlib import Path

from sync.fund_basic import sync_fund_basic
from sync.fund_net_value import sync_fund_net_value

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
    basic_count = sync_fund_basic()
    net_count = sync_fund_net_value()
    logger.info('run once done: fund_basic=%s, fund_net_value=%s', basic_count, net_count)


if __name__ == '__main__':
    main()
