@echo off
rem TASK-52735
rem Вызывается ведущим версии при выделении новой ветки. Поднимает версию workspace 
rem 

rem порядок работы:
rem 
rem 	1. Пробей новую версию в workspace.properties, например, так: ops.app.version=0.4.0
rem 	2. Вызови скрипт set-version.bat
rem 	2. Убедись, что скрипт отработал без ошибок и написал version updated successfully!!!
rem 	3. Выложи все изменения, сделанные скриптом(+ work.properties), в гит.
rem 	4. Убедись, что в твоем my.properties версия теперь тоже правильная.

rem https://github.com/mojohaus/versions-maven-plugin/issues/113
rem versions-maven-plugin:2.3:set выполняется с ошибкой вида java.io.FileNotFoundException: D:\work\src\ops\workspace\parent\parent
rem на установку версии не влияет, однако же некрасиво

set start_time=%time%
set root=%cd%
set last_error=0

rem пока выставляем версию только на основании work.properties (my.properties игнорируем)
for /F "tokens=1* delims==" %%A in (work.properties) do (
    if "%%A"=="ops.app.version" set version=%%B
)

call :set_version_with_dependents "" "parent" %version%
if %last_error% equ 1 goto build_error

call :set_version "" %version%
if %last_error% equ 1 goto build_error

call :set_version "server-app" %version%
if %last_error% equ 1 goto build_error

call :set_version "server-app\app-modules" %version%
if %last_error% equ 1 goto build_error

call :set_version "server-app\app-modules\billing-modules" %version%
if %last_error% equ 1 goto build_error

call :set_version "server-app\app-modules\crm-modules" %version%
if %last_error% equ 1 goto build_error

call :set_version "server-app\app-modules\nri-modules" %version%
if %last_error% equ 1 goto build_error

call :set_version "server-app\app-modules\pa-modules" %version%
if %last_error% equ 1 goto build_error

call :set_version "server-app\app-modules\product-dir-modules" %version%
if %last_error% equ 1 goto build_error

call :set_version "server-app\env-modules" %version%
if %last_error% equ 1 goto build_error

call :set_version "server-app\inf-modules" %version%
if %last_error% equ 1 goto build_error

call :set_version "server-conf" %version%
if %last_error% equ 1 goto build_error

:done
	echo version updated successfully!!!
	call time-diff.bat "%start_time%" "%time%"
	goto eof

:build_error
	echo [ERROR]  **********************************************************************************************************
	echo [ERROR]  VERSION CHANGE FAILED!!!
	echo [ERROR]  The version of the projects is not changed or partially changed!!! 
	echo [ERROR]  Error level is %last_error%
	goto eof

rem function set_version(path_to_project, project_version, set_parent)
rem begin
:set_version
	set last_error=0
	set path_to_project=%1
	set path_to_project=%path_to_project:"=%
	set project_version=%2
	set project_version=%project_version:"=%
	set project_directory=%cd%\%path_to_project%
	
	echo [INFO]   ===========================================================================================================
	echo [INFO]   Setting version for [%path_to_project%] project 
	echo [INFO]     Project directory: %project_directory%
	echo [INFO]     Project version: %project_version%
	
	cd %project_directory%
	
	if exist pom.xml (
		echo [INFO]     Found Maven project descriptor {pom.xml}
		
		call mvn versions:set -DnewVersion=%project_version%
		if errorlevel 1 goto set_version_error
		
		call mvn versions:commit
		if errorlevel 1 goto set_version_error
		
	) else (
		echo [ERROR]  Maven project descriptor not found {check pom.xml}
		goto set_version_error
	)
	
	:set_version_return 
		cd %root%
		goto eof
	
	:set_version_error	
		set last_error=%errorlevel%
		goto set_version_return
rem end

		
rem function set_version_with_dependents(path_to_reactor, project_name, project_version)
rem begin
:set_version_with_dependents
	set last_error=0
	set path_to_reactor=%1
	set path_to_reactor=%path_to_reactor:"=%
	set project_name=%2
	set project_name=%project_name:"=%
	set project_version=%3
	set project_version=%project_version:"=%
	set reactor_directory=%cd%\%path_to_reactor%
	
	echo [INFO]   ===========================================================================================================
	echo [INFO]   Setting version for project [%project_name%] in reactor [%path_to_reactor%]
	echo [INFO]     Reactor directory: %reactor_directory%
	echo [INFO]     Project version: %project_version%	
	
	cd %reactor_directory%
	
	if exist pom.xml (
		echo [INFO]     Found Maven reactor descriptor {%reactor_directory%\pom.xml}
		if exist %project_name%\pom.xml (
			echo [INFO]     Found Maven project descriptor {%reactor_directory%\%project_name%\pom.xml}
			
			call mvn versions:set -DnewVersion=%project_version% -pl %project_name% -amd
			if errorlevel 1 goto set_version_with_dependents_error
			
			call mvn versions:commit -pl %project_name% -amd
			if errorlevel 1 goto set_version_with_dependents_error
			
		) else (
			echo [ERROR]  Maven project not found {check %reactor_directory%\%project_name%\pom.xml}
			goto set_version_with_dependents_error
		)
	) else (
		echo [ERROR]  Maven reactor project not found {check %reactor_directory%\pom.xml}
		goto set_version_with_dependents_error
	)
	
	:set_version_with_dependents_return 
		cd %root%
		goto eof
	
	:set_version_with_dependents_error	
		set last_error=%errorlevel%
		goto set_version_with_dependents_return
rem end
		
:eof
