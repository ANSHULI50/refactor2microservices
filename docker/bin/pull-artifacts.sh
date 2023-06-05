#!/bin/bash

OMS_ROOT=../..
OMS_STAGING=$OMS_ROOT/staging
OMS_DEPLOY=$OMS_ROOT/docker

source $OMS_ROOT/bin/common.sh

function verify_success {
    if [ $1 == 0 ]; then
	echo "-- Copied $2 from staging --"
    else
	echo "-- Error: Copy failed for $2. Exiting! --"
	exit -1;
    fi
}

if [ -z "$1" ] || [ "$1" == "web" ]; then
    cp -u $OMS_STAGING/web/PyUI.tar.gz $OMS_DEPLOY/web/image
    verify_success $? "web build"
    cp -u $OMS_STAGING/web/PyUI.tar.gz $OMS_DEPLOY/lb-web/image
    verify_success $? "lb web build"
fi

if [ -z "$1" ] || [ "$1" == "spa" ]; then
    cp -u $OMS_STAGING/spa/reactapp.tar.gz $OMS_DEPLOY/spa/image
    verify_success $? "spa build"
fi


if [ -z "$1" ] || [ "$1" == "schema" ]; then
    cp -u $OMS_STAGING/schema/*.sql $OMS_DEPLOY/postgres/image
    verify_success $? "schema files"
fi

if [ -z "$1" ] || [ "$1" == "services" ]; then
    test -d $OMS_DEPLOY/services/image/war || mkdir -p $OMS_DEPLOY/services/image/war && \
	    cp -u $OMS_STAGING/services/*.war $OMS_DEPLOY/services/image/war
    verify_success $? "services build"
fi

if [ $(is_a_service "$1") == "true" ]; then
    test -d $OMS_DEPLOY/services/image/war || mkdir -p $OMS_DEPLOY/services/image/war && \
	    cp -u $OMS_STAGING/services/$1.war $OMS_DEPLOY/services/image/war
    verify_success $? "$1 service build"
fi    

if [ -z "$1" ] || [ "$1" == "tests" ]; then
    test -d $OMS_DEPLOY/jmeter/image/tests || mkdir -p $OMS_DEPLOY/jmeter/image/tests && \
	    cp -u $OMS_STAGING/tests/* $OMS_DEPLOY/jmeter/image/tests
    verify_success $? "jmeter files"
    test -d $OMS_DEPLOY/ubuntux/image/tests || mkdir -p $OMS_DEPLOY/ubuntux/image/tests && \
	    cp -u $OMS_STAGING/tests/* $OMS_DEPLOY/ubuntux/image/tests
    verify_success $? "ubuntux files"
fi
