#!/bin/bash

releaseversion=$1

sed -i '' 's/^/* /' "releasenotes.$releaseversion"
NEW_VERSION_RELEASE_NOTES=$(cat "releasenotes.$releaseversion")
NEW_VERSION_SECTION="### $releaseversion\n$NEW_VERSION_RELEASE_NOTES\n\n"
echo -e "$NEW_VERSION_SECTION$(cat RELEASENOTES.md)" > RELEASENOTES.md

rm -r "releasenotes.$releaseversion"

sed -E -i '' "s/VERSION_NAME=([0-9]+\.[0-9]+\.[0-9]+).*/VERSION_NAME=$appsflyerversion/g" gradle.properties

sed -E -i '' "s/(POM_ARTIFACT_ID=.*)-beta/\1/g" gradle.properties