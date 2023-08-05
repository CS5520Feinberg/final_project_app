package edu.northeastern.final_project.entity;

public class Contact {
    public String getName() {
        return name;
    }

    public Contact(String name, String phone_number) {
        this.name = name;
        this.phone_number = phone_number;
    }

    private String name;
    private String phone_number;
}
