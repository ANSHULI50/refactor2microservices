#!/usr/bin/env bash

function verify_success {
    if [ $1 == 0 ]; then
	echo "-- Build succeeded for $2 --"
    else
	echo "-- Build failed for $2 --"
	exit -1;
    fi
}

function do_clean {
    if [ -n "$1" ]; then
	cd $1
    fi
    mvn clean
    verify_success $? "maven clean"
}

function do_build {
    if [ -n "$1" ]; then cd $1; fi
    mvn package
    if [ -n "$1" ]; then
	cp ./target/$1.war ../target
    fi
    verify_success $? "maven build"
}


if [ "$1" == "clean" ]; then
    do_clean $2
elif [ "$1" == "build" ]; then
    pwd
    do_build $2
else
    do_clean
    do_build
fi
