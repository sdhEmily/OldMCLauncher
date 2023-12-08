# OldMCLauncher
OldMCLauncher is a fork of [OLauncher](https://github.com/olauncher/olauncher/) with all the olauncher related branding removed for a more authentic experience.

## How to use
1. Go to the [latest release](https://github.com/sdhEmily/OldMCLauncher/releases/latest)
2. Download the `OldMCLauncher-xxx-redist.jar` file
3. Run it

### But the launcher is broken :(
Have you used the new launcher in this .minecraft directory? If so, switch .minecraft directories or delete/rename the `launcher_profiles.json` file. You will have to recreate your minecraft profiles, as the new `launcher_profiles.json` format is incompatible with this version.

## Features
- Microsoft authentication
- Bundled JVMs
  - Automatically downloads the appropriate JVM for all minecraft versions
  - You just need a runtime to open the actual launcher
  - You can still provide your own JVMs
- Update checking

## Planned Features
- Add skin/cape management

### Minor TODOs
- Replace Mojang's Hopper service with our own
- Support demo users (if they have a Microsoft account but no Minecraft profile)
- Use `version_manifest_v2`

## How to build from source
The commands must be run in the following order to build from source:
- `decompile.sh`
  - Downloads original jar and decompiles it
- `init.sh`
  - Turns decompiled sources into a git repository
- `applyPatches.sh`
  - Applies OLauncher patches to the decompiled sources
- `mvn clean package`
  - Compiles the patched launcher
- `genredist.sh` (optional)
  - Generates the redistributable JAR - Do not distribute the JARs in `olauncher/target`!
