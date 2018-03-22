package gorden.album.item;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import gorden.album.R;

/**
 * 相机
 * Created by Gorden on 2017/4/2.
 */

public class ItemCamera extends LinearLayout{
    public ItemCamera(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        setBackgroundColor(Color.parseColor("#66000000"));
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(android.R.drawable.ic_menu_camera);
        addView(imageView,dip2px(40,context),dip2px(40,context));

        TextView textView = new TextView(context);
        textView.setText("拍摄照片");
        textView.setTextColor(ContextCompat.getColor(context, R.color.album_textcolor));
        textView.setTextSize(14);
        addView(textView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    }

    public static int dip2px(int dip, Context context) {
        return (int) (dip * context.getApplicationContext().getResources().getDisplayMetrics().density + 0.5f);
    }
}
