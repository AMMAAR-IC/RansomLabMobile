#!/bin/sh
# Gradle wrapper launch script
APP_HOME=$(dirname "$0")
exec java -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
