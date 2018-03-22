package com.cqebd.student.ui

import android.os.Bundle
import android.widget.ImageView
import com.anko.static.dp
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.widget.DraftPaperView
import com.cqebd.student.widget.floatactionmenu.FloatingActionMenu
import gorden.util.DensityUtil
import kotlinx.android.synthetic.main.activity_draft_paper.*

/**
 * 描述
 * Created by gorden on 2018/3/21.
 */
class DraftPaperActivity : BaseActivity() {
    private lateinit var btn_revoked: ImageView
    private lateinit var btn_restore: ImageView
    private lateinit var btn_clear: ImageView
    private lateinit var btn_mode: ImageView
    private lateinit var btn_palette: ImageView

    override fun setContentView() {
        setContentView(R.layout.activity_draft_paper)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val dp35 = 35.dp
        val dp5 = 5.dp

        btn_palette = ImageView(this)
        btn_palette.setPadding(dp5, dp5, dp5, dp5)
        btn_palette.setBackgroundResource(R.drawable.shape_draft)
        btn_palette.setImageResource(R.drawable.ic_draft_palette)

        btn_mode = ImageView(this)
        btn_mode.setPadding(dp5, dp5, dp5, dp5)
        btn_mode.setBackgroundResource(R.drawable.shape_draft)
        btn_mode.setImageResource(R.drawable.ic_draft_eraser)

        btn_clear = ImageView(this)
        btn_clear.setPadding(dp5, dp5, dp5, dp5)
        btn_clear.setBackgroundResource(R.drawable.shape_draft)
        btn_clear.setImageResource(R.drawable.ic_draft_clear)

        btn_revoked = ImageView(this)
        btn_revoked.setPadding(dp5, dp5, dp5, dp5)
        btn_revoked.isEnabled = false
        btn_revoked.setBackgroundResource(R.drawable.shape_draft)
        btn_revoked.setImageResource(R.drawable.selector_revoked)

        btn_restore = ImageView(this)
        btn_restore.setPadding(dp5, dp5, dp5, dp5)
        btn_restore.isEnabled = false
        btn_restore.setBackgroundResource(R.drawable.shape_draft)
        btn_restore.setImageResource(R.drawable.selector_restore)

        FloatingActionMenu.Builder(this)
                .attachTo(draft_menu)
                .addSubActionView(btn_palette, dp35, dp35)
                .addSubActionView(btn_mode, dp35, dp35)
                .addSubActionView(btn_clear, dp35, dp35)
                .addSubActionView(btn_revoked, dp35, dp35)
                .addSubActionView(btn_restore, dp35, dp35)
                .setRadius(DensityUtil.dip2px(100, this))
                .build()

        draftView.setOnDrawCallBack(object : DraftPaperView.OnDrawCallBack {
            override fun revokedSize(size: Int) {
                btn_revoked.isEnabled = size > 0
            }

            override fun restoreSize(size: Int) {
                btn_restore.isEnabled = size > 0
            }
        })
        btn_revoked.setOnClickListener { v -> draftView.revoked() }
        btn_restore.setOnClickListener { v -> draftView.restore() }
        btn_clear.setOnClickListener { v -> draftView.clear() }

        btn_mode.setOnClickListener { v ->
            if (draftView.currentMode() == DraftPaperView.MODE_PEN) {
                draftView.modeEarser()
                btn_mode.setImageResource(R.drawable.ic_draft_pen)
            } else {
                draftView.modePen()
                btn_mode.setImageResource(R.drawable.ic_draft_eraser)
            }
        }

        btn_palette.setOnClickListener { v -> draftView.showPalette() }
    }

    override fun onDestroy() {
        super.onDestroy()
        draftView.recycle()
    }
}