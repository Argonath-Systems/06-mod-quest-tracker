# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **QuestBookPageAdapter** - Quest journal UI component (2026-01-29)
  - Moved from `02-adapter-hytale` as part of architectural remediation
  - Quest book interface management (open/close/update)
  - Placeholder for HyUI PageBuilder integration
  - **Reason**: Quest book UI is quest tracker mod-specific, not generic adapter concern
- Initial project structure
- Core functionality implementation

### Changed
- **BREAKING**: Migrated from programmatic UI rendering to HYUIML templates
  - Replaced `QuestTrackerHUD.render(RenderContext)` with `HyuimlQuestTrackerHUD.generateHtml()`
  - Old `render()` method removed in favor of template-based generation
- Added `HyuimlQuestTrackerHUD` class for HYUIML-based HUD generation
- Added UI hot reload support for development mode
  - Edit HYUIML files and see changes without restart
  - Use `/uireload quest-tracker-hud` to force reload
- Added `quest-tracker-hud.hyuiml` template file in resources
- Added integration with `UnifiedUIManager` for hot reload
- Added per-player HUD tracking for targeted refresh

### Deprecated

### Removed

### Fixed

### Security

## [1.0.0] - 2026-01-25

### Added
- Initial release
- Core functionality implemented
- Documentation and examples
- Build system configured

[Unreleased]: https://github.com/Argonath-Systems/06-mod-quest-tracker/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/Argonath-Systems/06-mod-quest-tracker/releases/tag/v1.0.0
