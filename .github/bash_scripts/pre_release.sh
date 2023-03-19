#!/bin/bash

releaseversion=$1

sed -r -i '' "s/(.*)(af-android-sdk:)([0-9]+\.[0-9]+\.[0-9]+)'/\1$releaseversion\'/g" app/build.gradle

sed -r -i '' "s/(.*)(af-android-sdk:)([0-9]+\.[0-9]+\.[0-9]+)'/\1$releaseversion\'/g" segmenttestapp/build.gradle

version_code=$(grep -E 'VERSION_CODE=([0-9]+)' gradle.properties | grep -o '[0-9]\+')
version_code=$((version_code+1)) 
sed -r -i '' "s/VERSION_CODE=([0-9]+)/VERSION_CODE=$version_code/g" gradle.properties

sed -r -i '' "s/VERSION_NAME=([0-9]+\.[0-9]+\.[0-9]+)/VERSION_NAME=$releaseversion/g" gradle.properties

sed -r -i '' "s/(Built with AppsFlyer Android SDK.*)([0-9]+\.[0-9]+\.[0-9]+)(.*)/\1$releaseversion\3/g" Readme.md
sed -r -i '' "s/(.*appsflyer:segment-android-integration:)([0-9]+\.[0-9]+\.[0-9]+)(.*)/\1$releaseversion\3/g" Readme.md
sed -r -i '' "s/(.*Maven Central.*)([0-9]+\.[0-9]+\.[0-9]+)(.*)([0-9]+\.[0-9]+\.[0-9]+)(.*)/\1$releaseversion\3$releaseversion\5/g" Readme.md

touch "releasenotes.$releaseversion"
