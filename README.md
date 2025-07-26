# Number Sequence Trainer
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/achawki/number-sequence-trainer-android/blob/master/LICENSE)
[![Unit Tests](https://github.com/achawki/number-sequence-trainer-android/workflows/Unit%20Tests/badge.svg)](https://github.com/achawki/number-sequence-trainer-android/actions/workflows/unit-tests.yml)
[![Android CI](https://github.com/achawki/number-sequence-trainer-android/workflows/Android%20CI/badge.svg)](https://github.com/achawki/number-sequence-trainer-android/actions/workflows/android-ci.yml)

*Practise number sequences on Android. The `Number Sequence Trainer` will generate sequences randomly*. 

<a href='https://play.google.com/store/apps/details?id=com.achawki.sequencetrainer&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' height="70"/></a>

## Features

<img  src="./gifs/demo.gif" alt="demo" width="200"/>

- Support for dark mode
- Different difficulties
- User statistics
- Support for English and German
- No internet access required (data is only stored locally)


## Build APK

1. Clone the project
```
git clone https://github.com/achawki/number-sequence-trainer-android
```
2. Build (or import into [Android Studio](https://developer.android.com/studio/))
```
./gradlew assemble
```

3. APK can be found in `./app/build/outputs/apk/`

## Running tests

```
./gradlew test
```

Run instrumentation tests on a connected device

```
./gradlew connectedAndroidTest
```

