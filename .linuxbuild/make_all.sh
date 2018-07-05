#!/bin/bash

# TASK-86433 v.semchenko: этот скрипт используется тольуо в Gitlab CI
# 
# Modes: build-server, make-distrib, ui-tests, exists-errors-in-logs, install-update-db
#	build-server:
#		-- валидация pom.xml проектов
#		-- сборка проектов с прогоном unit-тестов		
#	make-distrib:
#       -- подготовка дистрибутива
#       -- если в окружении переменная autotag=true, выставляем тег.
#	build-update-db:
#		-- сборка проекта db
#	install_update_db:
#		-- подготовка БД и установка обновления БД
#	ui-tests:
#		-- ui-тесты на базе собранного дистриба СП и обновлений БД
#	exists-errors-in-logs
#		-- проверка наличия ошибок в логе workspace/target/tests-out.log
#

#################
# Script config #
#################
cd ../workspace

WORKSPACE=`pwd`
pathDB="${WORKSPACE}/../db"
b_err='Build ERROR!'

error_all () {
	# Throw error both to logs
	# Call like this:
	# error_all "Unable to do this kind of stuff"
	printf "\n%s\n" "ERROR: $1"
	exit 1
}

####################
# Modes definition #
####################

build_server () {
	
	echo ' --- Build projects server --- '
	cd $WORKSPACE
	echo ' -- Strategy: 1) Validate pom.xml all projects, 2) Build projects and unit-tests, 3) Generate report -- '
	echo ' -- 1) Validate pom.xml all projects -- '
	echo ' -- Invoking mvn xml:validate -pl parent -amd -- '
	mvn xml:validate -pl parent -amd
	[ $? -ne 0 ] && error_all "1) Validate pom.xml all projects: validate xml failed"

	echo ' -- 2) Build projects and unit-tests -- '
	# создаем workspace/target каталог для общего лога выполнения тестов
	if [ ! -d $WORKSPACE/target ]; then
		mkdir -p $WORKSPACE/target
		[ $? -ne 0 ] && error_all "2) Build projects and unit-tests: create target failed: $WORKSPACE/target"
	fi
	echo ' -- Invoking mvn install | tee $WORKSPACE/target/tests-out.log -- '
	mvn install | tee $WORKSPACE/target/tests-out.log
	# не работает, ошибку сборки проектов, будем ловить в exists_errors_in_logs
	#	[ $? -ne 0 ] && error_all "2) Build projects and unit-tests: install failed"
	exists_errors_in_logs

	echo ' -- 3) Generate report -- '
	echo ' -- Invoking mvn surefire-report:report-only -Daggregate=true -DshowSuccess=false -- '
	mvn surefire-report:report-only -Daggregate=true -DshowSuccess=false
	[ $? -ne 0 ] && error_all "3) Generate report: generate failed"

	echo " -- Result unit-tests -- "
	exists_errors_in_logs
}

make_distrib () {

    cd $WORKSPACE
    echo ' --- Making disrib --- '
    echo ' -- Invoking mvn install -DskipTests -- '
    mvn install -e -DskipTests 
    [ $? -ne 0 ] && error_all "Build projects: install failed"
    cd $WORKSPACE/server-conf/box-dist
    echo 'making dist'
    echo ' -- Invoking mvn install -DskipTests -- '
    mvn clean install -e
    [ $? -ne 0 ] && error_all "Build dist: install failed"

    cd $WORKSPACE/server-conf/box-dist/target
    echo 'add build-number in name ditrib'
    # TASK-86433, v.semchenko:Добавим в имя дистриба buildnumber
    local build_number=`sed -n 's/argus\.app\.build-number=//p' ${WORKSPACE}/my.properties`
    local source_version=`sed -n 's/box\.app\.version=//p' ${WORKSPACE}/work.properties`
    # TASK-86433, Исходное имя в соответствии с argus.dist.final-name из dist/pom.xml
    local source_name="argusbox-dist-${source_version}.jar"
    local new_name="argusbox-dist-${build_number}.jar"
    mv -f $source_name $new_name
    [ $? -ne 0 ] && error_all "Build dist: rename $PWD/$source_name failed"

    #Если если необходимо, то создаем тег в git
    if [[ $autotag == "true" ]]; then 
        local URL_TAGS="http://gitlab/api/v3/projects/${CI_PROJECT_ID}/repository/tags"
        local data_tag="tag_name=build-${build_number}&ref=${CI_COMMIT_SHA}"
        echo "Выполняем: curl -H 'PRIVATE-TOKEN: YPFuigzczEj9wWs5VSvp' -X POST -d ''"$data_tag"'' $URL_TAGS"
        curl -H 'PRIVATE-TOKEN: YPFuigzczEj9wWs5VSvp' -X POST -d ''"$data_tag"'' $URL_TAGS
        [ $? -ne 0 ] && error_all "Build dist: set tag with build-number"
    fi
}

