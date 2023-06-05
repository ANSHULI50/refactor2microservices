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
    rm -rf build
    rm -f ./reactapp.tar.gz ./build/reactapp.tar.gz
    if [ "$2" == "all" ]; then
	rm -rf ./node_modules
    fi
    verify_success $? "spa clean"
}

function do_build {
    npm update
    verify_success $? "npm update"

    npm run build
    verify_success $? "npm run build"

    pushd build > /dev/null
    echo Create reactapp.tar.gz file
    tar -zcf reactapp.tar.gz ./*
    verify_success $? "spa build"
    mv reactapp.tar.gz ..
    popd > /dev/null
}

if [ "$1" == "clean" ]; then
    do_clean
elif [ "$1" == "build" ]; then
    do_build
else
    do_clean
    do_build
fi
