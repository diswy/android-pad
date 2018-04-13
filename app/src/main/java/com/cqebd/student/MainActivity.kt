package com.cqebd.student

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.ui.*
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    private var currentFragment: Fragment? = null

    override fun setContentView() {
        setContentView(R.layout.activity_main)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val titles = resources.getStringArray(R.array.title)
        navigation.addItem(BottomNavigationItem(R.drawable.ic_home, titles[0]))
                .addItem(BottomNavigationItem(R.drawable.ic_video, titles[1]))
                .addItem(BottomNavigationItem(R.drawable.ic_work, titles[2]))
                .addItem(BottomNavigationItem(R.drawable.ic_mine, titles[3]))
                .initialise()
        navigation.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabReselected(position: Int) {

            }

            override fun onTabUnselected(position: Int) {
            }

            override fun onTabSelected(position: Int) {
                if (position == 3) {
                    text_title.visibility = View.GONE
                } else {
                    text_title.text = titles[position]
                    text_title.visibility = View.VISIBLE
                }
                switchFragment(position)
            }
        })
        switchFragment(0)
    }

    private fun switchFragment(position: Int) {
        var targetFragment: Fragment? = supportFragmentManager.findFragmentByTag("tag$position")
        if (targetFragment==null){
            when (position) {
                0 -> {
                    targetFragment = HomeFragment()
                }
                1 -> {
                    targetFragment = VideoFragment()
                }
                2 -> {
                    targetFragment = WorkFragment()
                }
                3 -> {
                    targetFragment = MineFragment()
                }
            }
        }
        val transaction = supportFragmentManager.beginTransaction().apply {
            if (currentFragment != null) {
                hide(currentFragment)
            }
        }
        if (targetFragment?.isAdded==true) {
            transaction.show(targetFragment).commitAllowingStateLoss()
        } else {
            transaction.add(R.id.frame_content, targetFragment,"tag$position").commitAllowingStateLoss()
        }
        currentFragment = targetFragment
    }
}
