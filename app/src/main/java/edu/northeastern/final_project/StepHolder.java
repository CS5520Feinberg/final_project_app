package edu.northeastern.final_project;

public class StepHolder {
    public Integer steps;
    public String timestamp;
    public String isCloudSynced;

    public StepHolder() {
        // Default constructor
    }

    public StepHolder(Integer steps, String timestamp, String isCloudSynced) {
        this.steps = steps;
        this.timestamp = timestamp;
        this.isCloudSynced = isCloudSynced;
    }

    public void setCloudSynced() {
        this.isCloudSynced = "1";
    }

    public void setCloudNotSynced() {
        this.isCloudSynced = "0";
    }
}
