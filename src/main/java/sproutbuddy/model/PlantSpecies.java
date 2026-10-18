package sproutbuddy.model;

public class PlantSpecies {

    // Attributes
    private int speciesId;
    private String commonName;
    private String scientificName;
    private String lightRequirement;
    private String waterRequirement;
    private String careInstructions;
    private boolean custom;
    private Integer createdByUserId;


    // Constructor
    public PlantSpecies(int speciesId,
                        String commonName,
                        String scientificName,
                        String lightRequirement,
                        String waterRequirement,
                        String careInstructions,
                        boolean custom,
                        Integer createdByUserId) {

        this.speciesId = speciesId;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.lightRequirement = lightRequirement;
        this.waterRequirement = waterRequirement;
        this.careInstructions = careInstructions;
        this.custom = custom;
        this.createdByUserId = createdByUserId;
    }


    // Getters and Setters

    public int getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(int speciesId) {
        this.speciesId = speciesId;
    }


    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }


    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }


    public String getLightRequirement() {
        return lightRequirement;
    }

    public void setLightRequirement(String lightRequirement) {
        this.lightRequirement = lightRequirement;
    }


    public String getWaterRequirement() {
        return waterRequirement;
    }

    public void setWaterRequirement(String waterRequirement) {
        this.waterRequirement = waterRequirement;
    }


    public String getCareInstructions() {
        return careInstructions;
    }

    public void setCareInstructions(String careInstructions) {
        this.careInstructions = careInstructions;
    }


    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }


    public Integer getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Integer createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}