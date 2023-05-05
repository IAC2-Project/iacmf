FROM library/ubuntu:bionic
ENV PUBLIC_ADDRESS=193.196.54.96
ENV OS_FAMILY=linux
ENV MACHINE_IMAGE=ubuntu
ENV INSTANCE_TYPE=large
ENV PORT=8080
WORKDIR /opt/edmm
ADD ./openstack.json openstack.json
ADD ./petclinic.war petclinic.war
ADD ./tomcat_create.sh tomcat_create.sh
ADD ./petclinic_configure.sh petclinic_configure.sh
ADD ./tomcat_start.sh tomcat_start.sh
RUN ./tomcat_create.sh && \
    ./petclinic_configure.sh
EXPOSE 8080
CMD ["./tomcat_start.sh"]

