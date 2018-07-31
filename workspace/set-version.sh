#!/bin/bash

NEW_VERSION="$1"
CONFIG_FILE='work.properties'
START_TIME=`date +'%s'`
DIR=`pwd`

if [ "$NEW_VERSION" ] ; then
	echo "NEW_VERSION from args: $NEW_VERSION"
	# Put the version to config
    sed -i "s/ops.app.version=.*/ops.app.version=$NEW_VERSION/" $DIR/$CONFIG_FILE
fi

declare -A properties

function readconf () {
    IFS="="
    while read -r name value; do
        # first letter
        n01=${name:0:1}
        # second letter
        n11=${name:1:1}        
        # skip comments
        [[ "$n01" == "#" || "$n11" == "#" ]] && continue
        # skip empty lines
        [[ -z "${name}" ]] && continue
        properties[$name]=$value
    done < "${CONFIG_FILE}"
}

function quit_on_error () {
    error_message=$1
    [[ "$error_message" == "" ]] && error_message='Set version script error!'
	echo $error_message >&1
	exit 1
}

function set_version_with_dependents () {
	path_to_reactor=$1
	project_name=$2
	project_version=$3
	reactor_directory=$DIR/$path_to_reactor
	echo [INFO]   ===========================================================================================================
	echo [INFO]   Setting version for project $project_name in reactor $path_to_reactor
	echo [INFO]     Reactor directory: $reactor_directory
	echo [INFO]     Project version: $project_version
	cd $reactor_directory
    echo ***[INFO]     reactor_directory: ${reactor_directory}pom.xml
    if [ -f pom.xml ]; then
		echo [INFO]     Found Maven reactor descriptor ${reactor_directory}pom.xml
		if [ -f ${project_name}/pom.xml ]; then
			echo [INFO]     Found Maven project descriptor $reactor_directory${project_name}/pom.xml
			echo [INFO]     "Running command: mvn versions:set -DnewVersion="$project_version -pl $project_name -amd
			mvn versions:set -DnewVersion=$project_version -pl $project_name -amd
			[ $? -ne 0 ] quit_on_error "Set version with dependents error 1. Name: $project_name"
			mvn versions:commit -pl $project_name -amd
			[ $? -ne 0 ] quit_on_error "Set version with dependents error 2. Name: $project_name"
		else
			quit_on_error "[ERROR]  Maven project not found, check $reactor_directory/${project_name}pom.xml"
		fi
	else
		quit_on_error "[ERROR]  Maven reactor project not found, check ${reactor_directory}pom.xml"
	fi
}

function set_version () {
	path_to_project=$1
	project_version=$2
	project_directory=$DIR/$path_to_project
	echo [INFO]   ===========================================================================================================
	echo [INFO]   Setting version for $path_to_project project
	echo [INFO]     Project directory: $project_directory
	echo [INFO]     Project version: $project_version
	cd $project_directory
	if [ -f pom.xml ]; then
		echo [INFO]     Found Maven project descriptor pom.xml		
		mvn versions:set -DnewVersion=$project_version
		[ $? -ne 0 ] quit_on_error "Set version error 1. Path: $path_to_project"
		mvn versions:commit
		[ $? -ne 0 ] quit_on_error "Set version error 2. Path: $path_to_project"
	else
		quit_on_error "1" "[ERROR]  Maven project descriptor not found, check pom.xml"
	fi
}

readconf
VERSION=${properties['ops.app.version']}
echo 'Version from config: ' $VERSION

set_version_with_dependents "" "parent" $VERSION
set_version "" $VERSION
set_version "server-app" $VERSION
set_version "server-app/app-modules" $VERSION
set_version "server-app/env-modules" $VERSION
set_version "server-app/inf-modules" $VERSION
set_version "server-conf" $VERSION

END_TIME=`date +'%s'`
echo '*******************************'
echo 'Version updated successfully!!!'
echo '*******************************'
echo 'Start time:' `date --date @$START_TIME '+%H:%M:%S'` 
echo 'End time:' `date --date @$END_TIME '+%H:%M:%S'`
echo 'Duration(sec.):' $(($END_TIME-$START_TIME))