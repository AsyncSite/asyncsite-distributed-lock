#!/usr/bin/env sh
set -e
DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
# Download wrapper jar on the fly
WRAPPER_JAR="$DIR/gradle/wrapper/gradle-wrapper.jar"
WRAPPER_PROPS="$DIR/gradle/wrapper/gradle-wrapper.properties"
if [ ! -f "$WRAPPER_JAR" ]; then
  mkdir -p "$DIR/gradle/wrapper"
  curl -sL -o "$WRAPPER_JAR" https://repo1.maven.org/maven2/org/gradle/gradle-wrapper/8.10.2/gradle-wrapper-8.10.2.jar
  echo "distributionUrl=https\://services.gradle.org/distributions/gradle-8.10.2-bin.zip" > "$WRAPPER_PROPS"
fi
exec java -jar "$WRAPPER_JAR" "$@"
}