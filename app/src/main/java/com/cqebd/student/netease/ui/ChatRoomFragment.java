package com.cqebd.student.netease.ui;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cqebd.student.R;
import com.cqebd.student.netease.NetEaseCache;
import com.cqebd.student.netease.base.TFragment;
import com.cqebd.student.netease.helper.ChatRoomMemberCache;
import com.cqebd.student.netease.helper.MsgHelper;
import com.cqebd.student.netease.helper.VideoListener;
import com.cqebd.student.netease.modle.FullScreenType;
import com.cqebd.student.netease.modle.MeetingConstant;
import com.cqebd.student.netease.modle.MeetingOptCommand;
import com.cqebd.student.netease.session.ModuleProxy;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserverLite;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatUserRole;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatAudioFrame;
import com.netease.nimlib.sdk.avchat.model.AVChatCameraCapturer;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatNetworkStats;
import com.netease.nimlib.sdk.avchat.model.AVChatParameters;
import com.netease.nimlib.sdk.avchat.model.AVChatSessionStats;
import com.netease.nimlib.sdk.avchat.model.AVChatSurfaceViewRenderer;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoCapturerFactory;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoFrame;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.rts.RTSCallback;
import com.netease.nimlib.sdk.rts.RTSManager2;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatRoomFragment extends TFragment implements AVChatStateObserverLite, View.OnClickListener {
    private final String TAG = "ChatRoomFragment";

    private Activity activity;
    private ChatRoomInfo roomInfo;
    private String roomId;
    private String roomName;
    private String shareUrl; // 分享地址

    private boolean disconnected = false; // 是否断网（断网重连用）
    private boolean isPermissionInit = false; // 是否收到其他成员权限
    private boolean isBackLayoutShow = true; // 返回布局是否显示
    private boolean isCreate = false; // 是否是主播

    private VideoListener videoListener;
    private List<String> userJoinedList = new ArrayList<>(); // 已经onUserJoined的用户
    AVChatSurfaceViewRenderer masterRender; // 主播画布
    private AVChatCameraCapturer videoCapturer; // 视频采集模块


    private TabLayout tabs;
    private ViewPager viewPager;

    /**
     * 直播区域（上）
     */
    private RelativeLayout videoLayout; // 直播/播放区域
    private ViewGroup backLayout;
    private ViewGroup fullScreenView; // 全屏播放显示区域
    private ViewGroup fullScreenLayout;
    private ImageView videoPermissionBtn; // 视频权限按钮
    private ImageView audioPermissionBtn; // 音频权限按钮
    private TextView interactionStartCloseBtn; // 互动开始/结束按钮
    private TextView statusText;
    private TextView roomIdText;
    private SurfaceView selfRender;
    private long uid;
    private ViewGroup masterVideoLayout; // 左上，主播显示区域
    private ViewGroup firstRightVideoLayout; // 右上，第一个观众显示区域
    private ViewGroup secondLeftVideoLayout; // 左下，第二个观众显示区域
    private ViewGroup thirdRightVideoLayout; // 右下， 第三个观众显示区域
    private ViewGroup[] viewLayouts = new ViewGroup[3];
    private ImageView fullScreenImage; // 显示全屏按钮
    private ImageView cancelFullScreenImage; //取消全屏显示按钮

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_room, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
//        setupPager();
        registerObservers(true);
//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!isPermissionInit) {
//                    requestPermissionMembers();
//                }
//            }
//        }, 5000);
//        requestLivePermission();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (ChatRoomActivity) context;
        videoListener = (VideoListener) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerObservers(false);

        if (roomId != null) {
            NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
            clearChatRoom();
        }
    }

    private void clearChatRoom() {
        Logger.d(TAG, "chat room do clear");
        AVChatManager.getInstance().leaveRoom2(roomId, new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Logger.d(TAG, "leave channel success");
            }

            @Override
            public void onFailed(int i) {
                Logger.d(TAG, "leave channel failed, code:" + i);
            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
        AVChatManager.getInstance().disableRtc();
        RTSManager2.getInstance().leaveSession(roomId, new RTSCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Logger.d(TAG, "leave rts session success");
            }

            @Override
            public void onFailed(int i) {
                Logger.d(TAG, "leave rts session failed, code:" + i);
            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
        ChatRoomMemberCache.getInstance().clearRoomCache(roomId);
    }

    /**
     * ********************************** 子页面 **********************************
     */

    private void setupPager() {
        tabs.setupWithViewPager(viewPager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return null;
                    case 1:
                        return null;
                    case 2:
                        return null;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "白板";
                    case 1:
                        return "讨论";
                    case 2:
                        return "成员";
                }
                return super.getPageTitle(position);
            }
        });
    }

    /**
     * ********************************** View初始化 **********************************
     */

    private void findViews() {
        // 直播区域
        videoLayout = findView(R.id.view_layout);
        backLayout = findView(R.id.back_layout);
        fullScreenView = findView(R.id.full_screen_view);
        fullScreenLayout = findView(R.id.full_screen_layout);
        // 顶部控制台
        videoLayout.setOnClickListener(v -> {
            isBackLayoutShow = !isBackLayoutShow;
            backLayout.setVisibility(isBackLayoutShow ? View.GONE : View.VISIBLE);
        });
        masterVideoLayout = findView(R.id.master_video_layout);
        firstRightVideoLayout = findView(R.id.first_video_layout);
        secondLeftVideoLayout = findView(R.id.second_video_layout);
        thirdRightVideoLayout = findView(R.id.third_video_layout);
        fullScreenImage = findView(R.id.full_screen_image);
        cancelFullScreenImage = findView(R.id.cancel_full_screen_image);
        fullScreenImage.setOnClickListener(this);
        cancelFullScreenImage.setOnClickListener(this);

        viewLayouts[0] = firstRightVideoLayout;
        viewLayouts[1] = secondLeftVideoLayout;
        viewLayouts[2] = thirdRightVideoLayout;

        roomIdText = findView(R.id.room_id);
        statusText = findView(R.id.online_status);
        findView(R.id.back_arrow).setOnClickListener(v -> onBackPressed());

        videoPermissionBtn = findView(R.id.video_permission_btn);
        audioPermissionBtn = findView(R.id.audio_permission_btn);
        interactionStartCloseBtn = findView(R.id.close_apply_btn);

        videoPermissionBtn.setOnClickListener(this);
        audioPermissionBtn.setOnClickListener(this);
        interactionStartCloseBtn.setOnClickListener(this);

        // 聊天室区域
        tabs = findView(R.id.chat_room_tabs);
        viewPager = findView(R.id.chat_room_view_pager);
    }

    // 初始化UI
    public void initLiveVideo(ChatRoomInfo roomInfo, String channelName, final boolean isCreate, String shareUrl, ModuleProxy moduleProxy) {
        this.roomInfo = roomInfo;
        this.roomId = roomInfo.getRoomId();
        this.roomName = channelName;
        this.shareUrl = shareUrl;
        this.isCreate = isCreate;
        roomIdText.setText(String.format("房间:%s", roomId));
        // 开启音视频引擎
        AVChatManager.getInstance().enableRtc();
        // 打开视频模块
        AVChatManager.getInstance().enableVideo();
        // 设置视频采集模块
        if (videoCapturer == null) {
            videoCapturer = AVChatVideoCapturerFactory.createCameraCapturer();
            AVChatManager.getInstance().setupVideoCapturer(videoCapturer);
        }
        // 如果是主播, 设置本地预览画布
        if (isCreate) {
            AVChatManager.getInstance().setupLocalVideoRender(masterRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
            AVChatManager.getInstance().startVideoPreview();
            AVChatManager.getInstance().setParameter(AVChatParameters.KEY_SESSION_MULTI_MODE_USER_ROLE, AVChatUserRole.NORMAL);
            ChatRoomMemberCache.getInstance().savePermissionMemberbyId(roomId, roomInfo.getCreator());
        } else {
            AVChatManager.getInstance().setParameter(AVChatParameters.KEY_SESSION_MULTI_MODE_USER_ROLE, AVChatUserRole.AUDIENCE);
        }
        AVChatManager.getInstance().joinRoom2(roomId, AVChatType.VIDEO, new AVChatCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData avChatData) {
                Logger.d("join channel success, extra:" + avChatData.getExtra());
                // 设置音量信号监听, 通过AVChatStateObserver的onReportSpeaker回调音量大小
                AVChatParameters avChatParameters = new AVChatParameters();
                avChatParameters.setBoolean(AVChatParameters.KEY_AUDIO_REPORT_SPEAKER, true);
                AVChatManager.getInstance().setParameters(avChatParameters);
            }

            @Override
            public void onFailed(int i) {
                Logger.d("join channel failed, code:" + i);
//                Toast.makeText(activity, "join channel failed, code:" + i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable throwable) {

            }
        });

        updateControlUI();
        switchHandsUpLayout();
        updateVideoAudioUI();
    }

    private void updateControlUI() {
        if (isCreate) {
            videoPermissionBtn.setVisibility(View.VISIBLE);
            audioPermissionBtn.setVisibility(View.VISIBLE);
            interactionStartCloseBtn.setVisibility(View.GONE);
        } else if (ChatRoomMemberCache.getInstance().hasPermission(roomId, NetEaseCache.getAccount())) {
            videoPermissionBtn.setVisibility(View.VISIBLE);
            audioPermissionBtn.setVisibility(View.VISIBLE);
            interactionStartCloseBtn.setVisibility(View.VISIBLE);
            interactionStartCloseBtn.setText(R.string.finish);
        } else {
            videoPermissionBtn.setVisibility(View.GONE);
            audioPermissionBtn.setVisibility(View.GONE);
            interactionStartCloseBtn.setVisibility(View.VISIBLE);
            interactionStartCloseBtn.setText(R.string.interaction);
        }
    }

    // 举手布局/取消举手布局切换
    private void switchHandsUpLayout() {
        if (!ChatRoomMemberCache.getInstance().isMyHandsUp(roomId)) {
            // 没举手
            interactionStartCloseBtn.setText(R.string.interaction);
        } else if (ChatRoomMemberCache.getInstance().isMyHandsUp(roomId)
                && !ChatRoomMemberCache.getInstance().hasPermission(roomId, NetEaseCache.getAccount())) {
            // 举手等待主播通过
            interactionStartCloseBtn.setText(R.string.cancel);
        } else if (ChatRoomMemberCache.getInstance().hasPermission(roomId, NetEaseCache.getAccount())) {
            // 主播通过，进行互动
            interactionStartCloseBtn.setText(R.string.finish);
        }
    }

    private void updateVideoAudioUI() {
        videoPermissionBtn.setBackgroundResource(!AVChatManager.getInstance().isLocalVideoMuted()
                ? R.drawable.chat_room_video_on_selector : R.drawable.chat_room_video_off_selector);
        audioPermissionBtn.setBackgroundResource(!AVChatManager.getInstance().isLocalAudioMuted()
                ? R.drawable.chat_room_audio_on_selector : R.drawable.chat_room_audio_off_selector);
    }

    public void onOnlineStatusChanged(boolean isOnline) {
        statusText.setVisibility(isOnline ? View.GONE : View.VISIBLE);
        NIMClient.getService(ChatRoomService.class).fetchRoomInfo(roomId).setCallback(new RequestCallback<ChatRoomInfo>() {
            @Override
            public void onSuccess(ChatRoomInfo chatRoomInfo) {
                roomInfo = chatRoomInfo;
                ((ChatRoomActivity) getActivity()).setRoomInfo(roomInfo);
                updateDeskShareUI();
            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }

    /**
     * **********************************  监 听  **********************************
     */

    private void registerObservers(boolean register) {
        AVChatManager.getInstance().observeAVChatState(this, register);
        ChatRoomMemberCache.getInstance().registerMeetingControlObserver(meetingControlObserver, register);
        ChatRoomMemberCache.getInstance().registerRoomMemberChangedObserver(roomMemberChangedObserver, register);
        ChatRoomMemberCache.getInstance().registerRoomInfoChangedObserver(roomInfoChangedObserver, register);
    }

    ChatRoomMemberCache.MeetingControlObserver meetingControlObserver = new ChatRoomMemberCache.MeetingControlObserver() {
        @Override
        public void onAccept(String roomID) {
            if (checkRoom(roomID)) {
                return;
            }
            chooseSpeechType();
        }

        @Override
        public void onReject(String roomID) {

        }

        @Override
        public void onPermissionResponse(String roomId, List<String> accounts) {
            if (checkRoom(roomId)) {
                return;
            }
            for (String a : accounts) {
                Logger.i(TAG, "on permission response, account:" + a);
                ChatRoomMemberCache.getInstance().savePermissionMemberbyId(roomId, a);
                onVideoOn(a);
            }
        }

        @Override
        public void onSendMyPermission(String roomID, String toAccount) {
            if (checkRoom(roomID)) {
                return;
            }

            if (ChatRoomMemberCache.getInstance().hasPermission(roomID, NetEaseCache.getAccount())) {
                List<String> accounts = new ArrayList<>(1);
                accounts.add(NetEaseCache.getAccount());
                MsgHelper.getInstance().sendP2PCustomNotification(roomID, MeetingOptCommand.STATUS_RESPONSE.getValue(),
                        toAccount, accounts);
            }
        }

        @Override
        public void onSaveMemberPermission(String roomID, List<String> accounts) {
            if (checkRoom(roomID)) {
                return;
            }
            saveMemberPermission(accounts);
        }

        @Override
        public void onHandsUp(String roomID, String account) {
            if (checkRoom(roomID)) {
                return;
            }
            ChatRoomMemberCache.getInstance().saveMemberHandsUpDown(roomId, account, true);
            onTabChange(true);
        }

        @Override
        public void onHandsDown(String roomID, String account) {
            if (checkRoom(roomID)) {
                return;
            }
            ChatRoomMemberCache.getInstance().saveMemberHandsUpDown(roomID, account, false);
            onTabChange(false);
            if (ChatRoomMemberCache.getInstance().hasPermission(roomID, account)) {
                removeMemberPermission(account);
            }
        }

        @Override
        public void onStatusNotify(String roomID, List<String> accounts) {
            if (checkRoom(roomID)) {
                return;
            }
            onPermissionChange(accounts);
            updateControlUI();
        }
    };

    private boolean checkRoom(String roomID) {
        return TextUtils.isEmpty(roomId) || !roomId.equals(roomID);
    }

    ChatRoomMemberCache.RoomMemberChangedObserver roomMemberChangedObserver = new ChatRoomMemberCache.RoomMemberChangedObserver() {
        @Override
        public void onRoomMemberIn(ChatRoomMember member) {
            onMasterJoin(member.getAccount());

            if (NetEaseCache.getAccount().equals(roomInfo.getCreator())
                    && !member.getAccount().equals(NetEaseCache.getAccount())) {
                // 主持人点对点通知有权限的成员列表
                // 主持人自己进来，不需要通知自己
                MsgHelper.getInstance().sendP2PCustomNotification(roomId, MeetingOptCommand.ALL_STATUS.getValue(),
                        member.getAccount(), ChatRoomMemberCache.getInstance().getPermissionMems(roomId));
            }

            if (member.getAccount().equals(roomInfo.getCreator())) {
                // 主持人重新进来,观众要取消自己的举手状态
                ChatRoomMemberCache.getInstance().saveMyHandsUpDown(roomId, false);
            }

            if (member.getAccount().equals(roomInfo.getCreator()) && NetEaseCache.getAccount().equals(roomInfo.getCreator())) {
                // 主持人自己重新进来，清空观众的举手状态
                ChatRoomMemberCache.getInstance().clearAllHandsUp(roomId);
                // 重新向所有成员请求权限
                requestPermissionMembers();
            }
        }

        @Override
        public void onRoomMemberExit(ChatRoomMember member) {
            // 主持人要清空离开成员的举手
            if (NetEaseCache.getAccount().equals(roomInfo.getCreator())) {
                ChatRoomMemberCache.getInstance().removeHandsUpMem(roomId, member.getAccount());
            }

            // 用户离开频道，如果是有权限用户，移除下画布
            if (member.getAccount().equals(roomInfo.getCreator())) {
                masterVideoLayout.removeAllViews();
            } else if (ChatRoomMemberCache.getInstance().hasPermission(roomId, member.getAccount())) {
                removeMemberPermission(member.getAccount());
            }
        }
    };

    ChatRoomMemberCache.RoomInfoChangedObserver roomInfoChangedObserver = new ChatRoomMemberCache.RoomInfoChangedObserver() {
        @Override
        public void onRoomInfoUpdate(IMMessage message) {
            ChatRoomNotificationAttachment attachment = (ChatRoomNotificationAttachment) message.getAttachment();
            if (attachment != null && attachment.getExtension() != null) {
                Map<String, Object> ext = attachment.getExtension();
                switchFullScreen(ext);
            }
        }
    };

    private void removeMemberPermission(String account) {
        ChatRoomMemberCache.getInstance().removePermissionMem(roomId, account);
        onVideoOff(account);
        if (NetEaseCache.getAccount().equals(roomInfo.getCreator()) && !account.equals(NetEaseCache.getAccount())) {
            MsgHelper.getInstance().sendCustomMsg(roomId, MeetingOptCommand.ALL_STATUS);
        }
    }

    /**
     * **********************************  监 听end  **********************************
     */

    // 选择发言方式
    @SuppressLint("RestrictedApi")
    private void chooseSpeechType() {
        final CharSequence[] items = {"语音", "视频"}; // 设置选择内容
        final boolean[] checkedItems = {true, true};// 设置默认选中
        String content = "";
        if (ChatRoomMemberCache.getInstance().isMyHandsUp(roomId)) {
            content = "主持人已通过你的发言申请，\n";
        } else {
            content = "主持人开通了你的发言权限，\n";
        }
        CheckBox checkBox = new CheckBox(activity);
        checkBox.setText("白板互动(常开)");
        checkBox.setEnabled(false);
        checkBox.setChecked(true);
        new AlertDialog.Builder(activity)
                .setTitle(content +
                        "请选择发言方式：")
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                })
                .setView(checkBox, 40, 0, 0, 0)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!ChatRoomMemberCache.getInstance().hasPermission(roomId, NetEaseCache.getAccount())) {
                            return;
                        }

                        AVChatManager.getInstance().enableAudienceRole(false);

                        if (!checkedItems[0]) {
                            AVChatManager.getInstance().muteLocalAudio(true);
                        } else {
                            AVChatManager.getInstance().muteLocalAudio(false);
                        }

                        if (!checkedItems[1]) {
                            AVChatManager.getInstance().muteLocalVideo(true);
                        } else {
                            AVChatManager.getInstance().muteLocalVideo(false);
                        }

                        ChatRoomMemberCache.getInstance().setRTSOpen(true);

                        videoListener.onAcceptConfirm();
                        updateControlUI();
                        updateVideoAudioUI();
                    }
                })
                .setCancelable(false)
                .show();
    }

    // 全屏显示和最小化显示的切换
    private void switchFullScreen(Map<String, Object> ext) {
        if (ext.containsKey(MeetingConstant.FULL_SCREEN_TYPE)) {
            int fullScreenType = (int) ext.get(MeetingConstant.FULL_SCREEN_TYPE);
            if (fullScreenType == FullScreenType.CLOSE.getValue()) {
                cancelFullScreen();
                fullScreenImage.setVisibility(View.GONE);
            } else if (fullScreenType == FullScreenType.OPEN.getValue()) {
                doFullScreen();
                fullScreenImage.setVisibility(View.VISIBLE);
            }
        }
    }

    // 举手红点提醒
    public void onTabChange(boolean notify) {
        // 移动端不需要创建房间，不需要举手提醒
    }

    // 更新成员权限缓存
    private void saveMemberPermission(List<String> accounts) {
        isPermissionInit = true;
        onPermissionChange(accounts);
    }

    // 向所有人请求成员权限
    private void requestPermissionMembers() {
        Logger.d(TAG, "request permission members");
        MsgHelper.getInstance().sendCustomMsg(roomId, MeetingOptCommand.GET_STATUS);
    }

    // 权限变化
    public void onPermissionChange(List<String> accounts) {
        List<String> oldAccounts = new ArrayList<>();
        if (ChatRoomMemberCache.getInstance().getPermissionMems(roomId) != null) {
            oldAccounts.addAll(ChatRoomMemberCache.getInstance().getPermissionMems(roomId));
        }
        // accounts是新的所有人权限。如果oldaccounts不在这个里面，就remove，在就add
        for (String a : oldAccounts) {
            if (a.equals(roomInfo.getCreator())) {
                continue;
            }
            if (accounts.contains(a)) {
                Logger.i(TAG, "on permission change, add:" + a);
                // 新增权限
                ChatRoomMemberCache.getInstance().savePermissionMemberbyId(roomId, a);
                onVideoOn(a);
            } else {
                Logger.i(TAG, "on permission change, remove:" + a);
                ChatRoomMemberCache.getInstance().removePermissionMem(roomId, a);
                onVideoOff(a);
                ChatRoomMemberCache.getInstance().setRTSOpen(false);
            }
        }

        accounts.removeAll(oldAccounts);
        for (String a : accounts) {
            ChatRoomMemberCache.getInstance().savePermissionMemberbyId(roomId, a);
            if (a.equals(roomInfo.getCreator())) {
                continue;
            }
            onVideoOn(a);
        }
    }

    // 将有权限的成员添加到画布
    public void onVideoOn(String account) {
        Map<Integer, String> imageMap = ChatRoomMemberCache.getInstance().getImageMap(roomId);
        if (imageMap == null) {
            imageMap = new HashMap<>();
        }

        showView(imageMap, account);

        ChatRoomMemberCache.getInstance().saveImageMap(roomId, imageMap);
    }

    // 显示成员图像
    private void showView(Map<Integer, String> imageMap, String a) {
        if (userJoinedList != null && userJoinedList.contains(a)
                && !roomInfo.getCreator().equals(a)
                && !imageMap.containsValue(a) && imageMap.size() < 3) {
            for (int i = 0; i < 3; i++) {
                if (!imageMap.containsKey(i)) {
                    AVChatSurfaceViewRenderer render = new AVChatSurfaceViewRenderer(getActivity());
                    boolean isSetup = false;
                    try {
                        if (NetEaseCache.getAccount().equals(a)) {
                            isSetup = AVChatManager.getInstance().setupLocalVideoRender(render, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
                        } else {
                            isSetup = AVChatManager.getInstance().setupRemoteVideoRender(a, render, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
                        }
                        Logger.i("setup render, creator account:" + roomInfo.getCreator() + ", render account:" + a + ", isSetup:" + isSetup);
                    } catch (Exception e) {
                        Logger.e("set up video render error:" + e.getMessage());
                        e.printStackTrace();
                    }
                    if (isSetup && render != null) {
                        imageMap.put(i, a);
                        addIntoPreviewLayout(render, viewLayouts[i]);
                    }
                    break;
                }
            }
        }
    }

    // 添加到成员显示的画布
    private void addIntoPreviewLayout(SurfaceView surfaceView, ViewGroup viewLayout) {
        if (surfaceView == null) {
            return;
        }
        if (surfaceView.getParent() != null)
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        viewLayout.addView(surfaceView);
        surfaceView.setZOrderMediaOverlay(true);
    }


    public void onBackPressed() {
        logoutChatRoom();
    }

    private void logoutChatRoom() {
        AVChatManager.getInstance().stopVideoPreview();
        AVChatManager.getInstance().disableVideo();
        activity.finish();
    }

    public void onKickOut() {
        Logger.d("chat room do kick out");
        activity.finish();
    }

    /**************************** AVChatStateObserver ****************************/

    @Override
    public void onJoinedChannel(int code, String audioFile, String videoFile, int elapsed) {

    }

    @Override
    public void onUserJoined(String account) {
        userJoinedList.add(account);
        onMasterJoin(account);
        if (ChatRoomMemberCache.getInstance().hasPermission(roomId, account) && !account.equals(roomInfo.getCreator())) {
            onVideoOn(account);
        }
    }

    @Override
    public void onUserLeave(String account, int event) {
        // 用户离开频道，如果是有权限用户，移除下画布
        if (ChatRoomMemberCache.getInstance().hasPermission(roomId, account) && !account.equals(roomInfo.getCreator())) {
            onVideoOff(account);
        } else if (account.equals(roomInfo.getCreator())) {
            masterVideoLayout.removeAllViews();
        }
        ChatRoomMemberCache.getInstance().removePermissionMem(roomId, account);
        videoListener.onUserLeave(account);
        userJoinedList.remove(account);
    }

    @Override
    public void onLeaveChannel() {
        userJoinedList.remove(NetEaseCache.getAccount());
    }

    @Override
    public void onProtocolIncompatible(int status) {

    }

    @Override
    public void onDisconnectServer(int code) {

    }

    @Override
    public void onNetworkQuality(String account, int quality, AVChatNetworkStats stats) {

    }

    @Override
    public void onCallEstablished() {
        userJoinedList.add(NetEaseCache.getAccount());
        onMasterJoin(NetEaseCache.getAccount());
    }

    @Override
    public void onDeviceEvent(int code, String desc) {

    }

    @Override
    public void onConnectionTypeChanged(int netType) {

    }

    @Override
    public void onFirstVideoFrameAvailable(String account) {

    }

    @Override
    public void onFirstVideoFrameRendered(String account) {

    }

    @Override
    public void onVideoFrameResolutionChanged(String account, int width, int height, int rotate) {

    }

    @Override
    public void onVideoFpsReported(String account, int fps) {

    }

    @Override
    public boolean onVideoFrameFilter(AVChatVideoFrame frame, boolean maybeDualInput) {
        return false;
    }

    @Override
    public boolean onAudioFrameFilter(AVChatAudioFrame frame) {
        return false;
    }

    @Override
    public void onAudioDeviceChanged(int device) {

    }

    @Override
    public void onReportSpeaker(Map<String, Integer> speakers, int mixedEnergy) {
        videoListener.onReportSpeaker(speakers);
    }

    @Override
    public void onSessionStats(AVChatSessionStats sessionStats) {

    }

    @Override
    public void onLiveEvent(int event) {

    }

    /**************************** 画布的显示和取消 ****************************/

    // 主持人进入频道
    private void onMasterJoin(String s) {
        if (userJoinedList != null && userJoinedList.contains(s) && s.equals(roomInfo.getCreator())) {
            if (masterRender == null) {
                masterRender = new AVChatSurfaceViewRenderer(getActivity());
            }

            boolean isSetup = setupMasterRender(s, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
            if (isSetup && masterRender != null) {
                addIntoMasterPreviewLayout(masterRender);
                ChatRoomMemberCache.getInstance().savePermissionMemberbyId(roomId, roomInfo.getCreator());
                updateDeskShareUI();
            }
        }
    }

    private void updateDeskShareUI() {
        Map<String, Object> ext = roomInfo.getExtension();
        if (ext != null && ext.containsKey(MeetingConstant.FULL_SCREEN_TYPE)) {
            int fullScreenType = (int) ext.get(MeetingConstant.FULL_SCREEN_TYPE);
            if (fullScreenType == FullScreenType.CLOSE.getValue()) {
                fullScreenImage.setVisibility(View.GONE);
            } else if (fullScreenType == FullScreenType.OPEN.getValue()) {
                fullScreenImage.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean setupMasterRender(String s, int mode) {
        if (TextUtils.isEmpty(s)) {
            return false;
        }
        boolean isSetup = false;
        try {
            if (s.equals(NetEaseCache.getAccount())) {
                isSetup = AVChatManager.getInstance().setupLocalVideoRender(masterRender, false, mode);
            } else {
                isSetup = AVChatManager.getInstance().setupRemoteVideoRender(s, masterRender, false, mode);
            }
        } catch (Exception e) {
            Logger.e("set up video render error:" + e.getMessage());
            e.printStackTrace();
        }
        return isSetup;
    }

    // 将主持人添加到主持人画布
    private void addIntoMasterPreviewLayout(SurfaceView surfaceView) {
        if (surfaceView.getParent() != null)
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        masterVideoLayout.addView(surfaceView);
        surfaceView.setZOrderMediaOverlay(true);
    }

    private void doFullScreen() {
        cancelFullScreenImage.setVisibility(View.VISIBLE);
        fullScreenImage.setVisibility(View.GONE);
        fullScreenLayout.setVisibility(View.VISIBLE);
        if (masterRender == null) {
            masterRender = new AVChatSurfaceViewRenderer(getActivity());
        }
        if (masterRender.getParent() != null) {
            ((ViewGroup) masterRender.getParent()).removeView(masterRender);
        }
        setupMasterRender(roomInfo.getCreator(), AVChatVideoScalingType.SCALE_ASPECT_FIT);
        fullScreenView.addView(masterRender);
        masterRender.setZOrderMediaOverlay(true);
        removeViews();
    }

    private void cancelFullScreen() {
        fullScreenImage.setVisibility(View.VISIBLE);
        cancelFullScreenImage.setVisibility(View.GONE);
        fullScreenLayout.setVisibility(View.GONE);
        if (masterRender == null) {
            return;
        }
        if (masterRender.getParent() != null) {
            ((ViewGroup) masterRender.getParent()).removeView(masterRender);
        }
        setupMasterRender(roomInfo.getCreator(), AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        addIntoMasterPreviewLayout(masterRender);
        showView();
    }

    // 取消全屏显示共享桌面时，显示其他画面。
    private void showView() {
        Map<Integer, String> map = ChatRoomMemberCache.getInstance().getImageMap(roomId);
        if (map == null) {
            return;
        }
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            AVChatSurfaceViewRenderer render = new AVChatSurfaceViewRenderer(getActivity());
            if (NetEaseCache.getAccount().equals(entry.getValue())) {
                AVChatManager.getInstance().setupLocalVideoRender(render, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
            } else {
                AVChatManager.getInstance().setupRemoteVideoRender(entry.getValue(), render, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
            }
            addIntoPreviewLayout(render, viewLayouts[entry.getKey()]);
        }
    }

    // 全屏显示共享桌面时，移除其他画面，否则会叠加显示
    private void removeViews() {
        for (ViewGroup viewLayout : viewLayouts) {
            viewLayout.removeAllViews();
        }
    }

    // 将被取消权限的成员从画布移除, 并将角色置为初始状态
    public void onVideoOff(String account) {
        Map<Integer, String> imageMap = ChatRoomMemberCache.getInstance().getImageMap(roomId);
        if (imageMap == null) {
            return;
        }
        removeView(imageMap, account);
        resetRole(account);
    }

    // 移除成员图像
    private void removeView(Map<Integer, String> imageMap, String account) {
        Iterator<Map.Entry<Integer, String>> it = imageMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, String> entry = it.next();
            if (entry.getValue().equals(account)) {
                viewLayouts[entry.getKey()].removeAllViews();
                it.remove();
                break;
            }
        }
    }

    // 恢复为观众角色
    private void resetRole(String account) {
        if (account.equals(NetEaseCache.getAccount())) {
            AVChatManager.getInstance().muteLocalAudio(true);
            AVChatManager.getInstance().enableAudienceRole(true);
            AVChatManager.getInstance().muteLocalAudio(false);
            AVChatManager.getInstance().muteLocalVideo(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_permission_btn:
                if (ChatRoomMemberCache.getInstance().hasPermission(roomId, NetEaseCache.getAccount())) {
                    setVideoState();
                }
                break;
            case R.id.audio_permission_btn:
                if (ChatRoomMemberCache.getInstance().hasPermission(roomId, NetEaseCache.getAccount())) {
                    setAudioState();
                }
                break;
            case R.id.close_apply_btn:
                speakRequestCancel();
                switchHandsUpLayout();
                break;
            case R.id.full_screen_image:
                doFullScreen();
                break;
            case R.id.cancel_full_screen_image:
                cancelFullScreen();
                break;
        }
    }

    // 设置自己的摄像头是否开启
    private void setVideoState() {
        if (AVChatManager.getInstance().isLocalVideoMuted()) {
            videoPermissionBtn.setBackgroundResource(R.drawable.chat_room_video_on_selector);
            AVChatManager.getInstance().muteLocalVideo(false);
        } else {
            videoPermissionBtn.setBackgroundResource(R.drawable.chat_room_video_off_selector);
            AVChatManager.getInstance().muteLocalVideo(true);
        }
    }

    // 设置自己的录音是否开启
    private void setAudioState() {
        if (AVChatManager.getInstance().isLocalAudioMuted()) {
            audioPermissionBtn.setBackgroundResource(R.drawable.chat_room_audio_on_selector);
            AVChatManager.getInstance().muteLocalAudio(false);
        } else {
            audioPermissionBtn.setBackgroundResource(R.drawable.chat_room_audio_off_selector);
            AVChatManager.getInstance().muteLocalAudio(true);
        }
    }

    private void speakRequestCancel() {
        if (ChatRoomMemberCache.getInstance().hasPermission(roomId, NetEaseCache.getAccount())) {
            // 结束互动
            cancelInteractionConfirm();
        } else if (ChatRoomMemberCache.getInstance().isMyHandsUp(roomId)) {
            // 取消互动
            MsgHelper.getInstance().sendP2PCustomNotification(roomId, MeetingOptCommand.SPEAK_REQUEST_CANCEL.getValue(),
                    roomInfo.getCreator(), null);
            ChatRoomMemberCache.getInstance().saveMyHandsUpDown(roomId, false);
        } else {
            // 申请互动
            MsgHelper.getInstance().sendP2PCustomNotification(roomId, MeetingOptCommand.SPEAK_REQUEST.getValue(),
                    roomInfo.getCreator(), null);
            ChatRoomMemberCache.getInstance().saveMyHandsUpDown(roomId, true);
        }
    }

    private void cancelInteractionConfirm() {
        // TODO：结束互动 后续添加一个dialog用于确认
        MsgHelper.getInstance().sendP2PCustomNotification(roomId, MeetingOptCommand.SPEAK_REQUEST_CANCEL.getValue(),
                roomInfo.getCreator(), null);
        ChatRoomMemberCache.getInstance().saveMyHandsUpDown(roomId, false);
//        EasyAlertDialogHelper.createOkCancelDiolag(activity, getString(R.string.operation_confirm),
//                getString(R.string.exit_interaction), getString(R.string.exit), getString(R.string.cancel), true,
//                new EasyAlertDialogHelper.OnDialogActionListener() {
//                    @Override
//                    public void doCancelAction() {
//
//                    }
//
//                    @Override
//                    public void doOkAction() {
//                        MsgHelper.getInstance().sendP2PCustomNotification(roomId, MeetingOptCommand.SPEAK_REQUEST_CANCEL.getValue(),
//                                roomInfo.getCreator(), null);
//                        ChatRoomMemberCache.getInstance().saveMyHandsUpDown(roomId, false);
//                    }
//                }).show();
    }
}
