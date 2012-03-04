#!/bin/sh

cd /system/etc/wifi
insmod /system/lib/modules/tiwlan_drv.ko
sleep 2
start wlan_loader
sleep 2
ifconfig tiwlan0 $1 netmask 255.255.255.0 up
sleep 2
start wpa_supplicant
sleep 3
wlan_cu -itiwlan0 -s /mnt/sdcard/cli.sh
