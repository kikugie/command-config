# Command Config Lib

Lightweight command-based config library.

## Why commands?
Commands are rarely affected by Minecraft updates, making this library compatible with most versions, including snapshots.  
For example, it can be used for:
- Quick in-game config, as it doesn't require opening another GUI.
- Server-side config. GUI functionality is limited for server mods, which leaves commands as a config option.
- Snapshot mod support. Other libraries mostly focus on full Minecraft releases, however this one is designed to be easy to port.
- Multi-version mods. Other libraries may not be available for old versions or have different API for them.

Note that this library doesn't aim to replace supported GUI config for your mod, but rather provide a robust alternative.

## How do I use this?
Check out the wiki: https://docs.kikugie.dev