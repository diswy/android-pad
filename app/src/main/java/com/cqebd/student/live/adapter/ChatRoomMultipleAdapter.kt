package com.cqebd.student.live.adapter

import android.view.View
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.live.entity.ChatRoomEntity
import com.cqebd.student.vo.entity.UserAccount
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import kotlinx.android.synthetic.main.item_chat_room_img.view.*
import kotlinx.android.synthetic.main.item_chat_room_text.view.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor

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
                    mStartSpace.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                    mEndSpace.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                    mChatRoomContent.text = item.content
                    mChatRoomContent.textColor = if (item.isMyself) R.color.white else R.color.black
                    mChatRoomContent.backgroundResource = if (item.isMyself) R.drawable.chat_bg_me else R.drawable.chat_bg_others

                    GlideApp.with(mContext)
                            .asBitmap()
                            .circleCrop()
                            .placeholder(R.drawable.ic_avatar)
                            .load(if (item.isMyself) UserAccount.load()?.Avatar else item.avatar)
                            .into(if (item.isMyself) mMyAvatar else mOtherAvatar)
                }
            }
            ChatRoomEntity.IMG -> {
                helper.itemView.apply {
                    mImgOtherAvatar.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                    mImgMyAvatar.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                    mImgStartSpace.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                    mImgEndSpace.visibility = if (item.isMyself) View.GONE else View.VISIBLE

                    GlideApp.with(mContext)
                            .load(item.imgSrc)
                            .centerCrop()
                            .into(mImgIv)

                    GlideApp.with(mContext)
                            .asBitmap()
                            .circleCrop()
                            .placeholder(R.drawable.ic_avatar)
                            .load(if (item.isMyself) UserAccount.load()?.Avatar else item.avatar)
                            .into(if (item.isMyself) mImgMyAvatar else mImgOtherAvatar)
                }
            }
        }
    }
}