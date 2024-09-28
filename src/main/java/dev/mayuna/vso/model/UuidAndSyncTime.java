package dev.mayuna.vso.model;

import java.util.UUID;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UuidAndSyncTime {

    private final UUID uuid;
    private long timeMillis;

}
