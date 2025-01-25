package fr.oneblock.player;

import java.util.UUID;

public class OneBlockPlayer {
    private final UUID uuid;
    private int phase;
    private int blocksBroken;
    private long playTime;
    private boolean firstJoin;

    // Statistiques de progression
    private int totalBlocksBroken;
    private int mobsKilled;
    private int challengesCompleted;
    private long lastBlockBreakTime;

    public OneBlockPlayer(UUID uuid, int phase, int blocksBroken, long playTime, boolean firstJoin) {
        this.uuid = uuid;
        this.phase = phase;
        this.blocksBroken = blocksBroken;
        this.playTime = playTime;
        this.firstJoin = firstJoin;
        this.lastBlockBreakTime = System.currentTimeMillis();
    }

    // Méthodes de progression
    public void incrementBlocksBroken() {
        this.blocksBroken++;
        this.totalBlocksBroken++;
        this.lastBlockBreakTime = System.currentTimeMillis();
    }

    public void incrementPhase() {
        this.phase++;
        this.blocksBroken = 0; // Réinitialiser le compteur pour la nouvelle phase
    }

    public void incrementMobsKilled() {
        this.mobsKilled++;
    }

    public void incrementChallengesCompleted() {
        this.challengesCompleted++;
    }

    public void updatePlayTime(long additionalTime) {
        this.playTime += additionalTime;
    }

    // Getters
    public UUID getUuid() {
        return uuid;
    }

    public int getPhase() {
        return phase;
    }

    public int getBlocksBroken() {
        return blocksBroken;
    }

    public long getPlayTime() {
        return playTime;
    }

    public boolean isFirstJoin() {
        return firstJoin;
    }

    public int getTotalBlocksBroken() {
        return totalBlocksBroken;
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public int getChallengesCompleted() {
        return challengesCompleted;
    }

    public long getLastBlockBreakTime() {
        return lastBlockBreakTime;
    }

    // Setters avec validation
    public void setPhase(int phase) {
        if (phase > 0) {
            this.phase = phase;
        }
    }

    public void setBlocksBroken(int blocksBroken) {
        if (blocksBroken >= 0) {
            this.blocksBroken = blocksBroken;
        }
    }

    public void setPlayTime(long playTime) {
        if (playTime >= 0) {
            this.playTime = playTime;
        }
    }

    public void setFirstJoin(boolean firstJoin) {
        this.firstJoin = firstJoin;
    }

    // Méthodes utilitaires
    public boolean canProgressToNextPhase(int requiredBlocks) {
        return blocksBroken >= requiredBlocks;
    }

    public double getPhaseProgress(int requiredBlocks) {
        return Math.min(1.0, (double) blocksBroken / requiredBlocks);
    }

    public boolean isActive() {
        // Considère un joueur comme inactif après 5 minutes sans casser de bloc
        return System.currentTimeMillis() - lastBlockBreakTime < 300000;
    }

    public void reset() {
        this.phase = 1;
        this.blocksBroken = 0;
        this.mobsKilled = 0;
        this.challengesCompleted = 0;
        // Ne pas réinitialiser le playTime et totalBlocksBroken pour garder l'historique
    }
}