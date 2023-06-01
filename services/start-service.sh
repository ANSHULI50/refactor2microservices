#!/bin/bash

function wait_for_port {
    while ! nc -z localhost $1; do   
	sleep 1 # wait for 1/10 of the second before check again
	printf "."
    done
}

PWD=`pwd`
SVC_ARGS="-Dspring.config.name=config"

function start_service {
    echo "-- starting service $1 --"
    JAVA_DEBUG_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=localhost:$3"
    echo java $JAVA_DEBUG_OPTIONS $SVC_ARGS -Dserver.port=$2 -jar $1/target/$1.war &
    java $JAVA_DEBUG_OPTIONS $SVC_ARGS -Dserver.port=$2 -jar $1/target/$1.war &
    wait_for_port $2
}

function verify_postgres_db {

    IS_POSTGRESS_INSTALLED="false"
    IS_POSTGRESS_RUNNING="false"
    IS_POSTGRES_SCHEMA_CREATED="false"

    if type -p pg_isready > /dev/null; then
	IS_POSTGRES_INSTALLED="true"
	pg_isready -h localhost > /dev/null
	if [ $? = 0 ]; then
	    IS_POSTGRESS_RUNNING="true"
	    if sudo su postgres -c "psql -lqt 2> /dev/null" | cut -d \| -f 1 | grep -wq "oms"; then
		IS_POSTGRES_SCHEMA_CREATED="true"
		echo "Verified postgres db is up and schema is created"
	    else
		echo "Postgres DB schema not created. Some services may fail."
	    fi	
	else
	    echo "Postgres DB not running on localhost. Some services may fail."
	fi
    else
	echo "Postgres DB not installed on localhost. Some services may fail."
    fi
    
    if [ "$IS_POSTGRES_SCHEMA_CREATED" == "false" ] ||
	   [ "$IS_POSTGRESS_RUNNING" == "false" ] ||
	   [ "$IS_POSTGRES_INSTALLED" == "false" ]; then
	read -p "Start services anyway (y/n): " yn
	case $yn in
	    [Nn]* ) exit -1;;
	esac
    fi
}

verify_postgres_db

SERVICES="gateway admin auth product order inventory"
SVC_PORT=8080
DEBUG_PORT=6000
if [ -z "$1" ] || [ "$1" == "all" ]; then
    if [ -z "$1" ]; then
	read -p "Do you wish to start all services [Y/n]? " yn
	case $yn in
	    [Nn]* ) exit;;
	esac
    fi
    for service in $SERVICES
    do
	start_service $service $SVC_PORT $DEBUG_PORT
	SVC_PORT=$((SVC_PORT+1))
	DEBUG_PORT=$((DEBUG_PORT+1))
    done
else
    service_match=0
    for service in $SERVICES
    do
	if [ "$1" == "discovery" ]; then
	    start_service discovery 8761 8762
	    break
	elif [ "$1" == "$service" ]; then
	    start_service $service $SVC_PORT $DEBUG_PORT
	    service_match=1
	    break
	else
	    SVC_PORT=$((SVC_PORT+1))
	    DEBUG_PORT=$((DEBUG_PORT+1))
	fi
    done
    if [ "$1" == "discovery" ]; then
	start_service discovery 8761 6006
	service_match=1
    fi
    if [ $service_match == 0 ]; then
	echo "Incorrect service name $1"
	echo "Use any one of the following as service name argument:"
	echo "all, gateway admin, auth, product, order, inventory, discovery" 
    fi
fi

echo "Following java server processes are running"
jps -v | grep server
