# 
#  Copyright (c) www.argustelecom.ru 2002, 2017. Все права защищены.
#  
#  remove_old_logs -- скрипт архивации и удаления лог-файлов Сервера Приложений за 
#		      прошлые дни.
#
# 	Описание:
#	  Скрипт предназначен для автоматизации удаления "устаревших"
# 	  логов Сервера Приложений.
#	  Рассчитан на запуск под тем пользователем, который имеет права
#         на запись в каталог с логами сервера. Как правило, это тот же 
# 	  пользователь, под которым запускается сам Сервер Приложений.
#
# 	  Для регулярной очистки логов рекомендуется создать задачу планировщика
#         cron, вызывающую данный скрипт.
#
#	  Перед использованием скрипта рекомендуется уточнить значения параметров:
#	  путь до каталога с логами Сервера, каталог для бэкапов,
# 	  количество дней с последнего изменения файла, при котором считать файл старым,
#     файл для ведения лога о проделанной скриптом работе.
#
#!/bin/sh

export PATH=/usr/local/bin:/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/sbin
# путь до каталога с логами Сервера Приложений
export argus_logs=/Data/jboss_prod/standalone/log
# путь файлу, в который ведется лог работы скрипта
export logfile=/Data/scripts/arch_logs.log
# путь до каталога с бэкапом логов
export bugreports=/Data/jboss_prod/standalone/log/bugreports

date_now=$(date +%Y\-%m\-%d);

echo '======================================================' >> $logfile
echo "" >> $logfile
echo "Started at `date`" >> $logfile
echo '' >> $logfile
ls -al $argus_logs >> $logfile
echo '' >> $logfile

#Переменные
OLD_LOGS=$(date -d "-1 day" +"%Y-%m-%d" )
echo $OLD_LOGS

BUGREPORTS=$(date -d "-1 day"  +"%Y.%m.%d")
echo $BUGREPORTS

OLD_ARCH=$(date -d "-30 day"  +"%Y.%m.%d")
echo $OLD_ARCH

#Создадим архивы старше 1 дня
find /Data/jboss_prod/standalone/log -name "*$OLD_LOGS*" | xargs tar -cvzf /Data/jboss_logs_arch/$OLD_LOGS.logs.tar.gz
find /Data/jboss_prod/standalone/log/bugreports -name "*$BUGREPORTS*" | xargs tar -cvzf /Data/jboss_logs_arch/$BUGREPORTS.logs.bugreports.tar.gz

#Удалим все файлы старше 1 дня из каталога логов СП
find /Data/jboss_prod/standalone/log -name "*$OLD_LOGS*" -exec rm -rf {} \;
find /Data/jboss_prod/standalone/log/bugreports -name "*$BUGREPORTS*" -exec rm -rf {} \;

#Удаление архивов, которые лежат дольше, чем кол-во дней хранения логов
find /Data/jboss_logs_arch -name "*$OLD_ARCH*" -exec rm -rf {} \;

echo "Finished at `date`" >> $logfile
echo '======================================================' >> $logfile
