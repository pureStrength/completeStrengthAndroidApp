package com.completeconceptstrength.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;
import com.roomorama.caldroid.CaldroidFragment;

import java.util.Calendar;

import completeconceptstrength.model.user.impl.User;

public class AthleteWorkouts extends AppCompatActivity {

    GlobalContext globalContext;
    User user;

    ViewPager pager;
    static CalendarView calendar;
    AthleteWorkoutsViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Calendar","List"};
    int numTabs = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_workouts);
        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new AthleteWorkoutsViewPagerAdapter(getSupportFragmentManager(),Titles, numTabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    class AthleteWorkoutsViewPagerAdapter extends FragmentStatePagerAdapter
    {
        CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
        int numTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

        // Build a Constructor and assign the passed Values to appropriate values in the class
        public AthleteWorkoutsViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
            super(fm);
            this.Titles = mTitles;
            this.numTabs = mNumbOfTabsumb;
        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {
            if(position == 0) // if the position is 0 we are returning the First tab
            {
                AthleteWorkoutsTab1 calendarTab = new AthleteWorkoutsTab1();
                return calendarTab;
            }
            else  // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
            {
                AthleteWorkoutsTab2 list = new AthleteWorkoutsTab2();
                return list;
            }
        }

        // This method return the titles for the Tabs in the Tab Strip
        @Override
        public CharSequence getPageTitle(int position) {
            return Titles[position];
        }

        // This method return the Number of tabs for the tabs Strip
        @Override
        public int getCount() {
            return numTabs;
        }
    }

    public class AthleteWorkoutsTab1 extends Fragment {


        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v =inflater.inflate(R.layout.athlete_workouts_tab_1, container, false);
            CaldroidFragment caldroidFragment = new CaldroidFragment();
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            caldroidFragment.setArguments(args);

            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.replace(R.id.calendar_frag, caldroidFragment);
            t.commit();

            return v;
        }
    }

    public static class AthleteWorkoutsTab2 extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.athlete_workouts_tab_2,container,false);
            return v;
        }
    }

}
