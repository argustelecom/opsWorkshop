#!/bin/bash

# debug mode чтобы падать при первой ошибке
set -x

#TODO: параметризация

#TODO: копировать из my.properties.template + настраивать?
[ ! -e my.properties ] && touch my.properties

# магическая подготовка для сборки. ИМХО должна быть убрана
cd server-conf 
mvn initialize 
[ ! $? -eq 0 ] && exit 1
#TODO: этот шаг уж точно надо убрать. 
#cd ops-core
#mvn clean install 
#[ ! $? -eq 0 ] && exit 1
#cd ..

cd ..


# сборка сервера
cd server-app
mvn clean install 
[ ! $? -eq 0 ] && exit 1

cd ..