package dev.mayuna.vso.model;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class QuestIdTime extends UuidAndSyncTime {

    private @Getter @Setter String questId;

    public QuestIdTime(UUID uuid) {
        super(uuid);
    }
}
