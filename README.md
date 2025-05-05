# Neocatechumenal Song Scraper

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

- Kotlin 1.9+
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

This project is intended for private, non-commercial use. Please respect the content rights of the original source.

## Disclaimer

This tool is provided as-is. The target site may change its structure or implement stronger anti-bot protections.
