package com.cqebd.student.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cqebd.student.R;
import com.cqebd.student.glide.GlideApp;
import com.google.gson.Gson;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomStatusChangeData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;

import gorden.library.Album;

public class TestChatRoomActivity extends AppCompatActivity {
    private final static String EXTRA_ROOM_ID = "ROOM_ID";
    private final static String EXTRA_MODE = "EXTRA_MODE";

    /**
     * 聊天室基本信息
     */
    private String roomId;
    private ChatRoomInfo roomInfo;

    private boolean isCreate; // true 主持人模式，false 观众模式

    private EditText et;
    private TextView tv;
    private Button btn;
    private ImageView iv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_chat_room);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        parseIntent();


        et = findViewById(R.id.et_send);
        tv = findViewById(R.id.tv_extend);
        btn = findViewById(R.id.btn_send);
        iv = findViewById(R.id.test_iv);
        findViewById(R.id.btn_send_img).setOnClickListener(v ->
                Album.Companion.create().single().start(this, Album.REQUEST_CODE)
        );

        btn.setOnClickListener(v -> {
            // 创建聊天室文本消息
            ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(roomId, et.getText().toString());

            // 将文本消息发送出去
            NIMClient.getService(ChatRoomService.class).sendMessage(message, false)
                    .setCallback(new RequestCallback<Void>() {
                        @Override
                        public void onSuccess(Void param) {
                            // 成功
                            Logger.e("onsuccess");
                        }

                        @Override
                        public void onFailed(int code) {
                            // 失败
                            Logger.e("onFailed");

                        }

                        @Override
                        public void onException(Throwable exception) {
                            // 错误
                            Logger.e("onException:" + exception.getMessage());

                        }
                    });
        });


        NIMClient.getService(ChatRoomServiceObserver.class)
                .observeReceiveMessage(incomingChatRoomMsg, true);


        // 监听消息状态变化
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(statusObserver, true);

        // 注册监听
        registerObservers(true);

        // 登录聊天室
        enterRoom();
    }

    private Observer<IMMessage> statusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage msg) {
            // 1、根据sessionId判断是否是自己的消息
            // 2、更改内存中消息的状态
            // 3、刷新界面

            com.cqebd.student.tools.Toast.show("回调成功");
            if (msg.getAttachment() instanceof ImageAttachment) {

                GlideApp.with(TestChatRoomActivity.this)
                        .load(((ImageAttachment) msg.getAttachment()).getPath())
                        .into(iv);
                Logger.d(((ImageAttachment) msg.getAttachment()).getPath());
            } else {
                Logger.d("这不属于图片");
                String ss = tv.getText().toString();
                ss = ss + "\n" + msg.getContent();
                tv.setText(ss);
            }
        }
    };

    Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> messages) {
            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(i).getAttachment() != null) {
                    Logger.e("-------Attachment不为空");

                    if (messages.get(i).getAttachStatus() == AttachStatusEnum.fail) {
                        Logger.e("-------下载失败！");
                    }

                    if (messages.get(i).getAttachment() instanceof ImageAttachment) {

                        if (isOriginImageHasDownloaded(messages.get(i))) {
                            String path = ((ImageAttachment) messages.get(i).getAttachment()).getPath();
                            Logger.e("-------图片地址，下载成功的：" + path);
                        } else {
                            // 下载附件，参数1为消息对象，参数2为是下载缩略图还是下载原图。
                            // 因为下载的文件可能会很大，这个接口返回类型为 AbortableFuture ，允许用户中途取消下载。
                            Logger.d("------执行下载！");

                            AbortableFuture future = NIMClient.getService(MsgService.class).downloadAttachment(messages.get(i), false);
                        }

//                        String path = ((ImageAttachment) messages.get(i).getAttachment()).getPath();
//                        String path2 = ((ImageAttachment) messages.get(i).getAttachment()).getThumbPath();
//                        String path3 = ((ImageAttachment) messages.get(i).getAttachment()).getThumbUrl();
//                        Logger.e("-------图片地址：" + path);
//                        Logger.e("-------图片地址：" + path2);
//                        Logger.e("-------图片地址：" + path3);
                    } else {
//                        Logger.e("-------getAttachment不为空但是没走！");
                    }


                } else {
                    String ss = tv.getText().toString();
                    ss = ss + "\n" + messages.get(i).getContent();
                    tv.setText(ss);
                }
            }
            // 处理新收到的消息
