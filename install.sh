#!/bin/bash

echo "[Requirement-Java] Requirement JDK 8.0+ and env of JAVA_HOME."

if [ -z "${JAVA_HOME}" ]; then
  echo "[Requirement-Java] JAVA_HOME is not exists!"
  echo "[Requirement-Java] Please check java is installed and set env of JAVA_HOME."
  exit 1
else
  echo "[Requirement-Java] JAVA_HOME: ${JAVA_HOME}"
fi

if [ ! -x "${JAVA_HOME}/bin/javac" ]; then
  echo "[Requirement-Java] Java not found!"
  echo "[Requirement-Java] Please install JDK 8.0+ or JRE 8.0+"
  exit 1
fi

JAVA_VERSION=`java -version 2>&1 |awk 'NR==1{ gsub(/"/,""); print $3 }'`
echo "[Requirement-Java] JAVA_VERSION: ${JAVA_VERSION}"

if [[ "$JAVA_VERSION" < "1.8" ]]; then
  echo "[Requirement-Java] Please install JDK 8.0+ or JRE 8.0+"
  exit 1
fi

echo "[Requirement-Maven] Requirement install Maven and set the PATH."
if [ -z "${MAVEN_HOME}" ]; then
  echo "[Requirement-Maven] MAVEN_HOME is not exists!"
  echo "[Requirement-Maven] Please check maven is installed and set env of MAVEN_HOME."
  exit 1
else
  echo "[Requirement-Java] MAVEN_HOME: ${MAVEN_HOME}"
fi

cd `dirname $0`
BIN_DIR=`pwd`
echo "[Requirement-BIN_DIR] BIN_DIR: ${BIN_DIR}"
mvn clean jfx:jar
if [ $? -ne 0 ]; then
  echo "Quit  because maven install fail"
  exit -1
fi

cp start.sh "${BIN_DIR}/target/jfx/app"
if [ $? -ne 0 ]; then
  echo "Copy start.sh fail"
  exit -1
fi

cd "${BIN_DIR}/target/jfx/app"
tar -zcvf mybatis-generator-gui-no-jre.tar.gz *
if [ $? -ne 0 ]; then
  echo "tar jar fail"
  exit -1
fi

cp "${BIN_DIR}/target/jfx/app/mybatis-generator-gui-no-jre.tar.gz" "${BIN_DIR}"
if [ $? -ne 0 ]; then
  echo "Copy jar fail"
  exit -1
fi
