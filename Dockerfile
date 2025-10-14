FROM openjdk:25-jdk-slim

ARG DEPLOY_HOME="/opt/app"

ENV DEPLOY_HOME=$DEPLOY_HOME
ENV APP_JVM_OPTIONS="-Xms128M -Xmx128M -XX:+DisableExplicitGC"
ENV APP_JAR=cassandra-dropwizard-all.jar

# Make deployment directory
RUN mkdir -p -m 775 $DEPLOY_HOME

#Copy jar
COPY build/libs/cassandra-dropwizard-all.jar $DEPLOY_HOME/cassandra-dropwizard-all.jar

#Copy config yaml
COPY config.yml $DEPLOY_HOME/config.yml

#Copy startup script
COPY docker_app_start.sh $DEPLOY_HOME/docker_app_start.sh

#Make startup script executable
RUN chmod +x $DEPLOY_HOME/docker_app_start.sh

EXPOSE 9000 9001

WORKDIR /opt/app

ENTRYPOINT ["sh","-x","docker_app_start.sh"]