package com.ransomlab.models;

public class Attack {
    public String id;
    public String name;
    public int year;
    public String color;       // hex string e.g. "#FF2D55"
    public String technique;
    public String vector;
    public String damage;
    public String victims;
    public String ransom;
    public String encryption;
    public String attribution;
    public String description;
    public String[] phases;    // animation phases
    public boolean iswiper;    // NotPetya-style wiper

    public Attack(String id, String name, int year, String color,
                  String technique, String vector, String damage,
                  String victims, String ransom, String encryption,
                  String attribution, String description,
                  String[] phases, boolean iswiper) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.color = color;
        this.technique = technique;
        this.vector = vector;
        this.damage = damage;
        this.victims = victims;
        this.ransom = ransom;
        this.encryption = encryption;
        this.attribution = attribution;
        this.description = description;
        this.phases = phases;
        this.iswiper = iswiper;
    }
}
