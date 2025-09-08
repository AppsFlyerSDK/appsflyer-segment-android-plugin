#!/bin/bash

appsflyerversion=$1
rc=$2

sed -E -i '' "s/(.*af-android-sdk:)([0-9]+\.[0-9]+\.[0-9]+)'/\1$appsflyerversion\'/g" app/build.gradle

sed -E -i '' "s/(.*af-android-sdk:)([0-9]+\.[0-9]+\.[0-9]+)'/\1$appsflyerversion\'/g" segmenttestapp/build.gradle

version_code=$(grep -E 'VERSION_CODE=([0-9]+)' gradle.properties | grep -o '[0-9]\+')
version_code=$((version_code+1)) 
sed -E -i '' "s/VERSION_CODE=([0-9]+)/VERSION_CODE=$version_code/g" gradle.properties

sed -E -i '' "s/VERSION_NAME=([0-9]+\.[0-9]+\.[0-9]+)/VERSION_NAME=$appsflyerversion-rc$rc/g" gradle.properties

sed -E -i '' "s/(POM_ARTIFACT_ID=.*)/\1-beta/g" gradle.properties

sed -E -i '' "s/(Built with AppsFlyer Android SDK.*)([0-9]+\.[0-9]+\.[0-9]+)(.*)/\1$appsflyerversion\3/g" Readme.md
sed -E -i '' "s/(.*appsflyer:segment-android-integration:)([0-9]+\.[0-9]+\.[0-9]+)(.*)/\1$appsflyerversion\3/g" Readme.md

sed -E -i '' "s/(.*setPluginInfo.*)([0-9]+\.[0-9]+\.[0-9]+)(.*)/\1$appsflyerversion\3/g" app/src/main/java/com/segment/analytics/android/integrations/appsflyer/AppsflyerIntegration.java

touch "releasenotes.$appsflyerversion"
