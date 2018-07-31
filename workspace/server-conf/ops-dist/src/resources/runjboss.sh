#!/bin/bash
#
#------------------------------------------------
# Скрипт управления ARGUS Enterprise server
# (c) НТЦ Аргус, Санкт-Петербург, 2015г.
#------------------------------------------------
#
# Поддерживает следующие аргументы:
#
# ./runjboss.sh
#       start   <mode>          - запуск сервера приложений, mode - режим запуска (standalone или domain). По умолчанию standalone.
#       stop    <kill>          - остановка сервера приложений. По умолчанию "мягкая остановка". При указании <kill> убивает процесс сервера 
#					и все его дочерние процессы.
#       restart                 - перезапуск сервера в том же режиме, в каком он был запущен ранее.
#       alert-restart           - аварийный перезапуск сервера (снимается дополнительная отладочная информация для последующей диагностики 
#					сотрудниками НТЦ "Аргус").
#       status                  - отображение текущего состояния сервера приложений (запущен/остановлен).
#		heap-dump				- выгружает дамп кучи в файл 'heap_dumpfile_<дата>.<номер процесса>'
#		thread-dump				- выгружает дамп потоков в стандартный вывод
#
# Примеры:
#       ./runjboss.sh start standalone                  - запуск сервера в режиме standalone
#       ./runjboss.sh stop kill                         - убивает ранее запущенный процесс сервера и все его дочерние
#       ./runjboss.sh config /home/developer/props      - переконфгурирование сервера на основе файлов work.properties и
#                                                         my.properties из директории /home/developer/props
#	./runjboss.sh thread-dump >> threads_dump  	- выгрузка дампа всех потоков в файл 'threads_dump'
#

# По-умолчанию используется системная версия Java.
# Пример: export JAVA_HOME=/usr/lib/jvm/java-7-oracle
JAVA_HOME=

if [ -z $JAVA_HOME ]; then
	echo "ERROR: 'JAVA_HOME' path is not defined."
	exit 1
else
	JAVA_BIN=$JAVA_HOME/bin
fi

export JBOSS_HOME=/jboss_prod
export JBOSS_USER=developer

export JBOSS_PIDFILE=$JBOSS_HOME/bin/wildfly.pid
export JBOSS_CONFIG=standalone.xml
export JBOSS_SCRIPT=$JBOSS_HOME/bin/standalone.sh
prog='wildfly'
properties=('work.properties' 'my.properties');

get_offset() {
	# Offset is used only in standalone mode.
	read mode < mode
	offset=""
	if [ "$mode" == "standalone" ]; then
		offset=$(grep "property\ name\=\"jboss\.socket\.binding\.port\-offset\"" ${JBOSS_HOME}/standalone/configuration/standalone.xml | grep -o "value\=\".*\"" | tr -d "\"" | awk -F"=" '{print $2}')
	elif [ -z "$mode" ]; then
		echo "WARNING: 'mode' file is empty. Starting in standalone mode..."
	fi
}

start() {
        if [ -f $JBOSS_PIDFILE ]; then
                read ppid < $JBOSS_PIDFILE
                if [[ `ps --pid $ppid 2> /dev/null | grep -c $ppid 2> /dev/null` -eq '1' ]]; then
                        echo "$prog is already running."                         
        		exit 1        	
        	fi
        fi

	if [ ! -z $1 ]; then
		mode="$1"
		if [ "$mode" != "domain" ] && [ "$mode" != "standalone" ]; then
			echo "ERROR: unsupported mode ($mode)"
			exit 1
		fi
		export JBOSS_MODE="$mode"
		echo "Starting $prog in ${mode} mode..."
		$JBOSS_HOME/bin/${mode}.sh > ${JBOSS_HOME}/standalone/log/native_stdout.log.`date +%Y-%m-%d-%H-%M` 2>&1 &
		echo $! > $JBOSS_PIDFILE
		echo "$mode" > mode
	else
		export JBOSS_MODE="standalone"
		# nohup ./standalone.sh > ./startlog.log 2>&1 &
		echo "Starting $prog in default mode ($JBOSS_MODE)..."
	        $JBOSS_HOME/bin/standalone.sh > ${JBOSS_HOME}/standalone/log/native_stdout.log.`date +%Y-%m-%d-%H-%M` 2>&1 &
		# $!    - PID последнего, запущенного в фоне, процесса
        	echo $! > $JBOSS_PIDFILE
		echo "standalone" > mode
	fi
}

