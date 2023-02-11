package com.plusls.carpet.util.rule.playerOperationLimiter;

public interface SafeServerPlayerEntity {
    int pca$getInstaBreakCountPerTick();

    int pca$getPlaceBlockCountPerTick();

    void pca$addInstaBreakCountPerTick();

    void pca$addPlaceBlockCountPerTick();

    boolean pca$allowOperation();
}
