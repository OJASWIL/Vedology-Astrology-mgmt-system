package com.vedology.model;

public class Astrologer {
    private int astrologerId;
    private String availableDays;
    private String address;
    private String contactNumber;
    private int experienceYear;
    private String specialization;

    // Constructor
    public Astrologer(int astrologerId, String availableDays, String address, 
                     String contactNumber, int experienceYear, String specialization) {
        this.astrologerId = astrologerId;
        this.availableDays = availableDays;
        this.address = address;
        this.contactNumber = contactNumber;
        this.experienceYear = experienceYear;
        this.specialization = specialization;
    }

    // Getters
    public int getAstrologerId() { return astrologerId; }
    public String getAvailableDays() { return availableDays; }
    public String getAddress() { return address; }
    public String getContactNumber() { return contactNumber; }
    public int getExperienceYear() { return experienceYear; }
    public String getSpecialization() { return specialization; }

}