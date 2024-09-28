package dev.mayuna.vso.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.mayuna.vso.VsoConfig;
import dev.mayuna.vso.VsoMod;
import dev.mayuna.vso.model.UuidAndSyncTime;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import net.minecraft.server.level.ServerPlayer;

@Mixin(AbilityTree.class)
public class AbilityTreeMixin {

    @Unique
    private static final List<UuidAndSyncTime> vaultserveroptimized$lastSyncTimes = new ArrayList<>();

    @Inject(method = "sync", at = @At("HEAD"), cancellable = true, remap = false)
    public void sync(SkillContext context, CallbackInfo ci) {
        if (!VsoConfig.abilityTreeMixinEnabled) {
            // Ignore, mixin is disabled
            return;
        }

        var optionalPlayer = context.getSource().as(ServerPlayer.class);

        if (optionalPlayer.isEmpty()) {
            // No player -> can't sync
            ci.cancel();
            return;
        }

        var player = optionalPlayer.get();
        var playerUUID = player.getUUID();

        var lastSync = vaultserveroptimized$lastSyncTimes.stream()
            .filter(sync -> sync.getUuid().equals(playerUUID))
            .findFirst()
            .orElse(null);

        if (lastSync != null) {
            if (System.currentTimeMillis() - lastSync.getTimeMillis() < 2000) {
                // Less than 2 second since last sync -> cancel
                //VsoMod.LOGGER.info("Skipping ability tree sync for player {}", player.getName().getString());
                ci.cancel();
                return;
            }
        } else {
            lastSync = new UuidAndSyncTime(playerUUID);
            vaultserveroptimized$lastSyncTimes.add(lastSync);
        }

        //VsoMod.LOGGER.info("Syncing ability tree for player {}", player.getName().getString());
        lastSync.setTimeMillis(System.currentTimeMillis());

        // Continue with the execution
    }
}
