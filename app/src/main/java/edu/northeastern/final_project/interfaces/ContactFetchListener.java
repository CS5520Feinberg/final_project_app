package edu.northeastern.final_project.interfaces;

import edu.northeastern.final_project.entity.Contact;

public interface ContactFetchListener {
    void onContactFetched(Contact contact);
    void onError(String errorMessage);
}

