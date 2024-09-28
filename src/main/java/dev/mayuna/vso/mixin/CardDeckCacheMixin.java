package dev.mayuna.vso.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.mayuna.vso.VsoConfig;
import dev.mayuna.vso.VsoMod;
import dev.mayuna.vso.model.CachedCardDeck;
import iskallia.vault.core.card.CardDeck;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.item.CardDeckItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

@Mixin(CardDeckItem.class)
public class CardDeckCacheMixin {

    // TODO: Remove cached card decks after a player disconnect (get their decks and remove them from the list)
    @Unique
    private static final List<CachedCardDeck> vaultserveroptimized$cachedCardDecks = new ArrayList<>();

    @Inject(method = "getCardDeck", at = @At("HEAD"), cancellable = true, remap = false)
    private static void getCardDeck(ItemStack stack, CallbackInfoReturnable<Optional<CardDeck>> cir) {
        if (!VsoConfig.cardDeckCacheMixinEnabled) {
            return; // Don't do anything
        }

        final var stackNbt = stack.getTag();

        if (stackNbt == null || !stackNbt.contains("data")) {
            //VsoMod.LOGGER.warn("Card deck without data! {}", stack);
            // Don't do anything, let the the_vault handle it
            return;
        }

        final var cardDeckUUIDTag = stackNbt.getCompound("data").get("uuid");

        if (cardDeckUUIDTag == null) {
            //VsoMod.LOGGER.warn("Card deck without UUID tag! {}", stack);
            // Don't do anything, let the the_vault handle it
            return;
        }

        final var cardDeckUUID = Adapters.UUID.readNbt(cardDeckUUIDTag).orElse(null);

        if (cardDeckUUID == null) {
            //VsoMod.LOGGER.warn("Card deck without UUID! {}", stack);
            // Don't do anything, let the the_vault handle it
            return;
        }

        CachedCardDeck cachedCardDeck = vaultserveroptimized$cachedCardDecks.stream()
            .filter(cached -> cached.getUuid().equals(cardDeckUUID))
            .findFirst()
            .orElse(null);

        if (cachedCardDeck != null) {
            // If the cached card deck is less than 1 second old, return it
            if (System.currentTimeMillis() - cachedCardDeck.getTimeMillis() <= 1000) {
                // Return cached card deck
                //VsoMod.LOGGER.info("Returning cached card deck for UUID {}", cardDeckUUID);
                cir.setReturnValue(Optional.of(cachedCardDeck.getCardDeck()));
                return;
            }
        }

        // Read the card deck from the stack
        CardDeck deck = new CardDeck();
        deck.readNbt(stack.getTag().getCompound("data"));

        // Create or update cached card deck
        //VsoMod.LOGGER.info("Caching card deck for UUID {}", cardDeckUUID);
        if (cachedCardDeck == null) {
            cachedCardDeck = new CachedCardDeck(cardDeckUUID);
            vaultserveroptimized$cachedCardDecks.add(cachedCardDeck);
        }

        cachedCardDeck.setCardDeck(deck);
        cachedCardDeck.setTimeMillis(System.currentTimeMillis());

        // Return the card deck
        cir.setReturnValue(Optional.of(deck));
    }
}
