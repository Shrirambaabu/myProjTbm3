package com.freshlancers.sriambalcrusher.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ashith VL on 10/13/2017.
 */

public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "SectionsStatePagerAdapter";

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments = new HashMap<>();
    private final HashMap<String, Integer> mFragmentsNumbers = new HashMap<>();
    private final HashMap<Integer, String> mFragmentsNames = new HashMap<>();

    public SectionsStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment mFragment, String fragmentName) {
        mFragmentList.add(mFragment);
        mFragments.put(mFragment, mFragmentList.size() - 1);
        mFragmentsNumbers.put(fragmentName, mFragmentList.size() - 1);
        mFragmentsNames.put(mFragmentList.size() - 1, fragmentName);
    }

    /**
     * returns the fragment number
     *
     * @param fragmentName
     * @return
     */
    public Integer getFragmentNumber(String fragmentName) {
        if (mFragmentsNumbers.containsKey(fragmentName)) {
            return mFragmentsNumbers.get(fragmentName);
        } else {
            return null;
        }
    }

    /**
     * returns the fragment name
     *
     * @param fragmentNumber
     * @return
     */
    public String getFragmentName(Integer fragmentNumber) {
        if (mFragmentsNames.containsKey(fragmentNumber)) {
            return mFragmentsNames.get(fragmentNumber);
        } else {
            return null;
        }
    }

}
