package edu.northeastern.final_project.entity;

public class ScoreCard {
    Contact contact;
    int stepCount;

    public ScoreCard(Contact contact, int stepCount) {
        this.contact = contact;
        this.stepCount = stepCount;
    }

    public Contact getContact() {
        return contact;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
