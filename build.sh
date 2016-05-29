: ${SIGN_KEYSTORE?"Need to set SIGN_KEYSTORE"}
: ${SIGN_PASSWORD?"Need to set SIGN_PASSWORD"}
: ${FEEDBACK_TELEGRAM?"Need to set FEEDBACK_TELEGRAM"}

export SING_BUILD=$(git rev-list --count master)0

./gradlew clean assembleRelease

 echo App version = $SING_BUILD