//            Logger.w("聊天：",messages);
//            String ss = tv.getText().toString();
//            for (int i = 0; i < messages.size(); i++) {
//                ss = ss + "\n" + messages.get(i).getContent();
//            }
//            tv.setText(ss);
        }
    };


    private AbortableFuture<EnterChatRoomResultData> enterRequest;


    private String creator;

    private void enterRoom() {
        EnterChatRoomData data = new EnterChatRoomData(roomId);
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {
            @Override
            public void onSuccess(EnterChatRoomResultData result) {
                enterRequest = null;

                roomInfo = result.getRoomInfo();
                ChatRoomMember member = result.getMember();
                member.setRoomId(roomInfo.getRoomId());
//                ChatRoomMemberCache.getInstance().saveMyMember(member);

                creator = roomInfo.getCreator();
            }

            @Override
            public void onFailed(int code) {
                enterRequest = null;
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    Toast.makeText(TestChatRoomActivity.this, "你已被拉入黑名单，不能再进入", Toast.LENGTH_SHORT).show();
                } else if (code == ResponseCode.RES_ENONEXIST) {
                    Toast.makeText(TestChatRoomActivity.this, "该聊天室不存在", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TestChatRoomActivity.this, "enter chat room failed, code=" + code, Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            @Override
            public void onException(Throwable exception) {
                enterRequest = null;
                Toast.makeText(TestChatRoomActivity.this, "enter chat room exception, e=" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void parseIntent() {
        roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        isCreate = getIntent().getBooleanExtra(EXTRA_MODE, false);
    }

    public static void start(Context context, String roomId, boolean isCreate) {
        Intent intent = new Intent();
        intent.setClass(context, TestChatRoomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_ROOM_ID, roomId);
        intent.putExtra(EXTRA_MODE, isCreate);
        context.startActivity(intent);
    }


    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeOnlineStatus(onlineStatus, register);
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, register);
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, register);
    }

    private Fragment fragment = null;

    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {
        @Override
        public void onEvent(StatusCode statusCode) {
            if (statusCode.wontAutoLogin()) {
                NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
                if (fragment != null) {
//                    fragment.onKickOut();
                }
            }
        }
    };

    Observer<ChatRoomStatusChangeData> onlineStatus = new Observer<ChatRoomStatusChangeData>() {
        @Override
        public void onEvent(ChatRoomStatusChangeData chatRoomStatusChangeData) {
            if (chatRoomStatusChangeData.status == StatusCode.CONNECTING) {
//                DialogMaker.updateLoadingMessage("连接中...");
            } else if (chatRoomStatusChangeData.status == StatusCode.UNLOGIN) {
                if (NIMClient.getService(ChatRoomService.class).getEnterErrorCode(roomId) == ResponseCode.RES_CHATROOM_STATUS_EXCEPTION) {
                    // 聊天室连接状态异常
//                    Toast.makeText(ChatRoomActivity.this, R.string.chatroom_status_exception, Toast.LENGTH_SHORT).show();
                    NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
                    if (fragment != null) {
//                        fragment.onKickOut();
                    }
                } else {
//                    Toast.makeText(ChatRoomActivity.this, R.string.nim_status_unlogin, Toast.LENGTH_SHORT).show();
                    if (fragment != null) {
//                        fragment.onOnlineStatusChanged(false);
                    }
                }
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINING) {
//                DialogMaker.updateLoadingMessage("登录中...");
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINED) {
                if (fragment != null) {
//                    fragment.onOnlineStatusChanged(true);
                }
            } else if (chatRoomStatusChangeData.status.wontAutoLogin()) {
            } else if (chatRoomStatusChangeData.status == StatusCode.NET_BROKEN) {
//                Toast.makeText(ChatRoomActivity.this, R.string.net_broken, Toast.LENGTH_SHORT).show();
                if (fragment != null) {
//                    fragment.onOnlineStatusChanged(false);
                }
            }
            Logger.i("Chat Room Online Status:" + chatRoomStatusChangeData.status.name());
        }
    };

    Observer<ChatRoomKickOutEvent> kickOutObserver = new Observer<ChatRoomKickOutEvent>() {
        @Override
        public void onEvent(ChatRoomKickOutEvent chatRoomKickOutEvent) {
            if (chatRoomKickOutEvent.getReason() == ChatRoomKickOutEvent.ChatRoomKickOutReason.CHAT_ROOM_INVALID) {
//                if (!roomInfo.getCreator().equals(DemoCache.getAccount()))
//                    Toast.makeText(ChatRoomActivity.this, R.string.meeting_closed, Toast.LENGTH_SHORT).show();
            } else if (chatRoomKickOutEvent.getReason() == ChatRoomKickOutEvent.ChatRoomKickOutReason.KICK_OUT_BY_MANAGER) {
//                Toast.makeText(ChatRoomActivity.this, R.string.kick_out_by_master, Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(ChatRoomActivity.this, "被踢出聊天室，reason:" + chatRoomKickOutEvent.getReason(), Toast.LENGTH_SHORT).show();
            }

            if (fragment != null) {
//                fragment.onKickOut();
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case Album.REQUEST_CODE:
                    List<String> pathList = data.getStringArrayListExtra(Album.KEY_IMAGES);
                    // 创建聊天室文本消息
//                    ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(roomId, et.getText().toString());
                    Logger.d(pathList.get(0));
                    ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomImageMessage(roomId, new File(pathList.get(0)), "pic");

                    // 将文本消息发送出去
                    NIMClient.getService(ChatRoomService.class).sendMessage(message, false)
                            .setCallback(new RequestCallback<Void>() {
                                @Override
                                public void onSuccess(Void param) {
                                    // 成功
                                    Logger.e("onSuccess Pic");
                                    com.cqebd.student.tools.Toast.show("onSuccess Pic");
                                }

                                @Override
                                public void onFailed(int code) {
                                    // 失败
                                    Logger.e("onFailed");
                                    com.cqebd.student.tools.Toast.show("onFailed Pic");


                                }

                                @Override
                                public void onException(Throwable exception) {
                                    // 错误
                                    Logger.e("onException:" + exception.getMessage());
                                    com.cqebd.student.tools.Toast.show("onException" + exception.getMessage());


                                }
                            });
                    break;
            }

//                Album.REQUEST_CODE -> {
//                    val pathList = data!!.getStringArrayListExtra(Album.KEY_IMAGES)
//                    crop(pathList[0])
//                }
        }
    }


    // 下载之前判断一下是否已经下载。若重复下载，会报错误码414。（以SnapChatAttachment为例）
    private boolean isOriginImageHasDownloaded(final IMMessage message) {
        if (message instanceof ImageAttachment) {
            if (message.getAttachStatus() == AttachStatusEnum.transferred &&
                    !TextUtils.isEmpty(((ImageAttachment) message.getAttachment()).getPath())) {
                return true;
            }
        }
        return false;
    }

}
