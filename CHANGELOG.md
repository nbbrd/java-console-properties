# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed

- Fix potential bugs related to system settings
- Fix NoSuchFileException when output parent directories are nonexistent [#342](https://github.com/nbbrd/java-console-properties/issues/342)

## [1.4.0] - 2022-10-19

### Added

- Add new Text IO API

### Fixed

- Fix description of `TextInputOptions#file`

## [1.3.4] - 2021-10-18

### Fixed

- Fix support of csv comment
- Fix encoding auto-detection in ConsoleProperties

## [1.3.3] - 2021-09-28

### Fixed

- Fix process inheritance when getting console height & width

## [1.3.2] - 2021-03-24

### Changed

- Migration to Maven-Central
- Maven groupId is now `com.github.nbbrd.java-console-properties`

## [1.3.1] - 2021-01-25

### Fixed

- Fix picocli errors at compile-time

## [1.3.0] - 2021-01-19

### Added

- Add Excel and Obs tools
- Add generic profiles

## [1.2.0] - 2020-10-01

### Added

- Add append option to TextOutput
- Add lenientSeparator property to CsvInput
- Add java-path option to GenerateLauncher
- Add PowerShell script generation
- Add gzip option to TextInput and TextOutput
- Add shortcuts and auto-gzip in TextInput and TextOutput

### Changed

- Improve executable jar lookup
- Reorder TextOutputOptions

## [1.1.2] - 2020-09-25

### Fixed

- Fix NPE when looking for config file on system scope

## [1.1.1] - 2020-08-11

### Fixed

- Fix root command name in picocli tools

## [1.1.0] - 2020-06-11

### Added

- Add several command line tools

## [1.0.0] - 2020-03-26

### Added

- Initial release

[Unreleased]: https://github.com/nbbrd/java-console-properties/compare/v1.4.0...HEAD
[1.4.0]: https://github.com/nbbrd/java-console-properties/compare/v1.3.4...v1.4.0
[1.3.4]: https://github.com/nbbrd/java-console-properties/compare/v1.3.3...v1.3.4
[1.3.3]: https://github.com/nbbrd/java-console-properties/compare/v1.3.2...v1.3.3
[1.3.2]: https://github.com/nbbrd/java-console-properties/compare/v1.3.1...v1.3.2
[1.3.1]: https://github.com/nbbrd/java-console-properties/compare/v1.3.0...v1.3.1
[1.3.0]: https://github.com/nbbrd/java-console-properties/compare/v1.2.0...v1.3.0
[1.2.0]: https://github.com/nbbrd/java-console-properties/compare/v1.1.2...v1.2.0
[1.1.2]: https://github.com/nbbrd/java-console-properties/compare/v1.1.1...v1.1.2
[1.1.1]: https://github.com/nbbrd/java-console-properties/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/nbbrd/java-console-properties/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/nbbrd/java-console-properties/releases/tag/v1.0.0
