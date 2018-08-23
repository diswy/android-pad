package com.cqebd.module_student_classroom;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cqebd.module_student_classroom.ui.activity.ClassroomActivity;

public class HomeReceiver extends BroadcastReceiver {
    static public final String SYSTEM_DIALOG_REASON_KEY = "reason";
    static public final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
    static public final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    static public final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    static public final String SYSTEM_DIALOG_REASON_ASSIST = "assist";
    public void onReceive(Context arg0, Intent arg1) {

        String action = arg1.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {                                           //按下Home键会发送ACTION_CLOSE_SYSTEM_DIALOGS的广播
            String reason = arg1.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (reason != null) {
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    Intent intent = new Intent(arg0, ClassroomActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);       //点击home键无延时，且不会产生新的activity
                    PendingIntent pendingIntent = PendingIntent.getActivity(arg0, 0,
                            intent, 0);
                    try {
                        pendingIntent.send();

                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
