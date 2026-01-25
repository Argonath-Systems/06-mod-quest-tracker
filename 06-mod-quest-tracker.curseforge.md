# Quest Tracker UI - Interactive Quest Management for Hytale

## 📋 Project Information

**Title:** Quest Tracker UI - RPG Quest Management  
**Project Type:** Hytale Mod  
**Current Version:** 1.0.0  
**Hytale Version:** Alpha 1.0+  
**Dependencies:** HyUI 0.4.6+  
**License:** All Rights Reserved

## 🎯 Purpose

**Quest Tracker UI** is a comprehensive quest management mod for Hytale that provides players with an immersive, RPG-style interface to track, manage, and complete quests. Built on the HyUI framework, it features a full-screen quest browser, detailed quest pages, real-time progress tracking, and a customizable HUD widget.

Perfect for adventure servers, RPG gameplay, and story-driven experiences on Argonath Systems servers.

## 📦 What's Included

- **Quest List View** with filtering (Available, Active, Completed)
- **Quest Details Page** with objectives, rewards, and lore
- **Real-Time Progress Tracking** with live updates
- **Customizable HUD Widget** showing active quests
- **Quest Categories** (Main, Side, Daily, World Events, Guild, Dungeon)
- **Objective Types** (Kill, Collection, Interaction, Location, Crafting, Custom)
- **Quest Waypoints** (planned v1.1.0)
- **Quest Sharing** (planned v1.1.0)

## ✨ Key Features

### 🔍 Quest List View

Browse all available, active, and completed quests with powerful filtering:

- **Filters**: All, Available, Active, Completed, Failed
- **Search**: Find quests by name or description
- **Sort**: By name, level, reward, or progress
- **Categories**: Filter by quest type (Main/Side/Daily/etc.)

### 📖 Quest Details Page

Click any quest to view comprehensive details:

- **Quest Lore**: Full description and backstory
- **Objectives List**: All requirements with real-time checkboxes
- **Rewards Display**: Experience, gold, items, and special rewards
- **Prerequisites**: Required quests or conditions to unlock
- **Recommended Level**: Suggested player level
- **Time Limits**: For daily quests and events

### 📊 Real-Time Progress Tracking

All quest progress updates instantly:

```
Kill 10 Orcs                     [✓ 7/10]
Collect 5 Ancient Scrolls        [✓ 3/5]
Talk to Gandalf                  [  ]
```

Checkbox animations and color-coded progress bars make tracking satisfying!

### 🎯 HUD Widget

Always see your active quests on-screen:

- **Position**: Configurable (Top-Right, Top-Left, Bottom-Right, Bottom-Left)
- **Scale**: Adjustable size (0.5x to 2.0x)
- **Max Quests**: Show up to 3 active quests at once
- **Drag-and-Drop**: Move the widget anywhere
- **Auto-Hide**: Collapse when not in combat (optional)

### 🏷️ Quest Categories

#### Main Quests
Primary storyline quests with unique rewards and progression gates.

#### Side Quests
Optional quests for extra experience and lore.

#### Daily Quests
Repeatable quests that reset every 24 hours.

#### World Events
Server-wide events with group objectives.

#### Guild Quests
Faction-specific quests for guild members.

#### Dungeon Quests
Challenging multi-objective quests in dungeons.

## 🚀 Installation

### CurseForge (Recommended)

1. Download from CurseForge
2. Place `quest-tracker-1.0.0.jar` in your `mods/` folder
3. Install **HyUI 0.4.6+** (required dependency)
4. Restart Hytale

### Manual Build

```bash
git clone https://github.com/Argonath-Systems/06-mod-quest-tracker.git
cd 06-mod-quest-tracker
mvn clean package
cp target/quest-tracker-1.0.0.jar <hytale-dir>/mods/
```

## 📖 Usage Guide

### Commands

