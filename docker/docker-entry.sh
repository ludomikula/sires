#!/bin/bash

APP_JAR="/sires/app.jar"

JAVA_XMS="256m"
JAVA_XMX="256m"

echo "Starting sires application"
appPids=`pidof java`
if [ ! -z ${appPids} ]; then
   echo "Tried to start sires application, but it is already running on PID(s): ${appPids}. Startup aborted."
   exit 1
fi

echo
echo "Java version:"
echo
${JAVA_HOME}/bin/java -version
echo

${JAVA_HOME}/bin/java  \
  -Djava.security.egd=file:/dev/./urandom \
  -Dhttps.protocols=TLSv1.1,TLSv1.2 \
  -Xms${JAVA_XMS} -Xmx${JAVA_XMX} \
  ${JAVA_OPTS} \
  -jar "${APP_JAR}" ${SPRINGBOOT_OPTS} ${CUSTOM_APP_PROPERTIES}

