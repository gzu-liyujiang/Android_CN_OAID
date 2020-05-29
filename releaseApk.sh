#!/usr/bin/env bash

echo "build release apk"
# 切换到当前脚本所在的目录
_CURRENT_FILE="${BASH_SOURCE[0]}"
_DIR_NAME="$(dirname "${_CURRENT_FILE}")"
echo "当前脚本：${_CURRENT_FILE}"
echo "脚本目录：${_DIR_NAME}"
cd "${_DIR_NAME}" || exit
echo "工作目录：${PWD}"

#==============================================================
# 以下为自动化构建，当然可考虑使用Github-Actions、Travis-CI等持续集成工具
#==============================================================
#gradlew clean cleanBuildCache assembleRelease --info --warning-mode all
gradlew clean cleanBuildCache resguardRelease --info --warning-mode all
