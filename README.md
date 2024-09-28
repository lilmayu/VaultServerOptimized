# VaultServerOptimized

Some optimizations to help with Vault Hunter's poor performance decisions.

## Current optimizations
- [AbilityTreeMixin](https://github.com/lilmayu/VaultServerOptimized/blob/main/src/main/java/dev/mayuna/vso/mixin/AbilityTreeMixin.java)
  - Syncs up ability trees every 2 seconds instead of 0.5 seconds
  - Helps with connectivity issues - sending just bit too much and netty threads can't keep up.
  - Also, should leave more processing power to ticking the world.
- [CardDeckCacheMixin](https://github.com/lilmayu/VaultServerOptimized/blob/main/src/main/java/dev/mayuna/vso/mixin/CardDeckCacheMixin.java)
  - Caches card decks. Vault Hunters is getting them quite often - more than 120 times per second to be exact - and every time their NBT is reloaded. This causes *big* performance issues.
  - The cache is valid just for one second but I think it could be extended to more - the client updates the tooltip etc. on its own so the delay does not actually matter to players.
- [CollectionQuestMixin](https://github.com/lilmayu/VaultServerOptimized/blob/main/src/main/java/dev/mayuna/vso/mixin/CollectionQuestMixin.java)
  - For some reason, quests that require collecting items were checked every second. I expanded this time to 5 seconds.
  - Further optimizations might include gameplay breaking stuff - checking just player's inventory, not their backpacks etc. - but it would virtually remove all lag caused by the checks.

## Installation
Drop the mod inside mods directory.

## Configuration
Upon first server boot with the mod, it creates vso.toml. You can disable / enable mixins there. They will be still loaded, but inactive - they won't alter default behavior.
