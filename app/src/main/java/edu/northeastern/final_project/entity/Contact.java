package edu.northeastern.final_project.entity;


import java.util.List;

public class Contact {
    public Contact() {

    }

    public String getName() {
        return name;
    }

    public List<String> getFollowing() {
        return following;
    }

    public List<String> getFollower() {
        return follower;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public String getEmail() {
        return email;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public void setFollower(List<String> follower) {
        this.follower = follower;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
