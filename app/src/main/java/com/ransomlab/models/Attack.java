package com.ransomlab.models;

public class Attack {
    private String id;
    private String name;
    private int year;
    private String colorHex;
    private String type;
    private String vector;
    private String damage;
    private String victims;
    private String ransomDemand;
    private String encryptionAlgorithm;
    private String threatActor;
    private String rootCause; // The newly added field!
    private String description;
    private String[] simulationSteps;
    private boolean isWiper;

    public String getId() {
        return id;
    }

    // Constructor with all 15 parameters
    public Attack(String id, String name, int year, String colorHex, String type,
                  String vector, String damage, String victims, String ransomDemand,
                  String encryptionAlgorithm, String threatActor, String rootCause,
                  String description, String[] simulationSteps, boolean isWiper) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.colorHex = colorHex;
        this.type = type;
        this.vector = vector;
        this.damage = damage;
        this.victims = victims;
        this.ransomDemand = ransomDemand;
        this.encryptionAlgorithm = encryptionAlgorithm;
        this.threatActor = threatActor;
        this.rootCause = rootCause;
        this.description = description;
        this.simulationSteps = simulationSteps;
        this.isWiper = isWiper;
    }

    // Getters
    public String getName() { return name; }
    public int getYear() { return year; }
    public String getColorHex() { return colorHex; }
    public String getType() { return type; }
    public String getVector() { return vector; }
    public String getDamage() { return damage; }
    public String getVictims() { return victims; }
    public String getRansomDemand() { return ransomDemand; }
    public String getEncryptionAlgorithm() { return encryptionAlgorithm; }
    public String getThreatActor() { return threatActor; }
    public String getRootCause() { return rootCause; }
    public String getDescription() { return description; }
    public String[] getSimulationSteps() { return simulationSteps; }
    public boolean isWiper() { return isWiper; }
}