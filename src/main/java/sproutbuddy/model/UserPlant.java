
package sproutbuddy.model;

import java.time.LocalDate;

public class UserPlant {

    // Attributes
    private int plantId;
    private int userId;
    private int speciesId;
    private String nickname;
    private LocalDate dateAdded;
    private String location;
    private String photo;
    private Integer wateringIntervalDays;
    private LocalDate lastWatered;
    private String notes;

    // Associated plant species information
    private PlantSpecies species;

    // Constructor
    public UserPlant(int plantId,
                     int userId,
                     int speciesId,
                     String nickname,
                     LocalDate dateAdded,
                     String location,
                     String photo,
                     Integer wateringIntervalDays,
                     LocalDate lastWatered,
                     String notes) {

        this.plantId = plantId;
        this.userId = userId;
        this.speciesId = speciesId;
        this.nickname = nickname;
        this.dateAdded = dateAdded;
        this.location = location;
        this.photo = photo;
        this.wateringIntervalDays = wateringIntervalDays;
        this.lastWatered = lastWatered;
        this.notes = notes;
    }

    // Getters and Setters
    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(int speciesId) {
        this.speciesId = speciesId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getWateringIntervalDays() {
        return wateringIntervalDays;
    }

    public void setWateringIntervalDays(
            Integer wateringIntervalDays) {
        this.wateringIntervalDays = wateringIntervalDays;
    }

    public LocalDate getLastWatered() {
        return lastWatered;
    }

    public void setLastWatered(LocalDate lastWatered) {
        this.lastWatered = lastWatered;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public PlantSpecies getSpecies() {
        return species;
    }

    public void setSpecies(PlantSpecies species) {
        this.species = species;
    }
}
