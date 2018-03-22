package com.cqebd.student.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


import com.cqebd.student.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 1 on 2016/7/1.
 */
public abstract class CropDialog extends Dialog {

    private TextView tittlevTv;

    public CropDialog(Context context, String title) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.preview_dialog);
        ButterKnife.bind(this);
        tittlevTv = findViewById(R.id.tittlev_tv);
        tittlevTv.setText(title);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setOnCancelListener(dialog -> onCancel());

        findViewById(R.id.cancle).setOnClickListener(view -> {
            dismiss();
            onCancel();
        });

        findViewById(R.id.sure).setOnClickListener(view -> {
            dismiss();
            onOk();
        });
    }

    public abstract void onOk();

    public abstract void onCancel();
}
