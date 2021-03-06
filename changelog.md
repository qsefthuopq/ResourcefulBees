•	Bee Json Changes
  o	color -> honeycombColor (optionally required)
  o	Added - primaryColor  (optionally required)
  o	Added - secondaryColor (optionally required)
  o	Added - primaryLayerTexture (optional)
  o	Added - secondaryLayerTexture (optional)
  o	Added - baseLayerTexture (optional)
  o	baseBlock -> mutationInput (optional)
  o	mutationBlock -> mutationOutput (optional)
  o	Added - isBeeColored (optionally required)
  o	Added - sizeModifier (optional)
  o	biomeList -> biomeWhitelist (optionally required)
  o	Added - biomeBlacklist (optional)
•	Config Changes
  o	Added - "GENERATE_BEE_NESTS" boolean
  o	Added - "BEE_NEST_GENERATION_WEIGHT" double (0.000 - 1.000)
•	Added additional null checks and other various NPE fixes.
•	Added Tiered Hives
  o	5 Tiers - Modifier affects maxCombs & maxBees
  o	Nest   = 0.5x
  o	Tier 1 = 1.0x
  o	Tier 2 = 1.5x
  o	Tier 3 = 2.0x
  o	Tier 4 = 4.0x
•	Added Nest Generation
  o	Nests only generate in biomes where apiaryBee spawns have been added
  o	Nest Generation can be disabled
  o	Nest generation has configurable weighting
  o	Variety of nest textures based on biome
•	Added Biome whitelist/blacklist support for more customizable spawning
•	Bees now have 2-color support
  o	Bees get a primary color and a secondary color
  o	Honeycombs now have their own color separate from apiaryBee colors
•	Bees have custom texture support
  o	custom textures can be specified for every apiaryBee including the base layer and each color layer
  o	supplied textures are used by default
•	Bees can now have custom sizes ranging from 0.5x -> 2.0x the normal apiaryBee size.
  o	child apiaryBee sizes scale accordingly.
  o	size defaults to 1.0
•	Various JEI compat fixes
•	Various ToP compat fixes
•	Various server/client sync fixes
•	Bee traits are now stackable
•	Added Spawn Eggs to JEI
•	Not specifying a “mainOutput” for a apiaryBee now skips honeycomb/block and centrifuge recipe generation
•	Added tag support for flowers
  o	Works exactly like mutation blocks
  o	Retains ALL/SMALL/TALL options
  o	Can be ANY block tag not just flowers
  o	Still only support single block or tag.
