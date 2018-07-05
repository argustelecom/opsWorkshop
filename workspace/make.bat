@SETLOCAL enableextensions enabledelayedexpansion  
@rem enabledelayedexpansion наделяет восклицательный знак особыми обязанностями, поэтому лучше не использовать его в скриптах
@echo off
rem  режимы: clean, clean-all, extra-lite, lite

rem conf:
rem     -- сконфигурировать сервер (аналог configure-server.bat в большом Аргус)
rem clean:
rem     -- очистить все проекты
rem clean-all:
rem     -- очистить все проекты
rem     -- удалить все деплойменты и конфигурацию с сервера приложений, указанного в INSTALL_PATH
rem extra-lite:
rem		-- выполнить инициализацию конфигурации
rem		-- выполнить сборку основных проектов
rem	lite:
rem		-- выполнить инициализацию конфигурации
rem		-- выполнить сборку основных проектов
rem     -- выполнить конфигурирование сервера приложений

set START_TIME=%TIME%
pushd .

if /I "%1" == "clean"		goto clean
if /I "%1" == "clean-all"	goto clean-all
if /I "%1" == "extra-lite"	goto extra-lite
if /I "%1" == "lite"    	goto lite
if /I "%1" == "dist"    	goto dist
if /I "%1" == "conf"    	goto conf
if /I "%1" == "renew"    	goto renew


echo usage: clean | clean-all | extra-lite | lite | conf | dist | renew
goto done

:clean
	set MAVEN_OPTS=-Xmx512M -Dfile.encoding=UTF-8
	
	call mvn clean -Dbuild-dist
	if errorlevel 1 goto build_error

	goto done

:clean-all
	set MAVEN_OPTS=-Xmx512M -Dfile.encoding=UTF-8
	
	call mvn clean -Dbuild-dist
	
	cd server-conf
	call mvn -f configure-server.xml groovy:execute@clean-deployments
	if errorlevel 1 goto build_error
	
	goto done
	
:extra-lite
	set MAVEN_OPTS=-Xmx512M -Dfile.encoding=UTF-8

	call mvn clean install -DskipTests
	if errorlevel 1 goto build_error

	goto done

:conf
	set MAVEN_OPTS=-Xmx512M -Dfile.encoding=UTF-8

	cd server-conf
	call mvn clean install 
	if errorlevel 1 goto build_error
	
	call mvn -f configure-server.xml groovy:execute@configure-server
	if errorlevel 1 goto build_error

	goto done
	
:lite
	set MAVEN_OPTS=-Xmx512M -Dfile.encoding=UTF-8

	call mvn xml:validate -pl parent -amd
	if errorlevel 1 goto build_error
	
	call mvn clean install
	if errorlevel 1 goto build_error
	
	call mvn javadoc:aggregate-jar -pl parent -amd
	if errorlevel 1 goto build_error
	
	cd server-conf
	call mvn -f configure-server.xml groovy:execute@configure-server
	if errorlevel 1 goto build_error	
	
	goto done

:dist
	set MAVEN_OPTS=-Xmx512M -Dfile.encoding=UTF-8

	call mvn clean install
	if errorlevel 1 goto build_error

	cd ../server-conf/dist
	call mvn clean install
	if errorlevel 1 goto build_error
	
	goto done

:renew
	set MAVEN_OPTS=-Xmx512M -Dfile.encoding=UTF-8

	call mvn clean install -DskipTests -U
	if errorlevel 1 goto build_error
	
	goto done
	
:build_error
	echo ************************************************************
	echo * build or test error occured, artifacts are not assembled *
	echo * error level is %ERRORLEVEL%                              *
	echo ************************************************************
	goto exit

:done
	echo build success

:exit
	popd
	call time-diff.bat "%START_TIME%" "%TIME%"