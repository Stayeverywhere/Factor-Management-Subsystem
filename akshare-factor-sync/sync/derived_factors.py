"""
量价衍生因子计算引擎
从 ak_market_quote / ak_index_quote 读取 OHLCV 行情数据，
计算各项量价衍生因子，结果存入 derivative_factor 和 derivative_factor_value。
"""
import uuid
import math
from datetime import datetime
import pandas as pd
import numpy as np
import psycopg2

DB = dict(host='127.0.0.1', port=54321, user='system', password='ysb18316923897', dbname='kingbase')

def get_conn():
    conn = psycopg2.connect(**DB)
    cur = conn.cursor()
    cur.execute('SET search_path TO biz_factor')
    return conn, cur

# ── 衍生因子定义 ──
DERIVED_FACTORS = [
    # (id, code, name, description, formula)
    ('df-daily-ret',    'daily_ret',    '日收益率',    'ret = close_t / shift(close,1) - 1', 'close'),
    ('df-ret-5',        'ret_5',        '5日累计收益率', 'ret_5 = close_t / shift(close,5) - 1', 'close'),
    ('df-ret-20',       'ret_20',       '20日累计收益率', 'ret_20 = close_t / shift(close,20) - 1', 'close'),
    ('df-ret-60',       'ret_60',       '60日累计收益率', 'ret_60 = close_t / shift(close,60) - 1', 'close'),
    ('df-ma-5',          'ma_5',        '5日移动均价',   'ma_5 = mean(close,5)', 'close'),
    ('df-ma-10',         'ma_10',       '10日移动均价',  'ma_10 = mean(close,10)', 'close'),
    ('df-ma-20',         'ma_20',       '20日移动均价',  'ma_20 = mean(close,20)', 'close'),
    ('df-ma-60',         'ma_60',       '60日移动均价',  'ma_60 = mean(close,60)', 'close'),
    ('df-amp-5',         'amp_5',       '5日价格振幅',   'amp_5 = (max(high,5)-min(low,5))/shift(close,5)', 'high,low,close'),
    ('df-amp-20',        'amp_20',      '20日价格振幅',  'amp_20 = (max(high,20)-min(low,20))/shift(close,20)', 'high,low,close'),
    ('df-to-avg-5',      'to_avg_5',    '5日均换手率',   'to_avg_5 = mean(turnover,5)', 'turnover'),
    ('df-to-avg-20',     'to_avg_20',   '20日均换手率',  'to_avg_20 = mean(turnover,20)', 'turnover'),
    ('df-vol-ratio',     'vol_ratio',   '量比',         'vol_ratio = volume / mean(volume,5)', 'volume'),
    ('df-amount-avg-5',  'amount_avg_5','5日均成交额',   'amount_avg_5 = mean(amount,5)', 'amount'),
    ('df-amount-avg-20', 'amount_avg_20','20日均成交额', 'amount_avg_20 = mean(amount,20)', 'amount'),
    ('df-mom-20',        'mom_20',      '20日动量因子',  'mom_20 = close_t / shift(close,20) - 1', 'close'),
    ('df-mom-60',        'mom_60',      '60日动量因子',  'mom_60 = close_t / shift(close,60) - 1', 'close'),
    ('df-mom-120',       'mom_120',     '120日动量因子', 'mom_120 = close_t / shift(close,120) - 1', 'close'),
    ('df-mom20-skip5',   'mom20_skip5', '标准动量(剔除1周)', 'mom20_skip5 = close_t / shift(close,25) - 1', 'close'),
    ('df-rev-5',         'rev_5',       '5日反转因子',   'rev_5 = -(close_t / shift(close,5) - 1)', 'close'),
    ('df-rsi-14',        'rsi_14',      'RSI相对强弱(14日)', 'RSI=100-100/(1+avg_gain/avg_loss)', 'close'),
    ('df-vol-20',        'vol_20',      '20日年化波动率', 'vol_20 = std(daily_ret,20)*sqrt(250)', 'daily_ret'),
    ('df-vol-60',        'vol_60',      '60日年化波动率', 'vol_60 = std(daily_ret,60)*sqrt(250)', 'daily_ret'),
    ('df-mdd-20',        'mdd_20',      '20日最大回撤',  'mdd_20 = 1-max(close,20)/min(close,20)', 'close'),
    ('df-mdd-60',        'mdd_60',      '60日最大回撤',  'mdd_60 = 1-max(close,60)/min(close,60)', 'close'),
    ('df-amihud',        'amihud',      'Amihud非流动性', 'amihud = abs(daily_ret)/amount', 'daily_ret,amount'),
]

