#!/bin/bash

releaseversion=$1

sed -r -i '' "s/version_release = \'[0-9]+\.[0-9]+\.[0-9]+\'/version_release = \'$releaseversion\'/g" segment-appsflyer-ios.podspec

sed -r -i '' "s/(.*pod \'segment-appsflyer-ios\')(.*)/\1,\'$releaseversion\'/g" examples/SwiftPodsSample/Podfile
sed -r -i '' "s/(.*pod \'segment-appsflyer-ios\')(.*)/\1,\'$releaseversion\'/g" examples/ObjcPodsSample/Podfile

sed -i '' 's/^/* /' "releasenotes.$releaseversion"
NEW_VERSION_RELEASE_NOTES=$(cat "releasenotes.$releaseversion")
NEW_VERSION_SECTION="### $releaseversion\n* Updated iOS SDK to v$releaseversion\n$NEW_VERSION_RELEASE_NOTES\n\n"
echo -e "$NEW_VERSION_SECTION$(cat RELEASENOTES.md)" > RELEASENOTES.md

rm -r "releasenotes.$releaseversion"