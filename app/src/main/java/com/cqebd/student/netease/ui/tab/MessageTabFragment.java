package com.cqebd.student.netease.ui.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cqebd.student.R;
import com.cqebd.student.netease.ui.ChatRoomMessageFragment;


/**
 * Created by hzxuwen on 2016/2/29.
 */
public class MessageTabFragment extends ChatRoomTabFragment {
    private ChatRoomMessageFragment fragment;

    public MessageTabFragment() {
        this.setContainerId(ChatRoomTab.CHAT_ROOM_MESSAGE.fragmentId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void onInit() {
        findViews();
    }

    @Override
    public void onCurrent() {
        super.onCurrent();
    }

    @Override
    public void onLeave() {
        super.onLeave();
        if (fragment != null) {
//            fragment.onLeave();
        }
    }

    private void findViews() {
        fragment = (ChatRoomMessageFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.chat_room_message_fragment);
    }
}
