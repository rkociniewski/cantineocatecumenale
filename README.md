# Neocatechumenal Song Scraper

[![version](https://img.shields.io/badge/version-1.1.8-yellow.svg)](https://semver.org)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)
[![Build](https://github.com/rkociniewski/cantineocatecumenale/actions/workflows/main.yml/badge.svg)](https://github.com/rkociniewski/cantineocatecumenale/actions/workflows/main.yml)
[![codecov](https://codecov.io/gh/rkociniewski/cantineocatecumenale/branch/main/graph/badge.svg)](https://codecov.io/gh/rkociniewski/cantineocatecumenale)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-blueviolet?logo=kotlin)](https://kotlinlang.org/)
[![Gradle](https://img.shields.io/badge/Gradle-9.10-blue?logo=gradle)](https://gradle.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-greem.svg)](https://opensource.org/licenses/MIT)

This Kotlin-based project is a web scraper for downloading MP3 songs from
the [Neocatechumenal Way songbook website](https://www.cantineocatecumenale.it). It is designed to extract, translate,
and download religious songs with Bible references.

## Features

- Scrapes all songs from paginated lists.
- Extracts song titles and optional Bible references.
- Translates Bible references into Polish equivalents.
- Downloads MP3 files to a local directory.
- Opens the output directory after downloading.
- Uses coroutines with throttling and retries to avoid 429 errors.
- Logs to both the console and a file (`logs/application.log`) using Kotlin Logging and Logback.

## Technologies Used

- Kotlin 2.1.21
- kotlinx.coroutines
- Jsoup
- Kotlin Logging (`kotlin-logging-jvm`)
- Logback (`logback-classic`)

## Setup

1. Clone the repository:
   ```bash
   git clone <your-repo-url>
   cd <your-project-dir>
   ```

2. Make sure you have Java 21+ and Kotlin installed.

3. Create a `logback.xml` in `src/main/resources/` or use the one already provided.

4. Run the scraper:
   ```bash
   ./gradlew run
   ```

## Output

- All downloaded `.mp3` files are saved in: `~/cantineocatecumenale`
- A log file is written to: `logs/application.log`

## License

This project is licensed under the MIT License.

## Built With

* [Gradle](https://gradle.org/) - Dependency Management

## Versioning

We use [SemVer](http://semver.org/) for versioning.

## Authors

* **Rafał Kociniewski** - [PowerMilk](https://github.com/rkociniewski)
