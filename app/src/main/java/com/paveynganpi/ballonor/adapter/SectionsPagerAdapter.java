package com.paveynganpi.ballonor.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.paveynganpi.ballonor.ui.FeedFragment;
import com.paveynganpi.ballonor.ui.NoTeamsFragment;
import com.paveynganpi.ballonor.ui.TeamFragment;

import java.util.Locale;

/**
 * Created by paveynganpi on 6/20/15.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    protected Context mContext;//creating context so as to call it in the getString() getPageTitle(), since we are not in an activity
    protected String mTeam;

    public SectionsPagerAdapter(Context context, FragmentManager fm, String team) {

        super(fm);
        mContext = context;
        mTeam = team;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        String team = toCamelCase(mTeam);
        Bundle args = new Bundle();
        args.putString("TeamName", team);
        TeamFragment teamFragment = new TeamFragment();
        teamFragment.setArguments(args);

        FeedFragment feedFragment = new FeedFragment();
        feedFragment.setArguments(args);

        switch (position){
            case 0:
                if(team.equals("chelseafc"))
                    return new NoTeamsFragment();
                else
                    return teamFragment;
            case 1:
                return feedFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mTeam.toUpperCase(l);
            case 1:
                return "Feed".toUpperCase(l);
        }
        return mTeam.toUpperCase(l);
    }

    public String toCamelCase(String str){
        return Character.toLowerCase(
                str.charAt(0)) + str.substring(1).replaceAll("\\s","");
    }
}
