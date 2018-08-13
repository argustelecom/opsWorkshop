#!/bin/sh

DUMP_DIR=$PWD/dumpdir
CREATE_EXTENTIONS=$PWD/create_extentions.sql
CURDATE=`date +%Y%m%d_%H%M`
USAGE="Usage: $0 [list;dump;clone;create;restore;drop] params
command:
  list                                       - получить текущий список баз
  inituser password                          - создать системного пользователя argus_sys с указанным паролем password
  initdb   databasename                      - создать чистую базу данных databasename без схем данных argus ops
  initext  databasename                      - активировать на базе данных databasename расширения PostgreSQL (должно выполняться перед каждым обновлением БД)
  dump     databasename [filename]           - снять дамп с базы databasename в файл $DUMP_DIR/filename
  clone    from_databasename to_databasename - склонировать базу \"from\" в \"to\" + будет снят дамп $DUMP_DIR/from_databasename_datetime.sql
  create   databasename filename             - создать базу databasename из файла /my/filename или $DUMP_DIR/filename
  restore  databasename filename             - восстановить базу databasename из файла /my/filename или $DUMP_DIR/filename
  drop     databasename                      - удалить базу databasename"

init_user(){
    echo "Init argus_sys user"
    local PASS=$1
    echo "  create user"
    su - postgres -c "psql -U postgres -h 127.0.0.1 -c \"CREATE ROLE argus_sys WITH LOGIN CREATEDB CREATEROLE PASSWORD '$PASS';\"" 2>&1; [ $? -ne 0 ] && exit 1;
    echo "done"
}

init_db(){
    echo "Init DB"
    local DB_NAME=$1
    echo "  databasename=$DB_NAME"
    su - postgres -c "psql -U postgres -h 127.0.0.1 -c \"CREATE DATABASE $DB_NAME;\"" 2>&1; [ $? -ne 0 ] && exit 1;
    su - postgres -c "psql -U postgres -h 127.0.0.1 -d $DB_NAME -c \"ALTER DATABASE $DB_NAME OWNER TO argus_sys;\"" 2>&1; [ $? -ne 0 ] && exit 1;
    su - postgres -c "psql -U postgres -h 127.0.0.1 -d $DB_NAME -c \"GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO argus_sys;\"" 2>&1; [ $? -ne 0 ] && exit 1;
    su - postgres -c "psql -U postgres -h 127.0.0.1 -d $DB_NAME -c \"CREATE SCHEMA IF NOT EXISTS dbm AUTHORIZATION argus_sys;\"" 2>&1; [ $? -ne 0 ] && exit 1;
    init_db_extensions $DB_NAME
    echo "done"
}

init_db_extensions(){
    echo "Init DB extensions"
    local DB_NAME=$1
    echo "  databasename=$DB_NAME"
    su - postgres -c "psql -U postgres -h 127.0.0.1 -d $DB_NAME -c \"ALTER DATABASE $DB_NAME SET search_path = pg_catalog, public, system;\"" 2>&1; [ $? -ne 0 ] && exit 1;
    su - postgres -c "psql -U postgres -h 127.0.0.1 -d $DB_NAME -c \"ALTER ROLE argus_sys SET search_path = pg_catalog, public, system;\"" 2>&1; [ $? -ne 0 ] && exit 1;
    su - postgres -c "psql -U postgres -h 127.0.0.1 -d $DB_NAME < $CREATE_EXTENTIONS" 2>&1; [ $? -ne 0 ] && exit 1;
    echo "done"
}

dump_db(){
    echo "Dump DB"
    local DB_NAME=$1
    local DUMP_FILE=$2
    echo "  run pg_dump"
    su - postgres -c "pg_dump -U postgres -h 127.0.0.1 -F plain -f $DUMP_FILE $DB_NAME" 2>&1
    echo "DB dump file is created: $DUMP_FILE"
}

drop_db(){
    echo "Drop DB"
    local DB_NAME=$1
    echo "  run dropdb"
    su - postgres -c "dropdb -U postgres -h 127.0.0.1 --if-exists $DB_NAME" 2>&1
    echo "done"
}

create_db(){
    echo "Create DB"
    local DB_NAME=$1
    local DUMP_FILE=$2

    echo "  run create database"; su - postgres -c "psql -U postgres -h 127.0.0.1 -c \"create database $DB_NAME;\"" 2>&1; [ $? -ne 0 ] && exit 1;
    echo "  run create load from dump"; su - postgres -c "psql -U postgres -h 127.0.0.1 -d $DB_NAME < $DUMP_FILE" 2>&1; [ $? -ne 0 ] && exit 1;
    echo "  run create alter owner"; su - postgres -c "psql -U postgres -h 127.0.0.1 -d $DB_NAME -c \"ALTER DATABASE $DB_NAME OWNER TO argus_sys;\"" 2>&1; [ $? -ne 0 ] && exit 1;
    echo "  run create grant privs"; su - postgres -c "psql -U postgres -h 127.0.0.1 -d $DB_NAME -c \"GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO argus_sys;\"" 2>&1; [ $? -ne 0 ] && exit 1;
    init_db_extensions $DB_NAME
    echo "done"
}

