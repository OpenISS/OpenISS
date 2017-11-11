#!/bin/sh -xe

# This script starts docker and systemd (if el7)

# Version of CentOS/RHEL
el_version=$1

# Run tests in Container
# We use `--privileged` for cgroup compatability, which seems to be enabled by default in HTCondor 8.6.x
if [ "$el_version" -eq "6" ]; then

	sudo docker run \
		--privileged -v /sys/fs/cgroup:/sys/fs/cgroup --rm=true \
		-v `pwd`:/OpenISS:rw centos:centos${OS_VERSION}
		/bin/bash -c "make -f /OpenISS/src/Makefile deps"

elif [ "$el_version" -eq "7" ]; then

	docker run \
		--privileged -d -ti -e "container=docker"  -v /sys/fs/cgroup:/sys/fs/cgroup
		-v `pwd`:/OpenISS:rw  centos:centos${OS_VERSION} /usr/sbin/init

	DOCKER_CONTAINER_ID=$(docker ps | grep centos | awk '{print $1}')
	docker logs $DOCKER_CONTAINER_ID
	docker exec \
		-ti $DOCKER_CONTAINER_ID /bin/bash \
		-xec "make -f /OpenISS/src/Makefile deps; echo -ne \"------\nEND OpenISS TESTS\n\";"
	docker ps -a
	docker stop $DOCKER_CONTAINER_ID
	docker rm -v $DOCKER_CONTAINER_ID

fi
