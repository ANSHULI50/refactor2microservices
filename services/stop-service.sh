#!/bin/bash
function stop_service {
    echo "-- Stopping service $1 --"
    SERVICE=$1
    PID=$(ps -eaf | grep -e ${SERVICE}.war | grep -v grep | awk '{print $2}')
    if [ -n "$PID" ]; then
        echo "Killing service $SERVICE with pid $PID"
        echo $PID | xargs kill
    else
        echo "$SERVICE service not running"
    fi
}

SERVICES="gateway admin auth product order inventory discovery"
if [ -z "$1" ] || [ "$1" == "all" ]; then
    read -p "Do you wish to stop all services [Y/n]: " yn
    case $yn in
        [Nn]* ) exit;;
    esac    
    for service in $SERVICES
    do
	stop_service $service
    done
else
    service_match=0
    for service in $SERVICES
    do
	if [ "$1" == "$service" ]; then
	    stop_service $service
	    service_match=1
	    break
	fi
    done
    if [ $service_match == 0 ]; then
	echo "Incorrect service name $1"
	echo "Use any one of the following as service name argument:"
	echo "all, gateway, admin, auth, product, order, inventory, discovery" 
    fi
fi
