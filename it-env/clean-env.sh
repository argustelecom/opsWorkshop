#!/bin/bash
sshpass -p vk38gwwm ssh -o StrictHostKeyChecking=no root@192.168.102.25 << HERE
cd /home/postgres/dbctl
./dbctl.sh drop $1
exit
HERE