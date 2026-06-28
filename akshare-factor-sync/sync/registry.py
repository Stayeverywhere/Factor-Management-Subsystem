from __future__ import annotations

from dataclasses import dataclass
from typing import Callable

from sync.factor import sync_factor_series
from sync.fund_basic import sync_fund_basic
from sync.fund_company import sync_fund_company
from sync.fund_dividend import sync_fund_dividend
from sync.fund_manager import sync_fund_manager
from sync.fund_net_value import sync_fund_net_value
from sync.fund_portfolio import sync_fund_portfolio
from sync.index import sync_index_quote
from sync.macro import sync_macro_indicator
from sync.market import sync_market_quote


@dataclass(frozen=True)
class TaskSpec:
    task_code: str
    task_name: str
    task_group: str
    runner: Callable[[], int]
    retry_limit: int = 3


TASKS: list[TaskSpec] = [
    TaskSpec('fund_profile', '基金基础信息同步', 'fund', sync_fund_basic),
    TaskSpec('fund_nav', '基金净值同步', 'fund', sync_fund_net_value),
    TaskSpec('fund_portfolio', '基金持仓同步', 'fund', sync_fund_portfolio),
    TaskSpec('fund_dividend', '基金分红拆分同步', 'fund', sync_fund_dividend),
    TaskSpec('fund_manager', '基金经理同步', 'fund', sync_fund_manager),
    TaskSpec('fund_company', '基金公司同步', 'fund', sync_fund_company),
    TaskSpec('market_quote', '行情数据同步', 'market', sync_market_quote),
    TaskSpec('index_quote', '指数数据同步', 'index', sync_index_quote),
    TaskSpec('macro_indicator', '宏观数据同步', 'macro', sync_macro_indicator),
    TaskSpec('factor_series', '因子序列同步', 'factor', sync_factor_series),
]
