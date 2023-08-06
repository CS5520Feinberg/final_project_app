package edu.northeastern.final_project;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerProfileAdapter extends FragmentStateAdapter {

    public ViewPagerProfileAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WeeklyChartFragment();
            case 1:
                return new DailyPieChartFragment();
            default:
                return new WeeklyChartFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