build_update_db () {

	cd $pathDB
	echo ' --- Build project db --- '
	echo ' -- Invoking mvn install -- '
	mvn install
	[ $? -ne 0 ] && error_all "Build project db: install failed"
}

ui_tests () {
	# TASK-86264, v.semchenko: Составляем имя дистриба. 
	local build_number=`sed -n 's/argus\.app\.build-number=//p' ${WORKSPACE}/my.properties`
	echo ' --- Starting build process (ui-tests mode) for all modules --- '
   	local name_distr="argusbox-dist-${build_number}.jar"

	echo ' -- Strategy: 1)Install application server, 2)Start server, 3)Perform ui-integration tests, 4)Stop server -- '
	echo ' -- 1) Install application server -- '
    
	if [ ! -f "$WORKSPACE/server-conf/box-dist/target/$name_distr" ]; then
    	echo "Not found $name_distr in directory $WORKSPACE/server-conf/box-dist/target"
		error_all "1) Install application server: not found distrib"
	else 
		echo "Found distrib $name_distr"
	fi

	cd $WORKSPACE/server-conf/box-dist/target
	java -jar $name_distr -options $WORKSPACE/my.properties
	[ $? -ne 0 ] && error_all "1) Install application server failed"

	echo ' -- 2) Start server -- '

	cd $WORKSPACE
	echo ' --- Yet another build of the project (Gitlab, fuck your agents!!!) --- '
	mvn install -e -DskipTests

	cd $WORKSPACE/server-app/inf-modules/webui
	if [ "$distComposition" == "" ]; then
		mvn pre-integration-test -Pbefore-ui-tests-build-start-appserver
		[ $? -ne 0 ] && error_all "2) Start server"
	fi

	echo ' -- 3) Performing ui-integration tests -- '
	cd $WORKSPACE/server-app
	#TASK-79874, v.semchenko: поиск от текущего каталога каталогов с постфиксом *-ui и преобразуем данные в строку (имена проектов через запятую без пробелов)
	modules=$(find . -type d -print | grep "/*\-ui$" | sed 's/\.\///' |  tr '\n' ' ' | sed 's/[ \t]/,/g' | sed 's/,$//');
	cd $WORKSPACE/server-app/inf-modules/webui
	# TASK-89021, v.semchenko: Редактируем список ui-module в соответсвии с указанным argus.security.login-module
	# результат записывается в target/tmpListModules.txt
	mvn initialize -DlistModules=$modules -Pprepare-list-ui-modules
	[ $? -ne 0 ] && error_all "3) Performing ui-integration tests: error prepare list ui-modules "
	if [ ! -f "$WORKSPACE/server-app/inf-modules/webui/target/tmpListModules.txt" ]; then
    	echo "Not found tmpListModules.txt in directory $WORKSPACE/server-app/inf-modules/webui/target"
		error_all "3) performing ui-integration tests: not found tmpListModules.txt" 
	else
		modules=`sed -n 's/modules=//p' $WORKSPACE/server-app/inf-modules/webui/target/tmpListModules.txt`
	fi
	echo cleaning ui-modules: $modules
	cd $WORKSPACE/server-app
	mvn clean -pl $modules
	[ $? -ne 0 ] && error_all "3) clean failed"
	if [ ! -d $WORKSPACE/target ]; then
		mkdir -p $WORKSPACE/target
		[ $? -ne 0 ] && error_all "3)create target failed: $WORKSPACE/target"
	fi
	mvn integration-test -pl $modules -Pui-tests | tee $WORKSPACE/target/tests-out.log
	# не работает, ошибку сборки проектов, будем ловить в exists_errors_in_logs
	#[ $? -ne 0 ] && error_all "3) ui-integration-test execution failed"

	echo ' -- 4) stop server -- '
	cd $WORKSPACE/server-app/inf-modules/webui
	mvn initialize -Pafter-ui-tests-build-stop-appserver
	[ $? -ne 0 ] && error_all "4) Stop server"

	echo "-- 5) Prepare server logs and create report ui-tests --"
	echo "-- Prepare server logs --"
	installPath=`sed -n 's/INSTALL_PATH=//p' ${WORKSPACE}/my.properties`
	cd $installPath/standalone
	tar -cf server-logs.tar log
	gzip  server-logs.tar
	[ $? -ne 0 ] && error_all "5) prepare server logs: pack logs in zip-archive failed"
	if [ ! -d $WORKSPACE/target ]; then
		mkdir -p $WORKSPACE/target
		[ $? -ne 0 ] && error_all "5)create target failed: $WORKSPACE/target"
	fi
	mv server-logs.tar.gz $WORKSPACE/target/server-logs.tar.gz
	[ $? -ne 0 ] && error_all "5) prepare server logs: move zip-archive failed"

	echo "-- Create report ui-tests --"	
	cd $WORKSPACE
    mvn surefire-report:failsafe-report-only -Daggregate=true -DshowSuccess=false
	[ $? -ne 0 ] && error_all "5) create report ui-tests: create report failed"

	echo " -- Result ui-tests -- "
	exists_errors_in_logs
}

