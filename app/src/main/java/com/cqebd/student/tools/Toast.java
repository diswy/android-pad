package com.cqebd.student.tools;


import com.cqebd.student.app.App;

/**
 * Created by gorden on 2016/5/13.
 */
public class Toast {
    private static android.widget.Toast toast;

    public static void show(CharSequence msg){
        if(toast==null){
            toast= android.widget.Toast.makeText(App.mContext,"", android.widget.Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.setDuration(android.widget.Toast.LENGTH_SHORT);
        toast.show();
    }
}
