package gorden.behavior;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * document
 * Created by Gordn on 2017/3/8.
 */

public class LoadingDialog {
    private static ProgressDialog progressDialog;
    public static boolean lock = false;

    public static void show(Activity context,String... title) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(title.length>1?Boolean.valueOf(title[1]):true);
        }
        if (title.length>0)
            progressDialog.setMessage(title[0]);
        else
            progressDialog.setMessage("请稍后...");
        if (!context.isFinishing())
            progressDialog.show();
    }

    public static void stop() {
        if (!lock&&null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog=null;
        }
    }
}
