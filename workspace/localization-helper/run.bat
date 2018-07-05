@echo off
call mvn clean install
cd target
java -jar localizationhelper.jar %1 %2 %3
cd ..