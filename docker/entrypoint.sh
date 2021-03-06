#!/bin/bash
# See Dockerfile and DOCKER.md for further info

if [ "${DOCKER_PLM_MONITORING_API_PORT_ENABLE}" == "1" ]; then
  nohup socat -lm TCP-LISTEN:8086,fork TCP:127.0.0.1:${DOCKER_PLM_MONITORING_API_PORT_DESTINATION} &
fi

PLM_JAR_FILE=$(find "$DOCKER_PLM_JAR_PATH" -type f -name "$DOCKER_PLM_JAR_FILE" -print -quit)
if [[ "${PLM_JAR_FILE}x" == "x" ]]
then
  >&2 echo "ERROR: File '$DOCKER_PLM_JAR_FILE' not found in path '$DOCKER_PLM_JAR_PATH'"
  exit 1
fi

exec java \
  $JAVA_OPTIONS \
  -Xms$JAVA_MIN_MEMORY \
  -Xmx$JAVA_MAX_MEMORY \
  -Djava.net.preferIPv4Stack="$DOCKER_JAVA_NET_PREFER_IPV4_STACK" \
  -DLogging-Level="$DOCKER_PLM_LOGGING_LEVEL" \
  -jar "$PLM_JAR_FILE" \
  --remote "$DOCKER_PLM_REMOTE" --remote-limit-api "$DOCKER_PLM_REMOTE_LIMIT_API" \
  "$@"
