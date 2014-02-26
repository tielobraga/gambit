# Gambit

This repository contains the source code for the Gambit Android app [available on Google Play][Google Play link]. Gambit is a really simple flashcards viewer and manager for Android.

[![Screenshot][Screenshot image]][Google Play link]

## License

* [Apache Version 2.0][Apache license link]

## Building

You will need JDK 1.6, Android SDK 22 and Gradle 1.10 installed.

1. Install required Android components.

  ```
  $ android update sdk --no-ui --force --all --filter build-tools-19.0.1
  $ android update sdk --no-ui --force --all --filter android-19
  $ android update sdk --no-ui --force --all --filter extra-android-m2repository
  $ android update sdk --no-ui --force --all --filter extra-google-m2repository
  ```

2. Build application.

  ```
  $ gradle clean assembleDebug
  ```

## Acknowledgements

Gambit uses some open source libraries and tools:

* [Commons IO][Commons IO link]
* [Otto][Otto link]
* [Seismic][Seismic link]
* [TransitionsBackport][TransitionsBackport link]
* [ViewPagerIndicator][ViewPagerIndicator link]


  [Google Play badge image]: http://www.android.com/images/brand/get_it_on_play_logo_large.png
  [Screenshot image]: https://f.cloud.github.com/assets/200401/2264233/a10084aa-9e6f-11e3-90f7-f4bd5dcceb14.png

  [Google Play link]: https://play.google.com/store/apps/details?id=ru.ming13.gambit
  [Apache license link]: http://www.apache.org/licenses/LICENSE-2.0.html
  [Commons IO link]: http://commons.apache.org/proper/commons-io
  [Otto link]: http://square.github.com/otto
  [Seismic link]: https://github.com/square/seismic
  [TransitionsBackport link]: https://github.com/guerwan/TransitionsBackport
  [ViewPagerIndicator link]: http://viewpagerindicator.com
