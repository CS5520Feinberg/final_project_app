package edu.northeastern.final_project.entity;

import java.util.List;

public class Contact {
    public String getName() {
        return name;
    }


    public Contact(String name, String phone_number) {
        this.name = name;
        this.phone_number = phone_number;
    }
    public Contact(String name, String phone_number,String email) {
        this.name = name;
        this.phone_number = phone_number;
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    private String name;

    private String phone_number;
    private List<String> following;
    private List<String> follower;
    private String image_uri;
    private String email;

}
