package com.cqebd.student.live

import android.view.Gravity
import android.view.View
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.glide.GlideApp
import com.cqebd.student.vo.entity.UserAccount
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.item_chat_room_text.view.*

class ChatRoomMultipleAdapter(data: List<ChatRoomEntity>?) : BaseMultiItemQuickAdapter<ChatRoomEntity, BaseViewHolder>(data) {

    init {
        addItemType(ChatRoomEntity.TEXT, R.layout.item_chat_room_text)
        addItemType(ChatRoomEntity.IMG, R.layout.item_chat_room_img)
    }

    override fun convert(helper: BaseViewHolder?, item: ChatRoomEntity) {
        when (helper?.itemViewType) {
            ChatRoomEntity.TEXT -> {
                helper.itemView.apply {
                    mOtherAvatar.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                    mMyAvatar.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                    mChatRoomContent.gravity = if (item.isMyself) Gravity.END else Gravity.START
                    mChatRoomContent.text = item.content

                    Logger.d(UserAccount.load()?.Avatar)
                    GlideApp.with(mContext)
                            .asBitmap()
                            .circleCrop()
                            .load(if (item.isMyself) UserAccount.load()?.Avatar else UserAccount.load()?.Avatar)
                            .into(if (item.isMyself) mMyAvatar else mOtherAvatar)
                }
            }
            ChatRoomEntity.IMG -> {

            }
        }
    }
}