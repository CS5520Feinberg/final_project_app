package edu.northeastern.final_project.interfaces;

import edu.northeastern.final_project.entity.Contact;

public interface ContactFetchedCallBack {
    void contactFetched(Contact contact);
    void errorFetched(String errorMessage);
    void noDataFound();
}