list_db(){
    su - postgres -c "psql -U postgres -h 127.0.0.1 -c \"SELECT db.datname AS dbname, count(sa.datname) AS connections, string_agg(DISTINCT sa.client_addr::varchar,', ') clientip FROM pg_database db LEFT JOIN pg_stat_activity sa ON sa.datname = db.datname GROUP BY db.datname ORDER BY 1;\""
}

case "$1" in
    inituser)
        if [ -z $2 ]
        then
          echo "$USAGE"
          exit 1;
        fi

        USER_PASS=$2
        init_user $USER_PASS
    ;;
    initdb)
        if [ -z $2 ]
        then
          echo "$USAGE"
          exit 1;
        fi

        DB_NAME=$2
        init_db $DB_NAME
    ;;
    initext)
        if [ -z $2 ]
        then
          echo "$USAGE"
          exit 1;
        fi

        DB_NAME=$2
        init_db_extensions $DB_NAME
    ;;
    dump)
        if [ -z $2 ]
        then
          echo "$USAGE"
          exit 1;
        fi
        SOURCE_DB_NAME=$2

        if [ -z $3 ]
        then
            DUMP_NAME=${DUMP_DIR}/${SOURCE_DB_NAME}_${CURDATE}.sql
        else
            DUMP_NAME=${DUMP_DIR}/${3%.sql}.sql
        fi

        dump_db $SOURCE_DB_NAME $DUMP_NAME
    ;;
    clone)
        if [ -z $2 ]
        then
          echo "$USAGE"
          exit 1;
        fi
        SOURCE_DB_NAME=$2

        if [ -z $3 ]
        then
          echo "$USAGE"
          exit 1;
        fi
        TARGET_DB_NAME=$3

        DUMP_NAME=${DUMP_DIR}/${SOURCE_DB_NAME}_${CURDATE}.sql

        dump_db $SOURCE_DB_NAME $DUMP_NAME
        drop_db $TARGET_DB_NAME
        create_db $TARGET_DB_NAME $DUMP_NAME
    ;;
    create)
        if [ -z $2 ]
        then
          echo "$USAGE"
          exit 1;
        fi
        TARGET_DB_NAME=$2

        if [ -z $3 ]
        then
          echo "$USAGE"
          exit 1;
        fi

        if [ -e $3 ]
        then
            #DUMP_NAME must be absolute path!
            if [[ "${3:0:1}" == "/" ]]
            then
                #abs path
                DUMP_NAME=$3
            else
                #rel path
                DUMP_NAME=$(pwd)/$3
            fi
        else
            if [ -e ${DUMP_DIR}/$3 ]
            then
                DUMP_NAME=${DUMP_DIR}/$3
            else
                echo "Error: File $3 or ${DUMP_DIR}/$3 not found."
                exit 1;
            fi
        fi

        drop_db $TARGET_DB_NAME
        create_db $TARGET_DB_NAME $DUMP_NAME
    ;;
    restore)
        if [ -z $2 ]
        then
          echo "$USAGE"
          exit 1;
        fi
        TARGET_DB_NAME=$2

        if [ -z $3 ]
        then
          echo "$USAGE"
          exit 1;
        fi

        if [ -e $3 ]
        then
            #DUMP_NAME must be absolute path!
            if [[ "${3:0:1}" == "/" ]]
            then
                #abs path
                DUMP_NAME=$3
            else
                #rel path
                DUMP_NAME=$(pwd)/$3
            fi
        else
            if [ -e ${DUMP_DIR}/$3 ]
            then
                DUMP_NAME=${DUMP_DIR}/$3
            else
                echo "Error: File $3 or ${DUMP_DIR}/$3 not found."
                exit 1;
            fi
        fi

        drop_db $TARGET_DB_NAME
        create_db $TARGET_DB_NAME $DUMP_NAME
    ;;
    drop)
        if [ -z $2 ]
        then
          echo "$USAGE"
          exit 1;
        fi
        TARGET_DB_NAME=$2

        drop_db $TARGET_DB_NAME
    ;;
    list)
        list_db
    ;;
    *) # other params
        echo "$USAGE"
        exit 1;
    ;;
esac

