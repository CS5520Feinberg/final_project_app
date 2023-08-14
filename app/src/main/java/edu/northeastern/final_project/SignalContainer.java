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

    public int findPeaks(int window_size, double thresh) {
        int peakCount = 0;

        if (full) {
            // Method: check if peak is (thresh) above 10-element window
            LinkedList<Double> window = new LinkedList<>();
            int half_window = window_size / 2;

            // filling initial window
            for (int i = 0; i < window_size; i++) {
                window.add(this.get(i));
            }

            for (int i = 0; i < this.size(); i++) {
                double thisVal = this.get(i);

                if ((i > half_window) && i < this.size() - half_window) {
                    window.add(this.get(i + half_window));
                    window.removeFirst();
                }

                // calculating window average
                double window_sum = 0;
                for (int j = 0; j < window.size(); j++) {
                    window_sum += window.get(j);
                }

                double avg_val = window_sum / window_size;

                if (thisVal > (avg_val + thresh)) {
                    peakCount++;
                }
            }
        } else {
            Log.e("SignalContainer", "findPeaks() called on not full container!");
        }
        return peakCount;
    }
}
