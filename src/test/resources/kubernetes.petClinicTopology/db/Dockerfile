FROM library/ubuntu:bionic
ENV PUBLIC_ADDRESS=193.196.52.76
ENV OS_FAMILY=linux
ENV MACHINE_IMAGE=ubuntu
ENV INSTANCE_TYPE=large
ENV SCHEMA_NAME=petclinic
ENV PASSWORD=petclinic
ENV USER=pc
ENV MYSQL_DATABASE=petclinic
ENV MYSQL_USER=pc
ENV MYSQL_PASSWORD=petclinic
ENV MYSQL_ALLOW_EMPTY_PASSWORD=yes
WORKDIR /opt/edmm
ADD ./openstack.json openstack.json
ADD ./schema.sql schema.sql
ADD ./db_configure.sh db_configure.sh
RUN ./db_configure.sh

