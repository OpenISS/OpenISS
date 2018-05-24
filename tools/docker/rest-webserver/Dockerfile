FROM tomcat:alpine

# tomcat-users.xml sets up user accounts for the Tomcat manager GUI
# and script access for Maven deployments
ADD tomcat/tomcat-users.xml $CATALINA_HOME/conf/
ADD tomcat/context.xml $CATALINA_HOME/webapps/manager/META-INF/

ADD tomcat/run.sh $CATALINA_HOME/bin/run.sh
RUN chmod +x $CATALINA_HOME/bin/run.sh

# create mount point for volume with application
RUN mkdir $CATALINA_HOME/webapps/OpenISS

# add tomcat jpda debugging environmental variables
#ENV JPDA_OPTS="-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n"
ENV JPDA_ADDRESS="8000"
ENV JPDA_TRANSPORT="dt_socket"

# start tomcat7 with remote debugging
EXPOSE 8080
CMD ["run.sh"]