| Command | Description |
|---------|-------------|
| `/quests open` | Open the quest tracker UI |
| `/quests active` | List all active quests |
| `/quests completed` | View completed quests |
| `/quests abandon <quest>` | Abandon a quest |
| `/quests hud toggle` | Show/hide the HUD widget |
| `/quests hud config` | Configure HUD settings |
| `/quests hud edit` | Enter drag-and-drop edit mode |

### HUD Controls

**Toggle HUD**: Press `Q` (default hotkey)  
**Edit Mode**: `/quests hud edit` then drag the widget  
**Reset Position**: `/quests hud reset`

### Quest Filters

**All**: Show all quests regardless of status  
**Available**: Quests you can accept  
**Active**: Quests currently in progress  
**Completed**: Successfully finished quests  
**Failed**: Abandoned or failed quests

## ⚙️ Configuration

### quest-tracker.yaml

```yaml
hud:
  enabled: true
  position: TOP_RIGHT      # TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT
  scale: 1.0               # 0.5 to 2.0
  max_quests: 3            # Maximum number of quests shown on HUD
  auto_hide: false         # Auto-hide when not in combat
  
ui:
  theme: LOTR_PARCHMENT    # LOTR_PARCHMENT, DARK, LIGHT, CUSTOM
  font_size: 14            # Font size in pixels
  show_completed: true     # Show completed quests in the list
  show_failed: false       # Show failed quests in the list
  
notifications:
  quest_start: true        # Show notification when quest starts
  quest_progress: true     # Show notification on objective completion
  quest_complete: true     # Show notification on quest completion
  sound_effects: true      # Play sounds for quest events
```

### hotkeys.yaml

```yaml
hotkeys:
  open_tracker: Q          # Open quest tracker UI
  toggle_hud: SHIFT+Q      # Toggle HUD visibility
  next_quest: ]            # Switch to next quest in HUD
  prev_quest: [            # Switch to previous quest in HUD
```

## 📸 Screenshots

![Quest Browser](screenshots/quest-browser.png)
*The main quest list view with filters and search*

![Quest Details](screenshots/quest-details.png)
*Detailed quest page showing objectives and rewards*

![HUD Widget](screenshots/hud-widget.png)
*In-game HUD overlay showing active quests*

![Quest Complete](screenshots/quest-complete.png)
*Quest completion celebration screen*

![Quest Categories](screenshots/quest-categories.png)
*Category filter view*

## 🏗️ Architecture

```
Quest Tracker Mod
    ├── UI Framework (HyUI)
    ├── Quest Framework (Business Logic)
    ├── Objective Framework (Progress Tracking)
    ├── Condition Framework (Requirements)
    └── Hytale Adapter (Platform Implementation)
```

Built using the **Zero Hytale Imports** architecture for maintainability.

## 🎮 Quest Types

### Main Story Quests
Epic narrative quests that advance the server storyline.

### Side Quests
Additional content for exploration and lore discovery.

### Daily Quests
Repeatable tasks for daily rewards (resets at midnight UTC).

### World Events
Large-scale server events requiring multiple players.

### Guild Quests
Faction-specific objectives for guild members.

### Dungeon Quests
Multi-objective challenges inside dungeons.

## 🎯 Objective Types

### Kill Objectives
Defeat specific enemies or enemy types:
- ✓ Kill 10 Orcs
- ✓ Defeat the Dragon Boss

### Collection Objectives
Gather items or resources:
- ✓ Collect 5 Ancient Scrolls
- ✓ Harvest 20 Mithril Ore

### Interaction Objectives
Talk to NPCs or interact with objects:
- ✓ Talk to Gandalf
- ✓ Activate 3 Ancient Pillars

### Location Objectives
Discover or reach specific locations:
- ✓ Discover the Mines of Moria
- ✓ Reach the Summit of Mount Doom

### Crafting Objectives
Create specific items:
- ✓ Craft an Elven Sword
- ✓ Brew 5 Healing Potions

### Custom Objectives
Script-based objectives for unique requirements:
- ✓ Complete without taking damage
- ✓ Finish within 10 minutes

## 🏷️ Tags

