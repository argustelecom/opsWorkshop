#!/bin/bash
# Script to build Argus Application Server BOX in Linux environment
# !!!Выполняется из каталога .linuxbuild 
# TASK-87917, v.semchenko (31.10.2017): скрипт используем только для подготовки СП и обновления к БД.
# ВАЖНО!!! Если адаптируешь еще для чего-то скрипт, укажи когда и где он используется!!!

export BUILDDIR=`pwd`
WORKSPACE="../workspace"
b_err="Build ERROR!"

# Завершение работы build
end_build () {
	
	# Если сборка ui-tests, то не следует оставлять после себя активные процессы
	if [ $mode == "ui-tests" ] || [ $mode == "ui-tests-pa" ]; then
		# Если процесс СП все еще висит нужно его грохнуть, иначе gitlab-runner не завершает работу
		pidRunAppServer=$(ps -ef | grep 'gitlab-runner' | grep "${branch}/${wildflyPackage}" | awk -F " " '{print $2}')
		# Ищем СП personal area и завершаем его процесс
		pidRunAppServerPA=$(ps -ef | grep 'gitlab-runner' | grep "${branch}/${wildflyPackage}-PA" | awk -F " " '{print $2}')
		if [ ! "$pidRunAppServerPA" == "" ]; then
			kill $pidRunAppServerPA;
		fi
		if [ ! "$pidRunAppServer" == "" ]; then
			kill $pidRunAppServer;
		fi

		echo "Stopping current Xvfb session";
		/etc/init.d/xvfb_start_script.sh stop $buildcounter $branch
		[ $? -ne 0 ] && error_all "error: xvfb_start_script.sh stop $buildcounter $branch"

		# Make sure all X sessions are killed
		echo "Killing all virtual framebuffers"
		killall Xvfb || true
		rm -Rf $HOME/xvfb/*
	fi

	cd $BUILDDIR;
}

# При получении ошибки
error_all () {
	# Throw error both to logs
	# Call it like this:
	# error_all "Unable to do this kind of stuff"
	printf "\n%s\n" "ERROR: $1"
	end_build
	exit 1
}

# Проверка версии Java
echo "Checking version Java"	
version=$("$JAVA_HOME"/bin/java -version 2>&1 | awk -F '"' '/version/ {print $2}')
echo "java version $version"
if [[ "$version" < "1.8" ]]; then
	error_all "Error: version JAVA < 1.8"
else
	echo "JAVA_HOME=$JAVA_HOME . OK!"
fi

# Останавливаем сервисы xvfb если они по какой-то причине остались с прошлой сборки
# для инфомраци: не обладаем правами закрыть процессы другого пользователя
echo "Getting rid of traces left by Xvfb"
killall Xvfb || true
rm -Rf $HOME/xvfb/*

echo " --- Setting up build environment --- "	
# Setting up local maven repo for this specific version (branch).
export MAVEN_OPTS="-Xmx768m -Dfile.encoding=UTF-8 -Dmaven.repo.local=${HOME}/maven_repo/${branch}"

# Prepare INSTALL_PATH
wildflyVars=(wildflyVersion wildflyPackage installPath)
# TASK-86433, v.semchenko (25.08.2017): версию берем из work.properties настройки argus.teamcity.server-package
wildflyVersion=`sed -n 's/argus\.teamcity\.server\-package=wildfly\-//p' ${WORKSPACE}/work.properties`
wildflyPackage="wildfly-${wildflyVersion}"
installPath="${HOME}/servers/$branch/$wildflyPackage/"

# Check wildfly var for installpath
for var in ${wildflyVars[@]}; do
   if [ -z "${!var}" ]; then
      error_all "Variable $var is not set"
   fi
done

cd $BUILDDIR
if [ ! -d $installPath ]; then
    echo "WildFly directory does not exist, will create when install distrib."
else
	# на всяки случай чистим каталог будущей установки 
	echo "WildFly directory exists. Cleaning directory."
	rm -rf $installPath/*
	[ $? -ne 0 ] && error_all "Unknown error"
fi

# Prepare configurations workspace/my.properties
echo "Prepare configuration $WORKSPACE/my.properties"
echo "INSTALL_PATH=$installPath" | cat > $WORKSPACE/my.properties
echo 'argus.app.memory.max-size=3600' | cat >> $WORKSPACE/my.properties
echo 'argus.app.debug-mode.enabled=true' | cat >> $WORKSPACE/my.properties
# на хосте также есть агент teamcity с СП под ui-теcты, для перестраховки взял смещение 10
echo "jboss.socket.binding.port-offset=10" | cat >> $WORKSPACE/my.properties
majorVersion=`sed -n 's/box\.app\.version=//p' ${WORKSPACE}/work.properties`
echo "argus.app.build-number=$majorVersion.$CI_PIPELINE_ID" | cat >> $WORKSPACE/my.properties
echo "jboss.bind.address=127.0.0.1" | cat >> $WORKSPACE/my.properties
echo "argus.app.admin.user=developer" | cat >> $WORKSPACE/my.properties
echo "argus.app.admin.pass=developer" | cat >> $WORKSPACE/my.properties
echo "argus.mail.enabled=true" | cat >> $WORKSPACE/my.properties
echo "argus.mail.smtp.user=box.noreply@argustelecom.ru" | cat >> $WORKSPACE/my.properties
echo "argus.mail.smtp.pass=DutyFr33!" | cat >> $WORKSPACE/my.properties
echo "argus.mail.smtp.port=25" | cat >> $WORKSPACE/my.properties
echo "argus.mail.smtp.host=mail.argustelecom.ru" | cat >> $WORKSPACE/my.properties
echo "argus.mail.smtp.ssl.enabled=false" | cat >> $WORKSPACE/my.properties
echo "argus.mail.smtp.starttls.enabled=false" | cat >> $WORKSPACE/my.properties
echo "argus.mail.smtp.auth.enabled=true" | cat >> $WORKSPACE/my.properties

if [ $mode == "ui-tests" ] || [ $mode == "ui-tests-pa" ]; then
	# переменую с именем БД Box объявляем в .gitlab-ci.yml 
	echo "Using database DB_NAME: $DB_NAME"
	echo "Prepare configuration argus.db.*"	
	# переменую с адресом хоста баз Box объявляем в .gitlab-ci.yml 
	echo "argus.db.address=$HostDB" | cat >> $WORKSPACE/my.properties
	echo "argus.db.port=5432" | cat >> $WORKSPACE/my.properties
	echo "argus.db.name=$DB_NAME" | cat >> $WORKSPACE/my.properties
	echo "argus.db.user=argus_sys" | cat >> $WORKSPACE/my.properties
	echo "argus.db.pass=vk38gwwm" | cat >> $WORKSPACE/my.properties

	if [ $mode == "ui-tests-pa" ]; then

   		echo ' -- Install server Box -- '
		build_number=`sed -n 's/argus\.app\.build-number=//p' ${WORKSPACE}/my.properties`
		name_distr="argusbox-dist-${build_number}.jar"
		if [ ! -f "$WORKSPACE/server-conf/box-dist/target/$name_distr" ]; then
    		echo "Not found $name_distr in directory $WORKSPACE/server-conf/box-dist/target"
			error_all "Install application server: not found distrib"
		else 
			echo "Found distrib $name_distr"
		fi
		cd $BUILDDIR/$WORKSPACE/server-conf/box-dist/target
		java -jar $name_distr -options $BUILDDIR/$WORKSPACE/my.properties
		[ $? -ne 0 ] && error_all "Install application server failed"

		echo ' -- Start server Box -- '
		cd $BUILDDIR/$WORKSPACE/server-app/inf-modules/webui
		mvn pre-integration-test -Pbefore-ui-tests-build-start-appserver
		[ $? -ne 0 ] && error_all "Start server Box"
		cd $BUILDDIR

		echo " -- Prepare configuration for Personal Area -- "	
		installPath="${HOME}/servers/${branch}/${wildflyPackage}-PA/";
		sed -i -e "s|INSTALL_PATH=.*|INSTALL_PATH=${installPath}|1" $WORKSPACE/my.properties
		# >> на хосте также есть агент teamcity с СП под ui-теcты, для перестраховки взял смещение 10,
		# а для СП личного кабинета port-offset=20
		sed	-i 's/jboss.socket.binding.port-offset=.*/jboss.socket.binding.port-offset=20/1' $WORKSPACE/my.properties
		echo "argus.security.login-module=ru.argustelecom.box.inf.login.PersonalAreaLoginModule" | cat >> $WORKSPACE/my.properties
		echo "argus.test.ui.remotearm=true" | cat >> $WORKSPACE/my.properties
		echo "contextRoot=" | cat >> $WORKSPACE/my.properties
		# ! незабывай учитывать в настройке argus.test.provider.address смещение порта "обыного" СП Box
		echo "argus.test.provider.address=127.0.0.1:8090" | cat >> $WORKSPACE/my.properties
	fi
fi

echo "Result configuration $WORKSPACE/my.properties: "
cat $WORKSPACE/my.properties | grep -Ev "(^#|^$)"

# selenium тесты только в сборке ui-tests
if [ $mode == "ui-tests" ] || [ $mode == "ui-tests-pa" ];  then
	# We use xvfb to run selenium tests on headless environment
	echo "Starting up Xvfb server";
	/etc/init.d/xvfb_start_script.sh start $buildcounter $branch
	[ $? -ne 0 ] && error_all "error: xvfb_start_script.sh start $buildcounter $branch"
	export DISPLAY=:$buildcounter
fi

# Deprecated. Convert shell scripts from dos to unix format
dos2unix make_all.sh && ./make_all.sh $mode
[ $? -ne 0 ] && error_all "error execute make_all.sh"

end_build
