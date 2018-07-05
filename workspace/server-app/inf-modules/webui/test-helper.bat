@setlocal enabledelayedexpansion
@echo off
REM скрипт работы с интеграционными и нагрузочными тестами. Применение:
REM  test-helper (ui-tests-build | run | prepare-tests | prepare-all)
REM 
REM ui-tests-build --	прогоняет модульные тесты, предварительно запуская, а затем останавливая сервер. Рассчитывает, что сервер уже сконфигурен, а деплоймент уже собран в argus-enterprise/target
REM run -- 		выполнить тесты проекта (также можно указать имя класса(классов) теста(ов), который(е) выполнять), например:
REM             	test-helper run system-env-ui CreateTransferIT скомпилит и выполнит тест CreateTransferIT
REM			Примечания:
REM				1. Более детально формат описания конкретных тестов см. в http://maven.apache.org/surefire/maven-failsafe-plugin/examples/single-test.html
REM				2. Таргет run не занимается запуском и остановкой сервера, деплоем ear - рассчитывает, что это уже сделано.
REM				3. Однако, если сервер еще не запущен, попытается запустить.
REM 
REM prepare-tests --	Подготавливает jar с тестами для подпихивания jmeter`у при нагрузочном тестировании (зависимости кладутся в maven репозиторий)
REM prepare-all --	Подготавливает полный комплект нагрузочного тестирования: jmeter c подсунутой test-jar, зависимостями test-jar, work.properties, my.properties, тестпланом.
REM			Остается только открыть jmeter, выбрать в нем тестплан и запустить.
REM
REM порадуй kostd@argus хорошими отзывами о работе скрипта

set START_TIME=%TIME%

if "%1"=="run" goto run 
if "%1"=="prepare-tests" goto prepare-tests
if "%1"=="prepare-all" goto prepare-all
if "%1"=="ui-tests-build" goto ui-tests-build
goto invalid_param

:run
echo ****************
echo *   runnin`!   *
echo ****************
if "%2"=="" echo running all tests
if NOT "%2"=="" echo running tests for project %2
pushd ..\..
if "%2"=="" call mvn integration-test -Pui-tests
if NOT "%2"=="" call mvn integration-test -pl %2 -Pui-tests -Dit.test=%3
if errorlevel 1 goto build_error
popd
goto done

:prepare-tests
echo **********************
echo *   prepare-tests    *
echo **********************
echo deprecated, not actualized, #TODO: TASK-79874
goto done

::==================================================================================================
:prepare-all
echo *********************
echo *   prepare-all     *
echo *********************
echo deprecated, not actualized, #TODO: TASK-79874
goto done

::===================================================================================================
:ui-tests-build
echo *********************
echo *   ui-tests-build  *
echo *********************
REM Собираем имена всех ui проектов в server-app
pushd ..\..
REM	Требуется: - найти все ui проекты в подкаталогах
REM            - сохранить относительный путь от server-app (переадача только имен в списке через 
REM              аргумент -pl вызывает у maven ошибку + воспринимается проще)
for /D /R %%a in (*-ui) do set modules=%%a,!modules! & set modules=!modules:%cd%\=!
echo cleaning projects:  %modules%
call mvn clean -pl %modules%
if errorlevel 1 goto build_error
popd

REM 1. в проекте webui выполняем предварительные мероприятия: все до фазы pre-integration-tests включительно.
echo *************	start and deploy	*********************
call mvn pre-integration-test -Pbefore-ui-tests-build-add-ear -Pbefore-ui-tests-build-start-appserver
if errorlevel 1 goto build_error

REM 2. из server-app запускаем integration-tests плагин на ui-проектах
pushd ..\..
echo *************	executing ui-tests build on projects:  %modules%	*************
call mvn integration-test -pl %modules% -Pui-tests
if errorlevel 1 goto build_error
popd

REM 3. завершающие мероприятия: снова из webui, включая особые профили. Там всего два плагина, и оба на initialize, чтобы делать поменьше лишнего
echo *************	stop and clean	*************
call mvn initialize -Pafter-ui-tests-build-stop-appserver -Pafter-ui-tests-build-clean-deployments
if errorlevel 1 goto build_error

echo *************	ui-tests build done! test results see in maven-log or target/failsafe/* reports
goto done
::===================================================================================================

:invalid_param
echo *******************************************************
echo *          incorrect parameter, build failed!!!!      *
echo *******************************************************
echo parameter %1 not valid!!!
echo commandline must be like this: 
echo "test-helper (compile | run | prepare-tests | prepare-all)"
goto end

:build_error
echo *******************************************************
echo * build or test error occured, distrib not assembled! *
echo *       check if server-app project is built or       *
echo * try "test-helper.bat compile" to install necessary  *
echo *                     artifacts.                      *
echo * error level is %ERRORLEVEL%                         *
echo *******************************************************
goto end

:done
echo ****************
echo * build done!  *
echo ****************
call ..\..\..\time-diff.bat "%START_TIME%" "%TIME%"

:end
echo off

