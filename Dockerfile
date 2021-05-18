FROM adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.11_9-slim

ARG DEPLOY_HOME="/opt/app"

ENV DEPLOY_HOME=$DEPLOY_HOME
ENV APP_JVM_OPTIONS="-Xms128mb -Xmx128mb -XX:+DisableExplicitGC"
ENV APP_JAR=cass-dropwizard-rest.jar


# Make deployment directory
RUN mkdir -p -m 775 $DEPLOY_HOME

#Copy jar
COPY build/libs/cass-dropwizard-rest.jar $DEPLOY_HOME/cass-dropwizard-rest.jar


#Copy config yaml
COPY config.yml $DEPLOY_HOME/config.yml

#Copy startup script
COPY docker_app_start.sh $DEPLOY_HOME/docker_app_start.sh

#Make startup script executable
RUN chmod +x $DEPLOY_HOME/docker_app_start.sh

EXPOSE 9000 9001

WORKDIR /opt/app

ENTRYPOINT ["sh","-x","docker_app_start.sh"]
