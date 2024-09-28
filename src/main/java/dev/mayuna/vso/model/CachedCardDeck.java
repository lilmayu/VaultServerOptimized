package dev.mayuna.vso.model;

import java.util.UUID;

import iskallia.vault.core.card.CardDeck;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Holds cached CardDeck and the time it was cached.
 */
public final class CachedCardDeck extends UuidAndSyncTime {

    private @Getter @Setter CardDeck cardDeck;

    /**
     * Constructor.
     *
     * @param cardDeckUUID   The ItemStack of the CardDeck.
     */
    public CachedCardDeck(UUID cardDeckUUID) {
        super(cardDeckUUID);
    }
}
