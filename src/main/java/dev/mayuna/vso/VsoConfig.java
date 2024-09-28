package dev.mayuna.vso;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod.EventBusSubscriber(modid = VsoMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VsoConfig {

    public static final ForgeConfigSpec SPECS;

    // Settings
    public static final BooleanValue QUEST_COLLECTION_MIXIN_ENABLED;
    public static boolean questCollectionMixinEnabled;
    public static final BooleanValue CARD_DECK_CACHE_MIXIN_ENABLED;
    public static boolean cardDeckCacheMixinEnabled;
    public static final BooleanValue ABILITY_TREE_MIXIN_ENABLED;
    public static boolean abilityTreeMixinEnabled;

    static {
        var builder = new ForgeConfigSpec.Builder();

        // vso
        builder.push("vso");

        QUEST_COLLECTION_MIXIN_ENABLED = builder.comment("Enable Quest Collection Mixin optimizations").define("questMixinEnabled", true);
        CARD_DECK_CACHE_MIXIN_ENABLED = builder.comment("Enable Card Deck Cache Mixin optimizations").define("cardDeckCacheMixinEnabled", true);
        ABILITY_TREE_MIXIN_ENABLED = builder.comment("Enable Ability Tree Mixin optimizations").define("abilityTreeMixinEnabled", true);

        // vso
        builder.pop();

        SPECS = builder.build();
    }

    public static boolean isLoaded() {
        return SPECS.isLoaded();
    }

    public static void load() {
        if (isLoaded()) {
            return;
        }

        VsoMod.LOGGER.info("Loading config...");

        // FORCE LOAD
        var path = FMLPaths.CONFIGDIR.get().resolve("vso.toml");
        try {
            final var configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();

            configData.load();
            SPECS.setConfig(configData);
            updateCache(null);
        } catch (Exception e) {
            var file = path.toFile();
            if (!file.exists()) throw new RuntimeException("Failed to read configuration file");
            if (!file.delete()) throw new RuntimeException("Failed to remove corrupted configuration file");
            load();
        }
    }

    /**
     * Saves config
     */
    public static void save() {
        SPECS.save();
    }

    @SubscribeEvent
    public static void updateCache(ModConfigEvent ignored) {
        VsoMod.LOGGER.info("Updating config cache...");

        questCollectionMixinEnabled = QUEST_COLLECTION_MIXIN_ENABLED.get();
        cardDeckCacheMixinEnabled = CARD_DECK_CACHE_MIXIN_ENABLED.get();
        abilityTreeMixinEnabled = ABILITY_TREE_MIXIN_ENABLED.get();
    }
}
