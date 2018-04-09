package com.cqebd.student.netease.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cqebd.student.R;
import com.cqebd.student.netease.base.TFragment;
import com.cqebd.student.netease.helper.ChatRoomMemberCache;
import com.cqebd.student.netease.helper.SimpleCallback;
import com.cqebd.student.netease.helper.VideoListener;
import com.cqebd.student.widget.PageLoadView;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import gorden.refresh.KRefreshLayout;

public class OnlinePeopleFragment extends TFragment implements VideoListener {
    private static final int LIMIT = 100;

    private KRefreshLayout kRefreshLayout;
    private RecyclerView rv;
    private PageLoadView pageLoadView;

    private BaseQuickAdapter<OnlinePeopleItem, BaseViewHolder> adapter;
    private String roomId;
    private List<OnlinePeopleItem> items = new ArrayList<>();
    private Map<String, OnlinePeopleItem> memberCache = new ConcurrentHashMap<>();
    private static Map<MemberType, Integer> compMap = new HashMap<>();
    private long updateTime = 0; // 非游客的updateTime
    private long enterTime = 0; // 游客的enterTime

    private boolean isNormalEmpty = false; // 固定成员是否拉取完
    private VideoListener videoListener;

    static {
        compMap.put(MemberType.CREATOR, 0);
        compMap.put(MemberType.ADMIN, 1);
        compMap.put(MemberType.NORMAL, 2);
        compMap.put(MemberType.LIMITED, 3);
        compMap.put(MemberType.GUEST, 4);
    }

    @Override
    public void onVideoOn(String account) {
        videoListener.onVideoOn(account);
    }

    @Override
    public void onVideoOff(String account) {
        videoListener.onVideoOff(account);
    }

    @Override
    public void onTabChange(boolean notify) {
        videoListener.onTabChange(notify);
    }

    @Override
    public void onKickOutSuccess(String account) {

    }

    @Override
    public void onUserLeave(String account) {

    }

    @Override
    public void onReportSpeaker(Map<String, Integer> map) {

    }

    @Override
    public void onAcceptConfirm() {

    }

    public static class OnlinePeopleItem {
        private String creator;
        private ChatRoomMember chatRoomMember;

        public OnlinePeopleItem(String creator, ChatRoomMember chatRoomMember) {
            this.creator = creator;
            this.chatRoomMember = chatRoomMember;
        }

        public String getCreator() {
            return creator;
        }

        public ChatRoomMember getChatRoomMember() {
            return chatRoomMember;
        }

