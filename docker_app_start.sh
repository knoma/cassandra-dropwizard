#!/bin/sh
# Fail on any error
set -e

# Log the command being executed
echo "Starting Dropwizard application..."

# Execute the Java application
# $APP_JVM_OPTIONS: "-Xms128M -Xmx128M -XX:+DisableExplicitGC"
# $DEPLOY_HOME: "/opt/app"
# $APP_JAR: "cassandra-dropwizard-all.jar"
# Args: "server config.yml"

exec java $APP_JVM_OPTIONS -jar $DEPLOY_HOME/$APP_JAR server $DEPLOY_HOME/config.yml
