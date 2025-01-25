package fr.oneblock.challenge;

import java.util.List;

public class Challenge {
    private final String id;
    private final String name;
    private final String description;
    private final ChallengeType type;
    private final String target;
    private final int amount;
    private final int phaseRequired;
    private final List<String> rewards;

    public Challenge(String id, String name, String description, ChallengeType type,
                     String target, int amount, int phaseRequired, List<String> rewards) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.target = target;
        this.amount = amount;
        this.phaseRequired = phaseRequired;
        this.rewards = rewards;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ChallengeType getType() { return type; }
    public String getTarget() { return target; }
    public int getAmount() { return amount; }
    public int getPhaseRequired() { return phaseRequired; }
    public List<String> getRewards() { return rewards; }
}