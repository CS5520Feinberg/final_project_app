package edu.northeastern.final_project.interfaces;

import edu.northeastern.final_project.entity.Contact;

public interface UserDataFetchedCallback {
    void onSuccess(Contact contact);
    void onError(String message);
}
