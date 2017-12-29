#!/bin/sh -xe

# This script starts docker and systemd (if el7)

# Version of CentOS/RHEL
el_version=$1

# Commands and paths
DOCKERPATH=/OpenISS
BUILDDEPS="cd $DOCKERPATH/tools/ci && ./docker_run_tests.sh"

# Debug
DEBUG=1

if [ "$DEBUG" -eq "1" ]; then
	uname -a
	date
	pwd
	ls -al
fi

# Run tests in Container
# Use `--privileged` for cgroup compatability
if [ "$el_version" -eq "6" -o "$el_version" -eq "6.8" ]; then

	sudo docker run \
		--privileged \
		-v /sys/fs/cgroup:/sys/fs/cgroup --rm=true \
		-v `pwd`:$DOCKERPATH:rw ${OS_TYPE}:${OS_VERSION_PREFIX}${OS_VERSION} \
		/bin/bash -c "$BUILDDEPS $el_version"

elif [ "$el_version" -eq "7" -o "$el_version" -eq "7.2" ]; then

	docker run \
		--privileged -d -ti -e "container=docker"  \
		-v /sys/fs/cgroup:/sys/fs/cgroup \
		-v `pwd`:$DOCKERPATH:rw ${OS_TYPE}:${OS_VERSION_PREFIX}${OS_VERSION} /usr/sbin/init

	DOCKER_CONTAINER_ID=$(docker ps | egrep 'centos|scientific' | awk '{print $1}')
	docker logs $DOCKER_CONTAINER_ID
	docker exec \
		-ti $DOCKER_CONTAINER_ID \
		/bin/bash -xec "$BUILDDEPS $el_version"
	docker ps -a
	docker stop $DOCKER_CONTAINER_ID
	docker rm -v $DOCKER_CONTAINER_ID

fi

if [ "$DEBUG" -eq "1" ]; then
	date
fi

# EOF
