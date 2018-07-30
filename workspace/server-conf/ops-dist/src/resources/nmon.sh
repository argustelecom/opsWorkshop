#!/bin/bash
export NMONFS=/Data/nmon

cd $NMONFS

/Data/scripts/nmon_x86_64_rhel6 -f -m $NMONFS -s 60 -c 1440
/bin/find $NMONFS -name '*.nmon' -mmin +2880 | xargs gzip
/bin/find $NMONFS -name '*.nmon.gz' -mtime +365 | xargs rm