`quests` `quest-tracker` `ui` `hud` `rpg` `quest-system` `objectives` `rewards` `hytale` `argonath` `argonath` `gameplay` `hyui` `adventure` `storytelling` `tracking`

## 🔗 Dependencies

**Required:**
- [HyUI 0.4.6+](https://curseforge.com/hytale/mods/hyui) - UI framework
- Hytale Alpha 1.0+

**Optional:**
- [Waypoint Mod](https://curseforge.com/hytale/mods/waypoints) - Quest waypoint integration (planned v1.1.0)

## 🔗 Related Projects

- [Quest Framework](https://github.com/Argonath-Systems/05-framework-quest) - Core quest system library
- [HyUI Framework](https://github.com/Argonath-Systems/HyUI) - UI framework
- [Argonath Systems](https://github.com/Argonath-Systems) - Full server ecosystem

## 🌐 Links

- **CurseForge**: https://curseforge.com/hytale/mods/quest-tracker
- **GitHub**: https://github.com/Argonath-Systems/06-mod-quest-tracker
- **Wiki**: https://github.com/Argonath-Systems/06-mod-quest-tracker/wiki
- **Issues**: https://github.com/Argonath-Systems/06-mod-quest-tracker/issues
- **Discord**: [LOTT Community Discord]

## 🗺️ Roadmap

### Version 1.1.0 (Q2 2025)
- Quest waypoints and map markers
- Quest sharing between players
- Quest journal with notes
- Custom quest creation API

### Version 1.2.0 (Q3 2025)
- Quest chains visualization
- Achievement integration
- Quest voice-over support
- Mobile companion app

### Version 2.0.0 (Q4 2025)
- Quest editor GUI
- Procedural quest generation
- Multi-language support
- Advanced statistics and analytics

## 📄 License

Copyright © 2025 Argonath Systems. All rights reserved.

## 👥 Authors & Contributors

- **Argonath Systems Team**
- **HytaleModder AI Agent**
- Special thanks to the HyUI community

## 📝 Changelog

### Version 1.0.0 (2025-01-25)

**Features:**
- Quest list view with filtering
- Quest details page with objectives and rewards
- Real-time progress tracking
- Customizable HUD widget
- 6 quest categories (Main, Side, Daily, World Events, Guild, Dungeon)
- 6 objective types (Kill, Collection, Interaction, Location, Crafting, Custom)
- Configuration system for HUD and UI
- Hotkey support
- Quest notifications and sound effects

**Documentation:**
- Complete user guide
- Configuration documentation
- Developer API documentation
- CurseForge project page

**Technical:**
- Built on HyUI 0.4.6+
- Zero Hytale Imports architecture
- Full unit test coverage
- CI/CD pipeline with GitHub Actions

## 🆘 Support

- **GitHub Issues**: https://github.com/Argonath-Systems/06-mod-quest-tracker/issues
- **Discord**: [LOTT Community Discord]
- **Wiki**: https://github.com/Argonath-Systems/06-mod-quest-tracker/wiki
- **Email**: support@argonathsystems.com

## 🤝 Contributing

We welcome contributions! See [CONTRIBUTING.md](../CONTRIBUTING.md) for:
- Code style guidelines
- Pull request process
- Issue reporting
- Feature requests

## 💬 FAQ

**Q: Can I use this on my own server?**  
A: Currently, this mod is designed for Argonath Systems servers. Contact us for licensing.

**Q: Does this work with other quest mods?**  
A: Yes! The Quest Framework API is extensible and can integrate with other quest systems.

**Q: How do I create custom quests?**  
A: See the [Quest Creation Guide](https://github.com/Argonath-Systems/06-mod-quest-tracker/wiki/Quest-Creation) in our wiki.

**Q: Can players create their own quests?**  
A: Planned for v2.0.0 with the Quest Editor GUI.

## 🎨 Themes

### LOTR Parchment (Default)
Immersive medieval parchment theme with sepia tones.

### Dark Theme
Modern dark interface for late-night questing.

### Light Theme
Clean, bright interface for daytime play.

### Custom Theme
Use the theme editor to create your own color scheme!
