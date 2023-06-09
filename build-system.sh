#!/usr/bin/env bash

export OMS_ROOT=`pwd`

source ./bin/common.sh

echo "--- Verify system and tools ---"
./bin/verify-system.sh
verify_success $?

./bin/verify-build-tools.sh
verify_success $? "build tools installation verification"

echo "--- Verification Done!! ---"


function do_clean {

    cd $OMS_ROOT
    echo "-- Clean --"
    for component in "services" "web" "spa" "tests"; do
	cd $component
	./build.sh clean
	cd ..
    done

    if [ "$1" == "all" ]; then
	cd $OMS_ROOT/staging
	rm -rf services web spa schema tests
	verify_success $? "staging clean"
	
	cd $OMS_ROOT/docker/bin
	./clean-files.sh
	verify_success $? "docker clean files"

	./clean-images.sh
	verify_success $? "docker clean images"
    fi
    echo "-- Done --"
}


function do_build {

    component=$1

    if [ -z "$component" ]; then
	for this_component in "services" "web" "spa" "tests"; do
	    cd $OMS_ROOT/$this_component
	    ./build.sh build
	    cd ..
	done
    else
	if [ $(is_a_component $component) == "true" ]; then
	    cd $OMS_ROOT/$component
	    ./build.sh
	    verify_success $? "$component build"
	    cd ..
	else
	    if [ $(is_a_service $component) == "true" ]; then
		cd $OMS_ROOT/services
		./build.sh build $component
		component="services"
	    else
		echo "Error: Incorrect component name $1 to build"
		exit 1
	    fi
	fi
    fi
    do_stage $component
}


function do_stage {
    echo "-- Pull artifacts to Staging --"
    cd $OMS_ROOT/staging/bin
    ./pull-artifacts.sh $1
    verify_success $? "staging pull artifacts"
}


function do_images {

    echo "-- Check if docker daemon is running --"
    cd $OMS_ROOT/docker/bin
    ./verify-docker-install.sh
    verify_success $? "docker install verification"

    echo "-- Pull artifacts from Staging to Docker images dir --"
    cd $OMS_ROOT/docker/bin
    ./pull-artifacts.sh $1
    verify_success $? "docker pull artifacts"

    echo "-- Build Docker Images --"
    cd $OMS_ROOT/docker
    if [ -z "$1" ]; then
	docker-compose build
	verify_success $? "All docker images"
    elif [ "$1" == "services" ]; then
	for service in ${services[@]}; do
	    docker-compose build $service
	done
    else
	docker-compose build $1
	verify_success $? "$1 docker image"
    fi

    if [ -z "$1" ] || [ "$1" == "tests" ]; then
	cd $OMS_ROOT/docker/jmeter
	docker-compose build
    fi

    docker images | head -1
    docker images | grep "ntw/"
}


#----------------Main-------------------

if [ "$1" == "clean" ]; then
    do_clean $2
elif [ "$1" == "build" ]; then
    do_build $2
elif [ "$1" == "stage" ]; then
    do_stage $2
elif [ "$1" == "images" ]; then
    do_images $2
else
    read -p "Do you wish to do complete build [Y/n]: " yn
    case $yn in
        [Nn]* ) exit;;
    esac
    do_clean; do_build; do_images;
fi
    
echo "-- Done --"