        public void setChatRoomMember(ChatRoomMember chatRoomMember) {
            this.chatRoomMember = chatRoomMember;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online_people, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initAdapter();
        findViews();
        registerObservers(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        videoListener = (VideoListener) context;
    }

    public void onCurrent() {
        clearCache();
        roomId = ((ChatRoomActivity) getActivity()).getRoomInfo().getRoomId();
        fetchData();
        videoListener.onTabChange(false);
    }

    private void fetchData() {
        if (!isNormalEmpty) {
            // 拉取固定在线成员
            getMembers(MemberQueryType.ONLINE_NORMAL, updateTime, 0);
        } else {
            // 拉取非固定成员
            getMembers(MemberQueryType.GUEST, enterTime, 0);
        }
    }

    /**
     * 获取成员列表
     */
    private void getMembers(final MemberQueryType memberQueryType, final long time, int limit) {
        ChatRoomMemberCache.getInstance().fetchRoomMembers(roomId, memberQueryType, time, (LIMIT - limit), new SimpleCallback<List<ChatRoomMember>>() {
            @Override
            public void onResult(boolean success, List<ChatRoomMember> result) {
                if (success) {
                    if (getActivity() == null) {
                        return;
                    }

//                    if (onlineText.getVisibility() == View.VISIBLE || result == null || result.isEmpty()) {
//                        onlineText.setVisibility(View.GONE);
//                    }

                    addMembers(result);

                    if (memberQueryType == MemberQueryType.ONLINE_NORMAL && result.size() < LIMIT) {
                        isNormalEmpty = true; // 固定成员已经拉完
                        getMembers(MemberQueryType.GUEST, enterTime, result.size());
                    }
                }

//                stopRefreshing();
            }
        });
    }

    private void addMembers(List<ChatRoomMember> members) {

        for (ChatRoomMember member : members) {
            if (!isNormalEmpty) {
                updateTime = member.getUpdateTime();
            } else {
                enterTime = member.getEnterTime();
            }

            if (memberCache.containsKey(member.getAccount())) {
                items.remove(memberCache.get(member.getAccount()));
            }
            OnlinePeopleItem item = new OnlinePeopleItem(((ChatRoomActivity) getActivity()).getRoomInfo().getCreator(),
                    member);

            memberCache.put(member.getAccount(), item);
            items.add(item);
        }
        Collections.sort(items, comp);

        adapter.setNewData(items);
    }

    private static Comparator<OnlinePeopleItem> comp = new Comparator<OnlinePeopleItem>() {
        @Override
        public int compare(OnlinePeopleItem lhs, OnlinePeopleItem rhs) {
            if (lhs == null) {
                return 1;
            }

            if (rhs == null) {
                return -1;
            }

            return compMap.get(lhs.getChatRoomMember().getMemberType()) - compMap.get(rhs.getChatRoomMember().getMemberType());
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        registerObservers(false);
    }

    private void clearCache() {
        updateTime = 0;
        enterTime = 0;
        items.clear();
        memberCache.clear();
        isNormalEmpty = false;
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<OnlinePeopleItem, BaseViewHolder>(R.layout.online_people_item, items) {

            @Override
            protected void convert(BaseViewHolder helper, OnlinePeopleItem item) {
                helper.setText(R.id.tv_name, item.getChatRoomMember().getNick());
            }
        };
    }

    private void findViews() {
        kRefreshLayout = findView(R.id.refreshLayout);
        rv = findView(R.id.recyclerView);
        pageLoadView = findView(R.id.pageLoadView);
        pageLoadView.hide();

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
    }


    /**
     * *************************** 成员操作监听 ****************************
     */
    private void registerObservers(boolean register) {
        ChatRoomMemberCache.getInstance().registerRoomMemberChangedObserver(roomMemberChangedObserver, register);
        ChatRoomMemberCache.getInstance().registerMeetingControlObserver(meetingControlObserver, register);
    }

    ChatRoomMemberCache.RoomMemberChangedObserver roomMemberChangedObserver = new ChatRoomMemberCache.RoomMemberChangedObserver() {
        @Override
        public void onRoomMemberIn(ChatRoomMember member) {
        }

        @Override
        public void onRoomMemberExit(ChatRoomMember member) {
            if (member == null) {
                return;
            }
//            for (OnlinePeopleItem item : items) {
//                if (item.getChatRoomMember().getAccount().equals(member.getAccount())) {
//                    items.remove(item);
//                    break;
//                }
//            }
            adapter.notifyDataSetChanged();
        }
    };

    ChatRoomMemberCache.MeetingControlObserver meetingControlObserver = new ChatRoomMemberCache.MeetingControlObserver() {

        @Override
        public void onAccept(String roomID) {
            if (checkRoom(roomID)) {
                return;
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onReject(String roomID) {
            if (checkRoom(roomID)) {
                return;
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onPermissionResponse(String roomId, List<String> accounts) {

        }

        @Override
        public void onSendMyPermission(String roomID, String toAccount) {

        }

        @Override
        public void onSaveMemberPermission(String roomId, List<String> accounts) {

        }

        @Override
        public void onHandsUp(String roomID, String account) {
            if (checkRoom(roomID)) {
                return;
            }
            clearCache();
//            fetchData();
        }

        @Override
        public void onHandsDown(String roomID, String account) {
            if (checkRoom(roomID)) {
                return;
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onStatusNotify(String roomID, List<String> accounts) {

        }
    };

    private boolean checkRoom(String roomID) {
        return TextUtils.isEmpty(roomId) || !roomId.equals(roomID);
    }
}
