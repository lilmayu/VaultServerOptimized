package dev.mayuna.vso.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.mayuna.vso.VsoConfig;
import dev.mayuna.vso.VsoMod;
import dev.mayuna.vso.model.UuidAndSyncTime;
import iskallia.vault.quest.type.CollectionQuest;
import net.minecraft.server.level.ServerPlayer;

@Mixin(CollectionQuest.class)
public class CollectionQuestMixin {

    @Unique
    private static final List<UuidAndSyncTime> vaultserveroptimized$lastQuestCollection = new ArrayList<>();

    @Inject(method = "queryCollection", at = @At("HEAD"), cancellable = true, remap = false)
    public void queryCollection(ServerPlayer player, CallbackInfo ci) {
        if (!VsoConfig.questCollectionMixinEnabled) {
            return; // Ignore, mixin is disabled
        }

        UUID playerUuid = player.getUUID();

        var lastQuestCollection = vaultserveroptimized$lastQuestCollection.stream()
            .filter(x -> Objects.equals(x.getUuid(), playerUuid))
            .findFirst()
            .orElse(null);

        if (lastQuestCollection != null) {
            if (System.currentTimeMillis() - lastQuestCollection.getTimeMillis() < 1000) {
                // Less than 1 seconds since last collection -> cancel
                //VsoMod.LOGGER.info("Skipping quest collection for player {}", player.getName().getString());
                ci.cancel();
                return;
            }
        } else {
            lastQuestCollection = new UuidAndSyncTime(playerUuid);
            vaultserveroptimized$lastQuestCollection.add(lastQuestCollection);
        }

        lastQuestCollection.setTimeMillis(System.currentTimeMillis());
        // Continue with the execution
        //VsoMod.LOGGER.info("Querying quest collection for player {}", player.getName().getString());
    }
}
