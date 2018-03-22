package com.cqebd.student.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * document
 * Created by Gordn on 2017/3/14.
 */

public class CardFragmentAdapter extends PagerAdapter {
    FragmentManager fm;
    private FragmentTransaction mCurTransaction = null;

    List<Fragment> cards = new ArrayList<>();
    public CardFragmentAdapter(FragmentManager fm, List<Fragment> cards) {
        this.fm = fm;
        this.cards.addAll(cards);
    }

    public Fragment getItem(int position) {
        return cards.get(position);
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Fragment instantiateItem(ViewGroup container, int position) {
        if (mCurTransaction == null) {
            mCurTransaction = fm.beginTransaction();
        }
        Fragment fragment = getItem(position);
        if (fragment != null) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
            if (!fragment.isAdded()) {
                mCurTransaction.add(container.getId(), fragment);
            }
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (mCurTransaction == null) {
            mCurTransaction = fm.beginTransaction();
        }
        mCurTransaction.remove(fragment);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }
    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            fm.executePendingTransactions();
        }
    }
}
