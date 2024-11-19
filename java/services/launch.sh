#! /usr/bin/bash
servicesdir="$PWD"
pidfile=$servicesdir/servers.pid
echo "Pids of launch `date`" > $pidfile
for i in $(ls $servicesdir); do
  if [ -d $servicesdir/$i ]; then
    cd $servicesdir/$i
    logfile="/tmp/debug_$i.log"
    mvn spring-boot:run > $logfile &
    pid=$!
    # echo "El servicio se está ejecutando en segundo plano con PID: $pid"
    # Opcionalmente, puedes guardar el PID en un archivo
    echo "$i running in pid $pid" >> $pidfile
    echo Launched $i service, view the log in $logfile
  fi
done
