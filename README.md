# Persian Chronological Date

A Java library for working with the Persian (Jalali) calendar system, providing date calculation, conversion, and localization support. This project aims to offer a robust and standards-compliant implementation of the Persian calendar, compatible with Java's `java.time.chrono` API.

## Features

- **Persian LocalDate**: Drop-in replacement for `java.time.LocalDate` for the Persian calendar.
- **Leap Year Calculation**: Accurate leap year logic for the Jalali calendar.
<!-- - **Date Conversion**: Convert between Persian and ISO (Gregorian) dates. -->
- **Chronology and Era Support**: Implements `Chronology`, `Era`, and related interfaces.
- **Localization**: Era and date names localized using CLDR data.
- **Unit Tested**: Comprehensive test coverage for date logic.

## Getting Started

### Requirements

- Java 17 or higher
- Maven or Gradle for building

### Usage

Add the library to your project and use `xyz.khooz.chrono.persian.LocalDate`:

```java
import xyz.khooz.chrono.persian.LocalDate;

LocalDate persianDate = new LocalDate(1403, 3, 12);
System.out.println(persianDate); // Outputs Persian date
```

### Example: Leap Year

```java
LocalDate date = new LocalDate(1399, 12, 30);
boolean isLeap = date.isLeapYear(); // true if 1399 is a leap year
```

<!-- ### Example: Conversion

```java
LocalDate persianDate = LocalDate.fromIsoDate(java.time.LocalDate.of(2025, 6, 2));
java.time.LocalDate isoDate = persianDate.toIsoDate();
``` -->

## Project Structure

- `src/main/java/xyz/khooz/chrono/persian/` - Core Persian calendar classes
- `src/main/java/xyz/khooz/chrono/persian/cldr/` - Localization and CLDR data handling
- `src/test/java/xyz/khooz/chrono/persian/` - Unit tests

## Running Tests

```sh
mvn test
```

or

```sh
./gradlew test
```

## License

***MIT License***

I am not an expert on licenses, but I want to retain some control over the project until it becomes a fully drop-in and compatible system for ISO Calendar chronology.

This means providing complete `LocalDate`, `LocalTime` and `LocalDateTime` implementations, conversion between `Instant` and these chronological objects (including `java.time.LocalDate`, `java.time.LocalTime`, and `java.time.LocalDateTime`), and seamless use with major JDBC drivers for date and time.

When most of these goals are achieved, I intend to re-release this repository as a Java library and Maven package with JPMS support, under a strong copyleft license.

## Acknowledgements

- [CLDR - Unicode Common Locale Data Repository](https://cldr.unicode.org/)
- [Java Time API](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html)

## Contributions

**Contributions welcome!** Please open issues or pull requests for improvements or bug fixes.

**Please be clear and code with intention.** Feel free to leave commented code or stubs as long as they do not create too much clutter and remain in development branches (and sometimes the main branch), but not in release branches. Comments and Javadocs are always welcome.

**Fork as a branch, merge as a branch.** We all dislike complicated merge conflicts. Therefore, I suggest forking and checking out new development branches, and merging those as development branches. This way, your pull requests can be processed faster with less supervision, allowing more attention to be given to the main and release branches.

**Suggestions for contribution:**

- Chronology conversion (comparison is already handled via `ChronoUnit.EPOCH_DAYS`)
- Better and leaner localization support. Less rigid dependencies and support for more localization systems are welcomed.
- A Java calendar system is more than welcome to complement the chronology.
- `LocalTime` and `LocalDateTime` implementations are in the plan, but I would personally appreciate it if this comes as a contribution.

### Also please answer these

- Should I change to `git flow` process and branch management?
- Are you comfortable with that? Does it bring additional value or save time?