def compute_all():
    conn, cur = get_conn()
    now = datetime.now()

    # 1. 创建衍生因子定义
    for df_id, code, name, desc, _ in DERIVED_FACTORS:
        cur.execute("""
            INSERT INTO derivative_factor (id, code, name, created_by, created_at, description, enabled)
            VALUES (%s, %s, %s, 'system', %s, %s, TRUE)
            ON CONFLICT (id) DO UPDATE SET code=EXCLUDED.code, name=EXCLUDED.name,
                description=EXCLUDED.description, enabled=TRUE
        """, (df_id, code, name, now, desc))
    conn.commit()
    print(f'已创建/更新 {len(DERIVED_FACTORS)} 个衍生因子定义')

    # 2. 清理旧计算结果
    cur.execute('DELETE FROM derivative_factor_value')
    conn.commit()
    print('已清理旧计算结果')

    # 3. 从行情表读取数据
    cur.execute("""
        SELECT market_type, symbol, trade_date, open_price, close_price,
               high_price, low_price, volume, turnover
        FROM ak_market_quote ORDER BY symbol, trade_date
    """)
    rows = cur.fetchall()
    if not rows:
        print('无行情数据，跳过计算')
        return

    df = pd.DataFrame(rows, columns=['market_type','symbol','trade_date',
                                      'open','close','high','low','volume','amount'])
    # 转数值类型（数据库返回 decimal.Decimal）
    for col in ['open','close','high','low','volume','amount']:
        df[col] = pd.to_numeric(df[col], errors='coerce')
    df['trade_date'] = pd.to_datetime(df['trade_date'])
    df = df.sort_values('trade_date').reset_index(drop=True)

    # 计算日收益率
    df['daily_ret'] = df['close'].pct_change()

    # 用 symbol 标识（我们只有 000001，但保留通用性）
    symbols = df['symbol'].unique()
    print(f'行情数据: {len(df)} 条, 标的: {symbols.tolist()}')

    inserted = 0
    for symbol in symbols:
        sdf = df[df['symbol'] == symbol].copy()
        sdf = sdf.sort_values('trade_date').reset_index(drop=True)

        n = len(sdf)
        results = {}  # col_name -> list of values (same length as sdf)
        for col in ['daily_ret']:
            results[col] = sdf[col].tolist()

        # 批量计算所有因子
        close = sdf['close'].values
        high = sdf['high'].values
        low = sdf['low'].values
        volume = sdf['volume'].values
        amount = sdf['amount'].values
        dates = sdf['trade_date'].tolist()
        ret = sdf['daily_ret'].values

        def rolling(arr, window, func):
            out = [None] * n
            for i in range(n):
                if i < window - 1:
                    continue
                out[i] = func(arr[i-window+1:i+1])
            return out

        def rolling_mean(arr, w):
            return rolling(arr, w, lambda x: float(np.nanmean(x)))

        def rolling_std(arr, w):
            return rolling(arr, w, lambda x: float(np.nanstd(x, ddof=1)))

        def rolling_max(arr, w):
            return rolling(arr, w, lambda x: float(np.nanmax(x)))

        def rolling_min(arr, w):
            return rolling(arr, w, lambda x: float(np.nanmin(x)))

        # 计算各因子值
        factor_values = {}
        for df_id, code, name, desc, depends in DERIVED_FACTORS:
            vals = [None] * n
            if code == 'daily_ret':
                vals = [float(v) if not (np.isnan(v) if isinstance(v, float) else False) else None for v in ret]
            elif code == 'ret_5':
                for i in range(5, n):
                    vals[i] = float(close[i] / close[i-5] - 1)
            elif code == 'ret_20':
                for i in range(20, n):
                    vals[i] = float(close[i] / close[i-20] - 1)
            elif code == 'ret_60':
                for i in range(60, n):
                    vals[i] = float(close[i] / close[i-60] - 1)
            elif code == 'ma_5':
                mv = rolling_mean(close, 5); vals = [float(v) if v else None for v in mv]
            elif code == 'ma_10':
                mv = rolling_mean(close, 10); vals = [float(v) if v else None for v in mv]
            elif code == 'ma_20':
                mv = rolling_mean(close, 20); vals = [float(v) if v else None for v in mv]
            elif code == 'ma_60':
                mv = rolling_mean(close, 60); vals = [float(v) if v else None for v in mv]
            elif code == 'amp_5':
                for i in range(5, n):
                    vals[i] = float((max(high[i-4:i+1]) - min(low[i-4:i+1])) / close[i-5])
            elif code == 'amp_20':
                for i in range(20, n):
                    vals[i] = float((max(high[i-19:i+1]) - min(low[i-19:i+1])) / close[i-20])
            elif code == 'to_avg_5':
                mv = rolling_mean(volume, 5); vals = [float(v) if v else None for v in mv]
            elif code == 'to_avg_20':
                mv = rolling_mean(volume, 20); vals = [float(v) if v else None for v in mv]
            elif code == 'vol_ratio':
                for i in range(5, n):
                    avg_v = float(np.nanmean(volume[i-4:i+1]))
                    vals[i] = float(volume[i] / avg_v) if avg_v > 0 else None
            elif code == 'amount_avg_5':
                mv = rolling_mean(amount, 5); vals = [float(v) if v else None for v in mv]
            elif code == 'amount_avg_20':
                mv = rolling_mean(amount, 20); vals = [float(v) if v else None for v in mv]
            elif code == 'mom_20':
                for i in range(20, n):
                    vals[i] = float(close[i] / close[i-20] - 1)
            elif code == 'mom_60':
                for i in range(60, n):
                    vals[i] = float(close[i] / close[i-60] - 1)
            elif code == 'mom_120':
                for i in range(min(120, n-1), n):
                    if i >= 120:
                        vals[i] = float(close[i] / close[i-120] - 1)
            elif code == 'mom20_skip5':
                for i in range(25, n):
                    vals[i] = float(close[i] / close[i-25] - 1)
            elif code == 'rev_5':
                for i in range(5, n):
                    vals[i] = -float(close[i] / close[i-5] - 1)
            elif code == 'rsi_14':
                for i in range(14, n):
                    gains = [ret[j] for j in range(i-13, i+1) if not np.isnan(ret[j]) and ret[j] > 0]
                    losses = [-ret[j] for j in range(i-13, i+1) if not np.isnan(ret[j]) and ret[j] < 0]
                    avg_gain = float(np.mean(gains)) if gains else 0
                    avg_loss = float(np.mean(losses)) if losses else 0.001
                    rs = avg_gain / avg_loss if avg_loss > 0 else 999
                    vals[i] = float(100 - 100 / (1 + rs))
            elif code == 'vol_20':
                for i in range(20, n):
                    s = float(np.nanstd(ret[i-19:i+1], ddof=1))
                    vals[i] = s * math.sqrt(250)
            elif code == 'vol_60':
                for i in range(60, n):
                    s = float(np.nanstd(ret[i-59:i+1], ddof=1))
                    vals[i] = s * math.sqrt(250)
            elif code == 'mdd_20':
                for i in range(20, n):
                    mx = max(close[i-19:i+1])
                    mn = min(close[i-19:i+1])
                    vals[i] = float(1 - mn / mx) if mx > 0 else None
            elif code == 'mdd_60':
                for i in range(60, n):
                    mx = max(close[i-59:i+1])
                    mn = min(close[i-59:i+1])
                    vals[i] = float(1 - mn / mx) if mx > 0 else None
            elif code == 'amihud':
                for i in range(1, n):
                    r = abs(ret[i]) if not np.isnan(ret[i]) else 0
                    a = amount[i] if not np.isnan(amount[i]) else 0
                    vals[i] = float(r / a) if a > 0 else None

            factor_values[df_id] = vals

        # 批量写入数据库
        batch = []
        for i in range(n):
            d = dates[i]
            if isinstance(d, pd.Timestamp):
                d = d.date()
            for df_id, code, name, desc, depends in DERIVED_FACTORS:
                v = factor_values.get(df_id, [None])[i]
                if v is not None and not (isinstance(v, float) and (np.isnan(v) or np.isinf(v))):
                    batch.append((uuid.uuid4().hex[:12], symbol, df_id, d, float(v), now))

            if len(batch) >= 10000:
                cur.executemany("""
                    INSERT INTO derivative_factor_value (id, fund_code, derivative_factor_id, data_date, value, calculated_at)
                    VALUES (%s, %s, %s, %s, %s, %s) ON CONFLICT (id) DO NOTHING
                """, batch)
                conn.commit()
                inserted += len(batch)
                batch = []

        if batch:
            cur.executemany("""
                INSERT INTO derivative_factor_value (id, fund_code, derivative_factor_id, data_date, value, calculated_at)
                VALUES (%s, %s, %s, %s, %s, %s) ON CONFLICT (id) DO NOTHING
            """, batch)
            conn.commit()
            inserted += len(batch)

    cur.close()
    conn.close()
    print(f'完成！共计算 {inserted} 条衍生因子值')

if __name__ == '__main__':
    compute_all()
