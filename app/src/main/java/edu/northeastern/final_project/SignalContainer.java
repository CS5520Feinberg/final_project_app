package edu.northeastern.final_project;

import android.util.Log;

import java.util.LinkedList;

public class SignalContainer extends LinkedList<Double> {
    private int maxSize;
    private Boolean full = false;

    public SignalContainer(int size) {
        maxSize = size;
    }

    public boolean add(Double value) {
        boolean added = super.add(value);

        // Raise error if added is false
        if (added == false) {
            Log.e("SignalContainer", "Failure to add!");
        }

        if (size() == maxSize) {
            full = true;
        } else if (size() > maxSize) {
            Log.e("SignalContainer", "Error with SignalContainer: larger than maxSize!");
        }

        return full;
    }

    public void clear() {
        super.clear();
        full = false;
    }
}