stop() {
	if [ -f $JBOSS_PIDFILE ]; then
		if [ ! -z $1 ] && [ "$1" == "kill" ]; then
			echo "Killing $prog..."
			read ppid < $JBOSS_PIDFILE
			[ -z $ppid ] && echo "[ERROR] Unable to get $prog pid, $JBOSS_PIDFILE is empty!" && exit 1
			ppid_children=($(pgrep -P ${ppid}))
			for k in ${ppid_children}
			do
				kill -9 $k && echo "Killed child pid $k"
			done
			kill -9 $ppid && echo "Killed parent pid $ppid"
			rm $JBOSS_PIDFILE && echo "Done." || echo "[ERROR] Unable to delete pid file!" || exit 1
			
		else
			echo "Stopping $prog: "
                	# Lets show errors if stop fails
                        read ppid < $JBOSS_PIDFILE
			[ -z $ppid ] && echo "[ERROR] Unable to get $prog pid, $JBOSS_PIDFILE is empty!" && exit 1
                        pkill -TERM -P $ppid
			[ "$?" == "0" ] && rm $JBOSS_PIDFILE && echo "Done." || echo "[ERROR] Unable to delete pid file" || exit 1
		fi
	else	
		pid_name=$(basename "$JBOSS_PIDFILE")
               	echo "ERROR: pid-file not found ($pid_name)!"
	        echo "Unable to stop $prog."
	        exit 1
        fi

}

alert_restart() {
	if [ -f $JBOSS_PIDFILE ]; then
		read pid_name < $JBOSS_PIDFILE
		child_procs=($(pgrep -P ${pid_name}))
		echo "Saving threads dump..."
		DATE=`date +%Y-%m-%d-%H_%M_%S`
		for i in ${child_procs[@]}
		do
			$JAVA_BIN/jstack $i >> $JBOSS_HOME/standalone/log/dumpfile_${i}-${DATE}.txt
        	        [ $? -ne 0 ] && echo "ERROR: error while saving dump!" && exit 1 || echo "Done. Child process dump is saved to dumpfile_${i}-${DATE}.txt"
		done
		echo "Backing up access.log ..."
		cp $JBOSS_HOME/standalone/log/access.log $JBOSS_HOME/standalone/log/accesslog_backup_${DATE}.log 2>/dev/null && echo "Done." || echo "WARNING: access.log not found, skipping."
		$0 stop && echo "Server stopped."
		sleep 15
		$0 start && echo "Server started."
	else
		echo "PID-file not found:  $JBOSS_PIDFILE"
                echo "ERROR: Unable to restart $prog "
                exit 1
	fi
		
}

status() {	
	if [ -f $JBOSS_PIDFILE ]; then
                read ppid < $JBOSS_PIDFILE
                if [ `ps --pid $ppid 2> /dev/null | grep -c $ppid 2> /dev/null` -eq '1' ]; then
                        echo "$prog started (pid $ppid)"
                        return 0
                else
                        echo "ERROR: $prog is not started, but pid-file exists."
                        return 1
                fi
        fi
        echo "$prog not started"
        return 3   
}

heap_dump () {
	if [ -f $JBOSS_PIDFILE ]; then
                read ppid < $JBOSS_PIDFILE
		child_procs=($(pgrep -P ${ppid}))
                echo "Saving heap dump..."
		DATE=`date +%Y-%m-%d-%H_%M_%S`
                for i in ${child_procs[@]}
                do
			$JAVA_BIN/jmap -dump:live,file=heap_dumpfile_${i}-${DATE}.${i} $i
			[ $? -ne 0 ] && echo "ERROR: error while saving dump!" && exit 1 || echo "Done. Heap dump saved to heap_dumpfile_${i}-${DATE}.${i}"
		done
	else
		echo "PID-file not found:  $JBOSS_PIDFILE ?"
                echo "ERROR: Unable to run heap dump"
                exit 1
	fi
}

thread_dump () {
        if [ -f $JBOSS_PIDFILE ]; then
                read ppid < $JBOSS_PIDFILE
                child_procs=($(pgrep -P ${ppid}))
                echo "Saving threads dump..."
                for i in ${child_procs[@]}
                do
			$JAVA_BIN/jstack $i
                done
        else
                echo "PID-file not found:  $JBOSS_PIDFILE ?"
                echo "ERROR: Unable to run thread dump"
                exit 1
        fi
}


case "$1" in
        start)
                start $2
                ;;
        stop)
                stop $2
                ;;
        restart)
                $0 stop
		sleep 15
		read mode < mode
                $0 start $mode
                ;;
	alert-restart)
		alert_restart
		;;
        status)
                status
                ;;
	heap-dump)
		heap_dump
		;;
	thread-dump)
		thread_dump
		;;
        *)
                ## If no parameters are given, print which are avaiable.
                echo "Usage: $0 {start|stop|restart|alert-restart|status|config|heap-dump|thread-dump}"
                exit 1
                ;;
esac

