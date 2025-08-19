# Catalyst

# Maybe you're looking for the [b1.7.3 StAPI](https://github.com/MartinSVK12/catalyst-stapi) version instead?

**Library for advanced modding needs of BTA**

Catalyst is split into modules, the current available modules are described below. 

It can be downloaded and used by downloading the **Core** module mod + other required modules mods, or it can be downloaded as one mod with all the modules (Catalyst: All).

Each module has an individual version! Not every module is updated each release.

The all modules package cannot be used in development, you have to implement the modules you want to use when making your own mod separately.

Available modules:
- Core
  - The core module, **every other module depends on this**.
  - Adds misc utility classes for modules and other mods to use.
  - Makes creating GUIs that work in MP easier.
  - Adds a block network system.
- Fluids
  - Improved api for fluid storage and transportation.
- Energy
  - Adds 2 types of configurable energy APIs.
    - Simple: RF-style system, energy is just a number that can be generated or consumed.
    - Electric: GT-style system, incorporates voltage and amperage. 
- Multiblocks
  - Adds support for multiblock structures/machines.
- Effects
  - API for custom attributes and stackable effects.
- Multipart
  - API and implementation for multiple block parts in one block.
