package gorden.behavior;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

/**
 * document 手机工具类
 * Created by Gordn on 2017/2/27.
 */

public class Phone {

    public static void call(Context mContext, String phone) {
        if (TextUtils.isEmpty(phone)) {
            return;
        }
        Intent dail = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        dail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(dail);
    }

    public static void sms(Context mContext, String phone, String msg) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.putExtra("address", phone);
        smsIntent.putExtra("sms_body", msg);
        smsIntent.setType("vnd.android-dir/mms-sms");
        mContext.startActivity(smsIntent);
    }

}
