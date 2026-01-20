# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
- Migrated rendering system to use `text-styling-lib`.
- Replaced `FontConfig` with `TextStylingProfile` in Theme system.
- Updated `RenderContext` to support `Component`-based rendering.
- Refactored `ObjectiveRenderer`, `QuestEntryRenderer`, `ToastRenderer`, `CompassRenderer` to use `Component` and `Style`.
- Added backward compatibility layer in `RenderContext` for legacy String rendering.

### Removed
- Deleted `FontConfig` class.
