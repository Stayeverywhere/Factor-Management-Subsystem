from pathlib import Path

# 项目根目录
BASE_DIR = Path(__file__).parent.parent

# ========== 金仓数据库配置 ==========
DB_CONFIG = {
    "dialect": "kingbase",        # 金仓数据库方言
    "driver": "kingbase8",        # 金仓SQLAlchemy驱动
    "host": "127.0.0.1",          # 金仓数据库IP地址
    "port": 54321,                 # 金仓默认端口
    "user": "system",             # 数据库账号
    "password": "ysb18316923897", # 数据库密码
    "database": "kingbase",       # 业务数据库名称
    "schema": "biz_factor"        # 业务Schema
}