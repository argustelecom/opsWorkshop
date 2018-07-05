# 
#  Copyright (c) www.argustelecom.ru 2002, 2013. Все права защищены.
#  
#  check_env_error -- скрипт проверки на соответсвие узла требованиям 
#                     для запуска Сервера приложений Аргус.
#
# 	Описание:
#	  Скрипт предназначен для автоматизации проверки узла на соответвие требованиям
# 	  для запуска Сервера приложений Аргус.
#	  Рассчитан на запуск под тем пользователем, который имеет права
#         на запись в каталог, из которого будет запущен скрипт. 
#
#       Примеры запуска: 
#              ./check-env.sh - вывод информации на экран
#              ./check-env.sh > log.txt - вывод информации в файл log.txt
#	
#       Лог ошибок:
#              check-env-error.log - в этот лог выводятся все ошибки проверок узла на соответствие 
#				требованиям для запуска Сервера приложений.
#
#!/bin/bash

date > check-env-error.log
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo 	1 	Требования к операционной системе:
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo 	1.1  	ОС: Unix-подобные системы на платформе intel x86_64
uname -a

us=`uname -s`
if [ $us != "Linux" ]; then
  echo 'Выбранная ОС не поддерживается. Необходима ОС Linux'  >> check-env-error.log
  exit 1
fi

um=`uname -m`
if [ $um != "x86_64" ]; then
  echo 'Выбранная платформа не поддерживается. Необходима платформа x86_64'  >> check-env-error.log
  exit 1
fi

echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo 	1.2    Сетевое имя хоста не должно содержать символ подчеркивания.
echo 'имя хоста:' `uname -n`

un=`uname -n`
if [[ $un =~ "_" ]] ; then
  #if echo "$un" | grep -i "." >/dev/null; then
  echo "Сетевое имя хоста $un не должно содержать символ подчеркивания: _" >> check-env-error.log
fi

echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo 	1.3   Наличие в ОС УЗ argus для установки и запуска cервера приложений.

grep "argus:" /etc/passwd #>/dev/null
if [ $? -ne 0 ]; then
  echo 'No user argus found'  >> check-env-error.log
else
echo
echo            УЗ argus должна обладать правами на запись в каталог cервера приложений и его подкаталоги.
echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo	1.3.1	Требования к организации каталогов
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo		/Data" 				"Каталог, который содержит в себе Сервер приложений и дополнительное окружение для поддержания его работы.
echo		"				"Рекомендуется выделять отдельный раздел диска.
echo		/Data/distr"			"Каталог содержит в себе дистрибутивы и пакеты установок Сервера приложений Аргус и дополнительного ПО.
echo		"	"/Data/distr/1450/argus-dist-3.8.4-ural.jar
echo		"	"/Data/distr/1450/ural.prod.argus-app-01.properties
echo		"	"/Data/distr/jdk_7u80_linux_x64.gz
echo
echo		/Data/jboss_arch"		"Каталог, хранящий в себе резервные копии Сервера приложений \(формат копии: ддммгггг/jboss_prod\).
echo		/Data/jboss_arch/16012016/jboss_prod
echo
echo		/Data/jboss_prod"		"Каталог установки Сервера приложений Аргус.
echo		/Data/jdk"			"Каталог с пакетом JDK.
echo		/Data/jdk/jdk1.7.0_80"		"Пакет JDK версии 7 обновления 80, требуется для работы Сервера приложений Аргус.
echo
echo		/Data/nmon"			"Каталог, содержащий отчеты nmon о производительности системы \(формат: \<сетевое_имя_узла\>_ггммдд_0101.nmon\)
echo		"				"и их архивы \(формат: \<сетевое_имя_узла\>_ггммдд_0101.nmon.gz\).
echo		"				"- после архивации отчеты удаляются.
echo		"				"Информацию о настройке nmon смотри в Руководстве Администратора.
echo
echo		/Data/rhq-agent"			"Каталог установки агента системы мониторинга.
echo		/Data/scripts"			"Разные вспомогательные скрипты.
echo		/Data/TEMP"			"Каталог с временны файлами.
echo

if ! [ -d /Data ]; then
echo 'Отсутствует каталог /Data. Невозможно проверить права на запись для пользователя: argus' >> check-env-error.log
else  echo `ls -l / | grep Data`
fi

if ! [ -d /Data/distr ]; then
echo 'Отсутствует каталог /Data/distr. Невозможно проверить права на запись для пользователя: argus' >> check-env-error.log
else  echo `ls -l /Data | grep distr`
fi

if ! [ -d /Data/jboss_arch ]; then
echo 'Отсутствует каталог /Data/jboss_arch. Невозможно проверить права на запись для пользователя: argus' >> check-env-error.log
else  echo `ls -l /Data | grep jboss_arch`
fi

if ! [ -d /Data/jboss_prod ]; then
echo 'Отсутствует каталог /Data/jboss_prod. Невозможно проверить права на запись для пользователя: argus' >> check-env-error.log
else  echo `ls -l /Data | grep jboss_prod`
fi

if ! [ -d /Data/jdk ]; then
echo 'Отсутствует каталог /Data/jdk. Невозможно проверить права на запись для пользователя: argus' >> check-env-error.log
else  echo `ls -l /Data | grep jdk`
fi

if ! [ -d /Data/jdk/jdk1.7.0_80 ]; then
echo 'Отсутствует каталог /Data/jdk/jdk1.7.0_80. Невозможно проверить права на запись для пользователя: argus' >> check-env-error.log
else  echo `ls -l /Data/jdk | grep jdk`
fi

