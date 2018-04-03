package com.cqebd.student.netease.helper;


import com.cqebd.student.netease.NetEaseCache;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;

/**
 *
 * Created by hzxuwen on 2016/1/19.
 */
public class ChatRoomHelper {

    public static void init() {
        ChatRoomMemberCache.getInstance().clear();
        ChatRoomMemberCache.getInstance().registerObservers(true);
    }

    public static void logout() {
        ChatRoomMemberCache.getInstance().clear();
    }

    public static String showDisplayName(ChatRoomMessage message) {
        if (message.getChatRoomMessageExtension() != null) {
            return message.getChatRoomMessageExtension().getSenderNick();
        } else {
            return NetEaseCache.getUserInfo() == null ? NetEaseCache.getAccount() : NetEaseCache.getUserInfo().getName();
        }
    }
}
