package com.cqebd.student.test;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cqebd.student.R;
import com.cqebd.student.custom_ui.TabLayout;

public class TestTablayout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_tablayout);


        TabLayout tab = findViewById(R.id.tabLayout);
        tab.addTab(tab.newTab().setText("No.1"));
        tab.addTab(tab.newTab().setText("No.2"));
        tab.addTab(tab.newTab().setText("No.3"));
        tab.addTab(tab.newTab().setText("No.4"));


        Fragment a = new BlankFragment();
        Fragment b = new BlankFragment();
        Fragment c = new BlankFragment();
        Fragment d = new BlankFragment();

        ViewPager vp =  findViewById(R.id.vp);
        vp.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return a;
                    case 1:
                        return b;
                    case 2:
                        return c;
                    case 3:
                        return d;
                }
                return a;
            }

            @Override
            public int getCount() {
                return 4;
            }
        });

        tab.setupWithViewPager(vp);
    }
}