if ! [ -d /Data/nmon ]; then
echo 'Отсутствует каталог /Data/nmon. Невозможно проверить права на запись для пользователя: argus' >> check-env-error.log
else  echo `ls -l /Data | grep nmon`
fi

if ! [ -d /Data/rhq-agent ]; then
echo 'Отсутствует каталог /Data/rhq-agent. Невозможно проверить права на запись для пользователя: argus' >> check-env-error.log
else  echo `ls -l /Data | grep rhq_agent`
fi

if ! [ -d /Data/scripts ]; then
echo 'Отсутствует каталог /Data/scripts. Невозможно проверить права на запись для пользователя: argus' >> check-env-error.log
else  echo `ls -l /Data | grep scripts`
fi

if ! [ -d /Data/TEMP ]; then
echo 'Отсутствует каталог /Data/TEMP. Невозможно проверить права на запись для пользователя: argus' >> check-env-error.log
else  echo `ls -l /Data | grep TEMP`
fi

fi
echo
echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo 	1.4    В ОС должна быть установлена кодировка ru_RU.UTF-8
echo 'в ОС установлена кодировка:'
locale
echo
locale | grep "ru_RU.UTF-8"
if [ $? -ne 0 ]; then
echo 'в ОС отсутствует кодировка ru_RU.UTF-8'  >> check-env-error.log
fi
echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo 	1.5 	Настройки синхронизаци времени ОС сервера приложений
echo Дата: `date`
echo
echo Настройка на ntp-server
cat /etc/ntp.conf | grep server
echo
echo Запущен ли ntp-service
ps -elf | grep ntpd | grep -v grep
echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo 	1.6	Значения по умолчанию портов СП offset=0:
echo            8080                    HTTP
echo            8009                    AJP/1.3
echo            9990                    management-http
echo            7600                    jgroups-tcp
echo            7601 и 7602             jgroups-tcp
echo            8190                    webui-mobile-http
echo            Если используется смещение \(offset\) портов, то к значениям по умолчанию прибавляется значение смещения.
echo            Подробнее см. Руководство Администратора, п. 2.5 Открытые порты.
echo
echo            занятые порты СП в ОС - из значений по умолчанию \(offset=0\):
netstat -lptunee | grep -E "(8080|8009|9990|7600|7601|7602|8190)"
echo
echo            все занятые порты в ОС:
netstat -lptunee | grep LISTEN
echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo 	1.7	HDD: для установки сервера приложений требуется не менее 1 ГБайт.
echo		За 1 день типовой работы 1000 пользователей на Сервере приложений на жестком диске заполняется примерно 2.5 ГБайт логов.
echo            Рекомендуемый объем раздела для 1000 пользователей: 240 ГБ
echo		Существующеее дисковое пространство:
df -h
echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo 	1.9.	Максимальное число открытых файлов и сокетов, на 1000 пользователей: 20000.
cat /proc/sys/fs/file-max
echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo 	1.10.	Максимальное число запущенных процессов, без дополнительных портов: 1000.
echo 		Каждый дополнительный порт добавляет к максимальному числу 256 запущенных процессов.
ulimit -a | grep processes
echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo	2.	Дополнительные рекомендуемые настройки ОС Linux \(см. Руководство Администратора, п. 2.10.6\):
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo 	2.1.	Размер буфера приема данных по умолчанию для всех соединений: 262144
cat /proc/sys/net/core/rmem_default
echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo 	2.2.	Размер буфера передачи данных по умолчанию для всех соединений: 262144
cat /proc/sys/net/core/wmem_default
echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo 	2.3.	Максимальный размер буфера приема данных для всех соединений: 262144
cat /proc/sys/net/core/rmem_max
echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo 	2.4.	Максимальный размер буфера передачи данных для всех соединений: 262144
cat /proc/sys/net/core/wmem_max
echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo	3.	Требования к аппаратной части:
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo	3.1.	ОЗУ: 6 ГБ + 20 МБ на 1 пользователя.
echo	В время работы сервера прилодений у ОС должны оставаться свободными не менее 2 ГБ ОЗУ. 
echo	Серверу приложений выделяется оперативная память: 4 ГБ + 20 МБ * КоличествоПользователей
echo	КоличествоПользователей - число конкурентных(одновременно работающих) пользователей сервера приложений. 
free -m
echo
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo	3.2.	CPU: 1 ядро для ОС + каждое следующее ядро требуется для 100 пользователей Сервера Приложений.
echo	Процессор - Intel Xeon E5-2640 или аналог.
echo	модель каждого ядра:
cat /proc/cpuinfo | grep "^model name"
echo	количество ядер:
cat /proc/cpuinfo | grep "^cpu core" | wc -l
echo

echo ----------------------------------------------------------------------------------------------------------------------------------------
echo	4.	Другие требования к ПО:
echo ----------------------------------------------------------------------------------------------------------------------------------------
echo	4.1.	Пакет JDK версии 8 обновления 77
java -version
jv=`java -version 2>&1`
jv=`echo $jv |cut -c 15-22`
#echo $jv

etalon="1.8.0_77"
#etalon="1.8.0_92"

if [[ "$jv" < "$etalon" ]]; then
echo "Версия Java $jv должна быть не меньше, чем $etalon" >> check-env-error.log
fi

date >> check-env-error.log
