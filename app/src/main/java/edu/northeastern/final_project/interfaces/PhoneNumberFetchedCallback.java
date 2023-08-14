package edu.northeastern.final_project.interfaces;

public interface PhoneNumberFetchedCallback {
    void onPhoneNumberFetched(String phone_number);

    void onError(Exception
                         ex);
}
