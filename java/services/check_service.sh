#!/usr/bin/bash
servicesdir="$PWD"
for i in $(ls $servicesdir); do
  if [ -d "$servicesdir/$i" ]; then
    logfile="/tmp/debug_$i.log"
    echo -n Log file for $i service: 
    [ ! -f ] && echo -n " NOT"
    echo " FOUND"
  fi
done

for i in {0..30..10}; do
  port=$((8101 + $i))
  curl "http://localhost:$port" > /dev/null 2>/dev/null
  if [ $? -eq 0 ]; then
    echo "Port $port is open"
  else
    echo "Port $port is closed"
  fi
done