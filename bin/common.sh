#!/usr/bin/env bash

declare -a components=("services" "web" "spa" "tests")
declare -a services=("discovery" "gateway" "admin" "auth" "product" "order" "inventory")

function is_a_component {
    found_match="false"
    for components in ${components[@]}; do
	if [ "$1" == "$component" ]; then found_match="true"; break; fi
    done
    echo $found_match
}

function is_a_service {
    found_match="false"
    for service in ${services[@]}; do
	if [ "$1" == "$service" ]; then found_match="true"; break; fi
    done
    echo $found_match
}

function verify_success {
    if [ $1 == 0 ]; then
	if [ -n "$2" ]; then
	    echo "-- Build succeeded for $2 --"
	fi
    else
	if [ -n "$2" ]; then
	    echo "-- Build failed for $2. Exiting! --"
	fi
	exit 1;
    fi
}

