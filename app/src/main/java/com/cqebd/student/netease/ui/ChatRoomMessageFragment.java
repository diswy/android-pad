package com.cqebd.student.netease.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cqebd.student.R;
import com.cqebd.student.netease.base.TFragment;

public class ChatRoomMessageFragment extends TFragment {


    public ChatRoomMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_room_message, container, false);
    }

}
