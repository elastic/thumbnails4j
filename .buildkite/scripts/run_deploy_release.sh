#!/bin/bash

set -eo pipefail

RUN_TYPE=${1:-}

if [[ "${RUN_TYPE}" != "deploy" && "${RUN_TYPE}" != "release" ]]; then
    echo "Missing arguemnt for RUN_TYPE. Must be 'deploy' or 'release'"
    exit 2
fi

function realpath {
  echo "$(cd "$(dirname "$1")"; pwd)"/"$(basename "$1")";
}

SCRIPT_PATH="$(dirname "${BASH_SOURCE}")"
BUILDKITE_PATH=$(realpath "$(dirname "$SCRIPT_PATH")")
PROJECT_ROOT=$(realpath "$(dirname "$BUILDKITE_PATH")")

# Set Maven options
export MAVEN_CONFIG="-V -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn -Dhttps.protocols=TLSv1.2 -Dmaven.wagon.http.retryHandler.count=3 -Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false $MAVEN_CONFIG"

set -x

mvnw_command="$PROJECT_ROOT/mvnw"

pushd $PROJECT_ROOT

cd $PROJECT_ROOT

if [[ "${RUN_TYPE}" == "deploy" ]]; then
    $mvnw_command -X -s .buildkite/mvn-settings.xml \
        -Pgpg clean deploy \
        -DskipTests \
        --batch-mode
        # 2>/dev/null
elif [[ "${RUN_TYPE}" == "release" ]]; then
    $mvnw_command release:prepare release:perform \
      --settings .buildkite/mvn-settings.xml \
      -Darguments="-DskipTests --settings .ci/settings.xml" \
      --batch-mode 2>/dev/null
else
    echo "Invalid RUN_TYPE: ${RUN_TYPE}. Nothing to do."
fi

popd
