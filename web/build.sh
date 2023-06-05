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
    rm -f ./logs/* PyUI.tar.gz
    if [ "$2" == "all" ]; then
	rm -rf ./node_modules
	rm -f PyUI.log
    fi
    verify_success $? "web clean"
}

function do_build {
    npm update
    cp -r ./node_modules/bootstrap ./app/static
    cp -r ./node_modules/jquery ./app/static
    cp -r ./node_modules/js-cookie ./app/static
    verify_success $? "npm update"

    echo Create PyUI.tar.gz file
    tar -zcf PyUI.tar.gz ./app ./db.sqlite3 manage.py pyui
    verify_success $? "web build"
}

if [ "$1" == "clean" ]; then
    do_clean
elif [ "$1" == "build" ]; then
    do_build
else
    do_clean
    do_build
fi
