package com.cqebd.student.live.adapter

import android.graphics.drawable.AnimationDrawable
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.live.entity.ChatRoomEntity
import com.cqebd.student.vo.entity.UserAccount
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import kotlinx.android.synthetic.main.item_chat_room_img.view.*
import kotlinx.android.synthetic.main.item_chat_room_text.view.*
import kotlinx.android.synthetic.main.item_chat_room_audio.view.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor

class ChatRoomMultipleAdapter(data: List<ChatRoomEntity>?) : BaseMultiItemQuickAdapter<ChatRoomEntity, BaseViewHolder>(data) {

    init {
        addItemType(ChatRoomEntity.TEXT, R.layout.item_chat_room_text)
        addItemType(ChatRoomEntity.IMG, R.layout.item_chat_room_img)
        addItemType(ChatRoomEntity.AUDIO, R.layout.item_chat_room_audio)
    }

    private var mAudioCallback: ((path: String, animator: AnimationDrawable) -> Unit)? = null

    fun setAudioCallback(listener: (path: String, animator: AnimationDrawable) -> Unit) {
        this.mAudioCallback = listener
    }

    private var mPicCallback: ((view: View, path: String) -> Unit)? = null

    fun setPicListener(listener: (view: View, path: String) -> Unit) {
        this.mPicCallback = listener
    }

    override fun convert(helper: BaseViewHolder?, item: ChatRoomEntity) {
        when (helper?.itemViewType) {
            ChatRoomEntity.TEXT -> {
                helper.itemView.apply {
                    if (item.account.toLowerCase().contains("teacher")) {// 老师消息
                        mOtherAvatar.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mMyAvatar.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mStartSpace.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mEndSpace.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mChatRoomContent.text = item.content
                        mChatRoomContent.textColor = if (item.isMyself) context.resources.getColor(R.color.white) else context.resources.getColor(R.color.red)
                        mChatRoomContent.backgroundResource = if (item.isMyself) R.drawable.chat_bg_me else R.drawable.chat_bg_others
                        mChatRoomNick.text = "老师"

                        GlideApp.with(mContext)
                                .asBitmap()
                                .circleCrop()
                                .load(R.drawable.avtar_teacher)
                                .into(if (item.isMyself) mMyAvatar else mOtherAvatar)
                    } else {
                        mOtherAvatar.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mMyAvatar.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mStartSpace.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mEndSpace.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mChatRoomContent.text = item.content
                        mChatRoomContent.textColor = if (item.isMyself) context.resources.getColor(R.color.white) else context.resources.getColor(R.color.black)
                        mChatRoomContent.backgroundResource = if (item.isMyself) R.drawable.chat_bg_me else R.drawable.chat_bg_others
                        mChatRoomNick.text = item.nick
                        mChatRoomNick.gravity = if (item.isMyself) Gravity.END else Gravity.START
                        GlideApp.with(mContext)
                                .asBitmap()
                                .circleCrop()
                                .placeholder(R.drawable.ic_avatar)
                                .load(if (item.isMyself) UserAccount.load()?.Avatar else item.avatar)
                                .into(if (item.isMyself) mMyAvatar else mOtherAvatar)
                    }
                }
            }
            ChatRoomEntity.IMG -> {
                helper.itemView.apply {
                    if (item.account.toLowerCase().contains("teacher")) {// 老师消息
                        mImgOtherAvatar.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mImgMyAvatar.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mImgStartSpace.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mImgEndSpace.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mChatRoomNick2.text = "老师"
                        mChatRoomNick2.gravity = if (item.isMyself) Gravity.END else Gravity.START

                        GlideApp.with(mContext)
                                .load(item.imgSrc)
                                .fitCenter()
                                .into(mImgIv)

                        GlideApp.with(mContext)
                                .asBitmap()
                                .circleCrop()
                                .load(R.drawable.avtar_teacher)
                                .into(if (item.isMyself) mImgMyAvatar else mImgOtherAvatar)
                    } else {
                        mImgOtherAvatar.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mImgMyAvatar.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mImgStartSpace.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mImgEndSpace.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mChatRoomNick2.text = item.nick

                        GlideApp.with(mContext)
                                .load(item.imgSrc)
                                .fitCenter()
                                .into(mImgIv)

                        GlideApp.with(mContext)
                                .asBitmap()
                                .circleCrop()
                                .placeholder(R.drawable.ic_avatar)
                                .load(if (item.isMyself) UserAccount.load()?.Avatar else item.avatar)
                                .into(if (item.isMyself) mImgMyAvatar else mImgOtherAvatar)
                    }

                    mImgIv.setOnClickListener {
                        mPicCallback?.invoke(it, item.imgSrc)
                    }
                }
            }
            ChatRoomEntity.AUDIO -> {
                helper.itemView.apply {
                    mAudioOtherAvatar.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                    mAudioMyAvatar.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                    mAudioStartSpace.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                    mAudioEndSpace.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                    mAudioChatRoomNick.text = item.nick
                    mAudioChatRoomNick.gravity = if (item.isMyself) Gravity.END else Gravity.START

                    mAudioPlay.setBackgroundResource(if (item.isMyself) R.drawable.chat_bg_me else R.drawable.chat_bg_others)
                    tv_duration.setTextColor(if (item.isMyself) ContextCompat.getColor(mContext, R.color.colorWhite)
                    else ContextCompat.getColor(mContext, R.color.colorPrimary))
                    iv_voice.setImageDrawable(if (item.isMyself) ContextCompat.getDrawable(mContext, R.drawable.animation_inner_voice)
                    else ContextCompat.getDrawable(mContext, R.drawable.animation_inner_other_voice))

                    GlideApp.with(mContext)
                            .asBitmap()
                            .circleCrop()
                            .placeholder(R.drawable.ic_avatar)
                            .load(if (item.isMyself) UserAccount.load()?.Avatar else item.avatar)
                            .into(if (item.isMyself) mAudioMyAvatar else mAudioOtherAvatar)

                    tv_duration.text = "${(item.audioDuration / 1000).toInt()}“"

                    val animator = iv_voice.drawable as AnimationDrawable

                    mAudioPlay.setOnClickListener {
                        mAudioCallback?.invoke(item.fileUrl, animator)
                    }
                }
            }
        }
    }
}