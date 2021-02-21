#!/usr/bin/env bash

SHELL_FOLDER=$(cd "$(dirname "$0")";pwd)

echo "执行pg 初始化"
export PGPASSWORD=123456

psql -h pg-master.base-service -p 5432 -U postgres -f "$SHELL_FOLDER"/create_database.sql
psql -h pg-master.base-service -p 5432 -U postgres -f "$SHELL_FOLDER"/create_role.sql
psql -h pg-master.base-service -p 5432 -U postgres -f "$SHELL_FOLDER"/tables.sql demo
psql -h pg-master.base-service -p 5432 -U postgres -f "$SHELL_FOLDER"/data.sql demo
psql -h pg-master.base-service -p 5432 -U postgres -d demo -f "$SHELL_FOLDER"/grant.sql