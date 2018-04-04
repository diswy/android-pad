package com.cqebd.student.netease.ui.tab;

import android.os.Bundle;

import com.cqebd.student.R;
import com.cqebd.student.netease.ui.ChatRoomRTSFragment;


/**
 * Created by hzxuwen on 2016/10/27.
 */

public class RTSTabFragment extends ChatRoomTabFragment {
    private ChatRoomRTSFragment fragment;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCurrent();
    }

    @Override
    protected void onInit() {
        findViews();
    }

    @Override
    public void onCurrent() {
        super.onCurrent();
        if (fragment != null) {
            fragment.onCurrent();
        }
    }

    private void findViews() {
        if (fragment == null) {
            fragment = (ChatRoomRTSFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.chat_room_rts_fragment);
        }
    }
}
