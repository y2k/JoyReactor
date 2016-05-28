: ${SIGN_KEYSTORE?"Need to set SIGN_KEYSTORE"}
: ${SIGN_PASSWORD?"Need to set SIGN_PASSWORD"}

export SING_BUILD=$(git rev-list --count master)0

./gradlew clean assembleRelease