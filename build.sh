#!/bin/bash

source common.sh

echo "Starting cleanup..."
${CLEAN_SCRIPT_PATH}
echo "Finished cleanup."

echo "Building to ${JAR_FULL_PATH} ..."
mvn -f ${WORKING_DIR} package

if [ -f ${JAR_FULL_PATH} ]; then
	echo "Build success!"
else
	echo "Build failed!"
	
	exit 1
fi