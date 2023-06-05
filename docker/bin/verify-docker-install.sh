#!/bin/bash

echo "Verifying docker installation"
docker version
if [ $? == 0 ]; then
    echo "Docker daemon installed and running"
elif [ $? == 1 ]; then
    echo "Docker daemon installed but not running"
    exit 1;
else
    echo "Docker daemon not installed"
    exit 1;
fi

exit 0
