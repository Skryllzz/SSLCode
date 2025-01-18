#!/bin/sh

cd /root/zenyte/ || exit

chmod +x ./zpaq

./gradlew run
