package edu.northeastern.final_project.interfaces;

import java.util.List;

import edu.northeastern.final_project.entity.Contact;

public interface ContactFetchedCallBack {
    void contactFetched(Contact contact);

    void errorFetched(String errorMessage);

    void noDataFound();

    void onMultipleContactFetched(List<Contact> contacts);
}
