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
    cd jmeter
    rm -f *.csv
    verify_success $? "tests clean"
    cd ..
}

function do_build {
    cd jmeter
    ./create-data.sh
    verify_success $? "tests build"
    cd ..
}

if [ "$1" == "clean" ]; then
    do_clean
elif [ "$1" == "build" ]; then
    do_build
else
    do_clean
    do_build
fi
