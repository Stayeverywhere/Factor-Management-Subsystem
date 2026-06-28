from __future__ import annotations

CORE_DDL = [
    '''
    CREATE TABLE IF NOT EXISTS ak_source_task (
        task_code VARCHAR(128) NOT NULL,
        task_name VARCHAR(255) NOT NULL,
        task_group VARCHAR(64) NOT NULL,
        source_name VARCHAR(64) NOT NULL DEFAULT 'akshare',
        enabled BOOLEAN NOT NULL DEFAULT TRUE,
        full_refresh BOOLEAN NOT NULL DEFAULT TRUE,
        retry_limit INT NOT NULL DEFAULT 3,
        retry_delay_seconds INT NOT NULL DEFAULT 30,
        last_run_at TIMESTAMP,
        last_success_at TIMESTAMP,
        last_status VARCHAR(32),
        last_message TEXT,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_ak_source_task PRIMARY KEY (task_code)
    )
    ''',
    '''
    CREATE TABLE IF NOT EXISTS ak_source_raw_payload (
        source_name VARCHAR(64) NOT NULL DEFAULT 'akshare',
        task_code VARCHAR(128) NOT NULL,
        business_key VARCHAR(256) NOT NULL,
        payload_json TEXT NOT NULL,
        payload_hash VARCHAR(128),
        fetched_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_ak_source_raw_payload PRIMARY KEY (source_name, task_code, business_key)
    )
    ''',
    '''
    CREATE TABLE IF NOT EXISTS ak_sync_audit (
        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        task_code VARCHAR(128) NOT NULL,
        task_group VARCHAR(64) NOT NULL,
        status VARCHAR(32) NOT NULL,
        total_count INT NOT NULL DEFAULT 0,
        success_count INT NOT NULL DEFAULT 0,
        failed_count INT NOT NULL DEFAULT 0,
        started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        finished_at TIMESTAMP,
        message TEXT
    )
    ''',
    '''
    CREATE TABLE IF NOT EXISTS ak_fund_profile (
        fund_code VARCHAR(32) NOT NULL,
        fund_name VARCHAR(255) NOT NULL,
        fund_type VARCHAR(128),
        company_name VARCHAR(255),
        manager_name VARCHAR(255),
        issue_date DATE,
        setup_date DATE,
        fund_size VARCHAR(64),
        fee_rate VARCHAR(64),
        source_url TEXT,
        source_system VARCHAR(64) NOT NULL DEFAULT 'akshare',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_ak_fund_profile PRIMARY KEY (fund_code)
    )
    ''',
    '''
    CREATE TABLE IF NOT EXISTS ak_fund_nav (
        fund_code VARCHAR(32) NOT NULL,
        fund_name VARCHAR(255),
        trade_date DATE NOT NULL,
        nav_value NUMERIC(24, 8),
        accum_nav_value NUMERIC(24, 8),
        daily_growth_rate NUMERIC(24, 8),
        source_system VARCHAR(64) NOT NULL DEFAULT 'akshare',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_ak_fund_nav PRIMARY KEY (fund_code, trade_date)
    )
    ''',
    '''
    CREATE TABLE IF NOT EXISTS ak_fund_portfolio (
        fund_code VARCHAR(32) NOT NULL,
        report_date DATE NOT NULL,
        holding_name VARCHAR(255) NOT NULL,
        holding_code VARCHAR(64),
        holding_ratio NUMERIC(24, 8),
        holding_market_value NUMERIC(24, 8),
        source_system VARCHAR(64) NOT NULL DEFAULT 'akshare',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_ak_fund_portfolio PRIMARY KEY (fund_code, report_date, holding_name)
    )
    ''',
    '''
    CREATE TABLE IF NOT EXISTS ak_fund_dividend (
        fund_code VARCHAR(32) NOT NULL,
        dividend_date DATE NOT NULL,
        dividend_amount NUMERIC(24, 8),
        split_ratio NUMERIC(24, 8),
        dividend_type VARCHAR(128),
        source_system VARCHAR(64) NOT NULL DEFAULT 'akshare',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_ak_fund_dividend PRIMARY KEY (fund_code, dividend_date)
    )
    ''',
    '''
    CREATE TABLE IF NOT EXISTS ak_fund_manager (
        manager_name VARCHAR(255) NOT NULL,
        company_name VARCHAR(255),
        start_date DATE,
        manage_fund_count NUMERIC(24, 8),
        max_return NUMERIC(24, 8),
        source_system VARCHAR(64) NOT NULL DEFAULT 'akshare',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_ak_fund_manager PRIMARY KEY (manager_name, company_name)
    )
    ''',
    '''
    CREATE TABLE IF NOT EXISTS ak_fund_company (
        company_name VARCHAR(255) NOT NULL,
        company_code VARCHAR(64),
        established_date DATE,
        fund_count NUMERIC(24, 8),
        manager_count NUMERIC(24, 8),
        source_system VARCHAR(64) NOT NULL DEFAULT 'akshare',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_ak_fund_company PRIMARY KEY (company_name)
    )
    ''',
    '''
    CREATE TABLE IF NOT EXISTS ak_market_quote (
        market_type VARCHAR(32) NOT NULL,
        symbol VARCHAR(64) NOT NULL,
        trade_date DATE NOT NULL,
        open_price NUMERIC(24, 8),
        close_price NUMERIC(24, 8),
        high_price NUMERIC(24, 8),
        low_price NUMERIC(24, 8),
        volume NUMERIC(24, 8),
        turnover NUMERIC(24, 8),
        source_system VARCHAR(64) NOT NULL DEFAULT 'akshare',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_ak_market_quote PRIMARY KEY (market_type, symbol, trade_date)
    )
    ''',
    '''
    CREATE TABLE IF NOT EXISTS ak_index_quote (
        symbol VARCHAR(64) NOT NULL,
        trade_date DATE NOT NULL,
        open_price NUMERIC(24, 8),
        close_price NUMERIC(24, 8),
        high_price NUMERIC(24, 8),
        low_price NUMERIC(24, 8),
        volume NUMERIC(24, 8),
        turnover NUMERIC(24, 8),
        source_system VARCHAR(64) NOT NULL DEFAULT 'akshare',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_ak_index_quote PRIMARY KEY (symbol, trade_date)
    )
    ''',
    '''
    CREATE TABLE IF NOT EXISTS ak_macro_indicator (
        indicator_code VARCHAR(64) NOT NULL,
        indicator_name VARCHAR(255) NOT NULL,
        indicator_date DATE NOT NULL,
        indicator_value NUMERIC(24, 8),
        unit VARCHAR(64),
        source_system VARCHAR(64) NOT NULL DEFAULT 'akshare',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_ak_macro_indicator PRIMARY KEY (indicator_code, indicator_date)
    )
    ''',
    '''
    CREATE TABLE IF NOT EXISTS ak_factor_series (
        factor_code VARCHAR(64) NOT NULL,
        factor_name VARCHAR(255) NOT NULL,
        series_date DATE NOT NULL,
        series_value NUMERIC(24, 8),
        source_system VARCHAR(64) NOT NULL DEFAULT 'akshare',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT pk_ak_factor_series PRIMARY KEY (factor_code, series_date)
    )
    ''',
]
