#!/bin/bash
#
#------------------------------------------------
# Установщик дистрибутива ARGUS Enterprise server
# (c) НТЦ Аргус, Санкт-Петербург, 2015г.
#------------------------------------------------
#

java -jar ${argus.dist.final-name}.jar -console
chmod +x ${appInstallPath}/bin/*.sh
