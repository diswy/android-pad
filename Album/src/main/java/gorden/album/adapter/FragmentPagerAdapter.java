package gorden.album.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import gorden.album.fragment.ImageFragment;

/**
 * document
 * Created by Gordn on 2017/4/7.
 */

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> imgList;

    public FragmentPagerAdapter(FragmentManager fm, ArrayList<String> imgList) {
        super(fm);
        this.imgList = imgList;
    }

    @Override
    public Fragment getItem(int position) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle argument = new Bundle();
        argument.putString(ImageFragment.PARAM_IMAGE_URI, imgList.get(position));
        imageFragment.setArguments(argument);
        return imageFragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