exists_errors_in_logs () {
	# TASK-86433 v.semchenko: проверяем наличие в логах выполнения ui-tests хоть одной строки, где есть failure или error. Пример строки: 
	# Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
	# cd $WORKSPACE/target
	
	sumfail=$( grep "Tests run: " $WORKSPACE/target/tests-out.log | awk -F ",|:" 'BEGIN {OFS="\n"} {print $4}' | awk '{sum+=$1} END {print sum}')
	if [ $sumfail -ne 0 ]; then
		echo "[INFO] One or few failures in tests!"
		exit 1;
	fi

	sumerr=$( grep "Tests run: " $WORKSPACE/target/tests-out.log | awk -F ",|:" 'BEGIN {OFS="\n"} {print $6}' | awk '{sum+=$1} END {print sum}')
	if [ $sumerr -ne 0 ]; then
		echo "[INFO] One or few errors in tests!"
		exit 1;
	fi

	result_build=$( grep "\[INFO\] BUILD SUCCESS" $WORKSPACE/target/tests-out.log )
	if [ ! "$result_build" == "[INFO] BUILD SUCCESS" ]; then
		echo "[INFO] BUILD FAILURE"
		exit 1;
	fi 
	
	echo "[INFO] Not exists failures or errors in tests." 
}

install_update_db () {
	cd $pathDB/target/dbm
	echo ' --- Install update database --- '
    dos2unix $PWD/$DBM_FILE && $PWD/$DBM_FILE updateDatabase
	[ $? -ne 0 ] && error_all "2) Install update database "
}

case "$1" in
	build-server)
		build_server
		;;
	make-distrib)
		make_distrib
		;;
	build-update-db)
		build_update_db
		;;
	install-update-db)
		install_update_db
		;;
	ui-tests)
		ui_tests 
		;;
	ui-tests-pa)
		ui_tests
		;;
	exists-errors-in-logs)
		exists_errors_in_logs
		;;
	*)
		echo "Usage: $0 {build-server|make-distrib|build-update-db|install-update-db|ui-tests|exists-errors-in-logs}"
		exit 1
		;;
esac

echo "Build SUCCESS!"

cd $BUILDDIR
