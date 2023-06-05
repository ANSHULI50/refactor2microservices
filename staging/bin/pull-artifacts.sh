#!/bin/bash

OMS_ROOT=../..
OMS_STAGING=$OMS_ROOT/staging

source $OMS_ROOT/bin/common.sh

function verify_success {
    if [ $1 == 0 ]; then
	echo "-- Copied $2 to staging --"
    else
	echo "-- Error: Copy failed for $2. Exiting! --"
	exit -1;
    fi
}

if [ -z "$1" ] || [ "$1" == "web" ]; then
    test -d $OMS_STAGING/web || mkdir -p $OMS_STAGING/web
    cp -u -r $OMS_ROOT/web/PyUI.tar.gz $OMS_STAGING/web
    verify_success $? "web build"
fi

if [ -z "$1" ] || [ "$1" == "spa" ]; then
    test -d $OMS_STAGING/spa || mkdir -p $OMS_STAGING/spa
    cp -u -r $OMS_ROOT/spa/reactapp.tar.gz $OMS_STAGING/spa
    verify_success $? "spa build"
fi

if [ -z "$1" ] || [ "$1" == "services" ]; then
    test -d $OMS_STAGING/services || mkdir -p $OMS_STAGING/services
    cp -u $OMS_ROOT/services/target/*.war $OMS_STAGING/services
    verify_success $? "services build"
fi

if [ $(is_a_service "$1") == "true" ]; then
    test -d $OMS_STAGING/services || mkdir -p $OMS_STAGING/services
    cp -u $OMS_ROOT/services/target/$1.war $OMS_STAGING/services
    verify_success $? "$1 service build"
fi

if [ -z "$1" ] || [ "$1" == "tests" ]; then
    test -d $OMS_STAGING/tests || mkdir -p $OMS_STAGING/tests
    cp -u -r $OMS_ROOT/tests/jmeter/* $OMS_STAGING/tests
    verify_success $? "jmeter files"
fi

if [ -z "$1" ] || [ "$1" == "schema" ]; then
    test -d $OMS_STAGING/schema || mkdir -p $OMS_STAGING/schema
    cp -u -r $OMS_ROOT/bin/*.sql $OMS_STAGING/schema
    verify_success $? "schema files"
fi
