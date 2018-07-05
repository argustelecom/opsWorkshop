#!/usr/bin/env bash
#
# Requirements:
#   - git >= 1.8.5
#   - mvn
#   - rsync
#   - tar
#   - sed
#   - dos2unix
#
# Note:
#   To remove files not in repository use: git clean -dfx


### VARIABLES ###
PRODUCT_NAME="Box"
ROOT_PATH=$(dirname $0)
TMP_BUILD_DIRECTORY_NAME=/tmp/$(date +%s)


### READ BRANCH ###
if [ -z "$1" ]; then
    echo "No branch given"
    exit 1
fi

if [ -z "$(git -C $ROOT_PATH branch -a | egrep "remotes/origin/${1}$" 2>&1)" ]; then
    echo "Bad branch name: $1"
    exit 1
fi

# Get branch version name
GIT_BRANCH=$1


### MAIN SCRIPT ###

# Clone ansible installer repository
git clone --depth=1 git@gitlab:DevOps/Ansible.git $TMP_BUILD_DIRECTORY_NAME/Ansible

# Move argus_install directory
rsync -r --progress --remove-source-files $TMP_BUILD_DIRECTORY_NAME/Ansible/tasks/argus_install/* $TMP_BUILD_DIRECTORY_NAME

# Remove unnecessary repo directory
rm -rf $TMP_BUILD_DIRECTORY_NAME/Ansible

# Read utils.sh
source $TMP_BUILD_DIRECTORY_NAME/data/utils.sh
trap_cleanup() {
    # Cleanup tmp directories
    rm -rf $TMP_BUILD_DIRECTORY_NAME
    info "Clear $TMP_BUILD_DIRECTORY_NAME directory"
}
trap trap_cleanup 0
trap 'trap_error ${LINENO}' ERR

# Make raw installer
bash $TMP_BUILD_DIRECTORY_NAME/make.sh "$PRODUCT_NAME"

# Make build directories
mkdir -p $TMP_BUILD_DIRECTORY_NAME/dist/data/dbctl
info "$TMP_BUILD_DIRECTORY_NAME/dist/data/dbctl was created/checked"
mkdir -p $TMP_BUILD_DIRECTORY_NAME/dist/data/updatedb-$GIT_BRANCH
info "$TMP_BUILD_DIRECTORY_NAME/dist/data/update-$GIT_BRANCH was created/checked"

# Remember old branch name
OLD_GIT_BRANCH=$(git -C $ROOT_PATH rev-parse --abbrev-ref HEAD)
info "Remember $OLD_GIT_BRANCH git branch"
trap_checkout() {
    # Switch back repo version
    git -C $ROOT_PATH checkout $OLD_GIT_BRANCH
    info "Checkout repository to $OLD_GIT_BRANCH"
}
trap trap_checkout 0

# Switch repo to given version
git -C $ROOT_PATH checkout $GIT_BRANCH
info "Checkout repository from $OLD_GIT_BRANCH to $GIT_BRANCH"

# Copy dbctl util
rsync -r --progress $ROOT_PATH/db/src/projects/dbhost_scripts/ $TMP_BUILD_DIRECTORY_NAME/dist/data/dbctl
info "dbctl util copied to $TMP_BUILD_DIRECTORY_NAME/dist/data/dbctl"

# Build argusbox-dist jar
info "Run: workspace install"
S_PWD=$PWD; cd $(dirname $0)/workspace && mvn clean install && cd $S_PWD
info "Run: box-dist clean install"
S_PWD=$PWD; cd $(dirname $0)/workspace/server-conf/box-dist && mvn clean install && cd $S_PWD

# Copy argusbox-dist jar
rsync -r --progress $ROOT_PATH/workspace/server-conf/box-dist/target/*.jar $TMP_BUILD_DIRECTORY_NAME/dist/data/argus-$GIT_BRANCH.jar
info "argus-$GIT_BRANCH.jar copied to $TMP_BUILD_DIRECTORY_NAME/dist/data"

# Build db update
S_PWD=$PWD; cd $(dirname $0)/db && mvn clean install && cd $S_PWD
info "$(dirname $0)/db builded"

# Copy db update
rsync -r --progress $ROOT_PATH/db/target/dbm/* $TMP_BUILD_DIRECTORY_NAME/dist/data/updatedb-$GIT_BRANCH
info "db update copied to $TMP_BUILD_DIRECTORY_NAME/dist/data/update-$GIT_BRANCH"

# Setup config file
rsync -r --progress --remove-source-files $TMP_BUILD_DIRECTORY_NAME/dist/install.conf.raw $TMP_BUILD_DIRECTORY_NAME/dist/install.conf
set_config_option $TMP_BUILD_DIRECTORY_NAME/dist/install.conf "version" $GIT_BRANCH
info "Configuration was set to $GIT_BRANCH version"

# Create installer archive
INSTALLER_NAME=${PRODUCT_NAME,,}-installer-$GIT_BRANCH.tgz
S_PWD=$PWD; cd $TMP_BUILD_DIRECTORY_NAME/dist && tar cvfz $S_PWD/$INSTALLER_NAME * && cd $S_PWD
info "Done"
