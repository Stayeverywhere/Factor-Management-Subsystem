import psycopg2
conn = psycopg2.connect(host='127.0.0.1', port=54321, user='system', password='ysb18316923897', dbname='kingbase')
cur = conn.cursor()
cur.execute('SET search_path TO biz_factor')

indexes = [
    "CREATE INDEX IF NOT EXISTS idx_bfv_factor_date ON base_factor_value(base_factor_id, data_date DESC)",
    "CREATE INDEX IF NOT EXISTS idx_bfv_fund_factor ON base_factor_value(fund_code, base_factor_id)",
    "CREATE INDEX IF NOT EXISTS idx_dfv_factor_date ON derivative_factor_value(derivative_factor_id, data_date DESC)",
    "CREATE INDEX IF NOT EXISTS idx_sfv_factor_date ON style_factor_value(style_factor_id, data_date DESC)",
    "CREATE INDEX IF NOT EXISTS idx_nav_date ON ak_fund_nav(fund_code, trade_date DESC)",
    "CREATE INDEX IF NOT EXISTS idx_quote_date ON ak_market_quote(symbol, trade_date DESC)",
]

for idx in indexes:
    try:
        cur.execute(idx)
        print(f'OK: {idx[:50]}...')
    except Exception as e:
        print(f'ERR: {e}')

conn.commit()
cur.close()
conn.close()
print('索引创建完成')
