from __future__ import annotations

from sync.base import ensure_core_schema


def main() -> None:
    ensure_core_schema()
    print('Database initialized successfully.')


if __name__ == '__main__':
    main()
