: ${SIGN_KEYSTORE?"Need to set SIGN_KEYSTORE"}
: ${SIGN_PASSWORD?"Need to set SIGN_PASSWORD"}
: ${FEEDBACK_TELEGRAM?"Need to set FEEDBACK_TELEGRAM"}
: ${GITHUB_AUTH?"Need to set GITHUB_AUTH"}

export SING_BUILD=$(git rev-list --count origin/master)0

./gradlew clean assembleRelease

# cd ios && ./build-frameworks.sh && cd ..
# ./gradlew clean build assembleRelease robovmArchive

UPLOAD_RESPONSE=$(curl --user $GITHUB_AUTH --data "{\"tag_name\":\"0.9.$SING_BUILD\"}" "https://api.github.com/repos/y2k/JoyReactor/releases")
ASSET_ID=$(echo $UPLOAD_RESPONSE | grep -oE "id\": \d+, \"tag" | grep -oE "\d+") 

# curl --user $GITHUB_AUTH \
# 	-T "ios/build/robovm/JoyReactor.ipa" \
#     -H "Content-Type: application/octet-stream" \
#     "https://uploads.github.com/repos/y2k/JoyReactor/releases/$ASSET_ID/assets?name=JoyReactor.ipa"
    
curl --user $GITHUB_AUTH \
	-T "android/build/outputs/apk/android-release.apk" \
    -H "Content-Type: application/octet-stream" \
    "https://uploads.github.com/repos/y2k/JoyReactor/releases/$ASSET_ID/assets?name=JoyReactor.apk"

 echo App version = $SING_BUILD