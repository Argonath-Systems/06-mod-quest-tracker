# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **Command Registration (2026-02-01)** - Quest tracking commands
  - `/quest list|log` - Show all active quests with objectives
  - `/quest track <id>` - Track a specific quest in HUD
  - `/quest untrack <id>` - Stop tracking a quest
  - `/quest toggle` - Toggle tracker visibility
  - `/questlog` - Alias for `/quest list`
  - `/qtrack` - Quick toggle for tracker visibility

### Added (Previous)
- **Keybinding System (QT-006)** - Full keybind framework (2026-01-30)
  - `KeybindConfig` record with default keybindings per specification
  - `KeybindAction` enum for all keybind actions (toggle, expand, cycle, menu, etc.)
  - `KeybindManager` for handler registration and triggering
  - `KeybindDescriptor` record for adapter registration
  - Modifier key support (SHIFT, CTRL, ALT)
  - Menu-context-only actions for in-menu keybinds
- **Context Awareness (QT-L2-003)** - Immersive auto-hide behavior (2026-01-30)
  - `ImmersiveConfig` record for immersive mode settings
  - `ContextManager` for tracking combat/dialogue/cutscene states
  - Visibility and opacity listener support
  - Idle timeout with opacity fade (5s default, 80% opacity)
  - Manual toggle override support
- **HudReference** - Type-safe HUD wrapper implementing `UIContext` (2026-01-30)
  - Replaces `Object` type for player HUD references
  - Type-safe `getHudAs(Class<T>)` method
  - Visibility and age tracking
- **QuestBookPageAdapter** - Quest journal UI component (2026-01-29)
  - Moved from `02-adapter-hytale` as part of architectural remediation
  - Quest book interface management (open/close/update)
  - Placeholder for HyUI PageBuilder integration
  - **Reason**: Quest book UI is quest tracker mod-specific, not generic adapter concern
- Initial project structure
- Core functionality implementation

### Changed
- **BREAKING**: `WaypointManager.findNearest()` now returns `Optional<QuestWaypoint>` instead of nullable
- **BREAKING**: `QuestTrackerMod.registerPlayerHud()` now requires `HudReference` instead of `Object`
- **BREAKING**: `QuestTrackerMod.getPlayerHud()` now returns `HudReference` instead of `Object`
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
- **BREAKING**: `QuestWaypoint.formatDistance()` no-arg instance method removed (use static `formatDistance(double)` instead)

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
