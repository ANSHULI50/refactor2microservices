#!/usr/bin/env bash

OMS_ROOT=`pwd`

source $OMS_ROOT/bin/common.sh

declare -a service_containers=("discovery" "gateway" "lb-services" "admin" "auth" "product" "order" "inventory")
declare -a web_containers=("web" "lb-web")
declare -a data_containers=("cassandra" "postgres" "rabbitmq" "redis")
declare -a infra_containers=("elasticsearch" "kibana" "jaeger-collector" "jaeger-query" "jaeger-agent" "fluentd" "es-exporter" "redis-exporter" "pg-exporter" "prometheus")

declare -A commands=( ["start"]="up -d" ["stop"]="stop" ["status"]="ps -a" )
declare -A wait_durations=( ["start"]=3 ["stop"]=0 )

function exec_command {
    task=$1
    command="docker-compose ${commands["$task"]}"
    wait_duration=${wait_durations[$task]}
    shift;
    containers=$@

    echo "$task: $containers"
    if [ "$task" != "start" ]; then
        $(echo $command ${containers[@]})
	if [ "$task" == "stop" ]; then
	    docker-compose rm -f
	fi
    else
	for container in $containers; do
	    echo "Executing: $command $container"
	    $($command $container 1> /dev/null 2> /dev/null)
	    if [ $? -ne 0 ]; then
		echo "Error executing $command $container"
		exit 1
	    fi
	    if [[ $wait_duration -gt 0 ]]; then
		docker-compose ps $container
		echo "Waiting for ${wait_duration}s" 
		sleep $wait_duration
	fi
	done
    fi
}


function exec_task {
    task=$1
    container=$2
    cd $OMS_ROOT/docker

    if [ -n "$container" ]; then
	     exec_command $task $container
    else
	while true; do
	    echo "Choose containers to start."
	    echo "1> Infra Containers"
	    echo "2> Data Containers"
	    echo "3> Service Containers"
	    echo "4> Web Containers"
	    echo "5> All Containers"
	    printf "Choice: "
	    read choice
	    case $choice in
		1 ) exec_command $task "${infra_containers[@]}";
		    break;;
		2 ) exec_command $task "${data_containers[@]}";
		    break;;
		3 ) exec_command $task "${service_containers[@]}";
		    break;;
		4 ) exec_command $task "${web_containers[@]}";
		    break;;
		5 ) echo "$task all containers";
		    exec_command $task "${infra_containers[@]}";
		    exec_command $task "${data_containers[@]}";
		    exec_command $task "${service_containers[@]}";
		    exec_command $task "${web_containers[@]}";
		    break;;
		* ) echo "Incorrect choice"
	    esac
	done
    fi
}


function do_test {
    echo "-- Run tests --"
    cd $OMS_ROOT/docker/jmeter
    docker-compose build
    docker-compose up
    if [ $? != 0 ]; then
	echo "Tests failed - Unable to start Jmeter container"
	exit -1;
    fi
    echo "-- Done --"
}


function do_update {

    component=$1
    container=$component

    cd $OMS_ROOT/docker
    exec_task stop $container

    cd $OMS_ROOT
    ./build-system.sh build $component
    ./build-system.sh images $component

    cd $OMS_ROOT/docker
    exec_task start $container

}



function get_status {
    cd $OMS_ROOT/docker
    docker-compose ps $1
}


#-------------Main---------------

if [ "$1" == "start" ]; then
    exec_task start $2
elif [ "$1" == "update" ]; then
    do_update $2
elif [ "$1" == "test" ]; then
    do_test
elif [ "$1" == "stop" ]; then
    exec_task stop $2
elif [ "$1" == "status" ]; then
    exec_task status $2
else
    echo "Usage: Arg 1 must be: start | update | test | stop | status"
    exit 1;
fi
