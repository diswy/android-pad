package gorden.behavior;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.Stack;

/**
 * Created by gorden on 2016/3/18.
 */
public class ActivityStack {
    private Stack<Activity> activityStack;

    private static ActivityStack instance = null;
    private ActivityStack() {
        Log.e("XXXXXXXXXXXXXX","SDK: "+ Build.VERSION.SDK_INT);
    }
    public static ActivityStack getInstance() {
        // 先检查实例是否存在，如果不存在才进入下面的同步块
        if (instance == null) {
            // 同步块，线程安全的创建实例
            synchronized (ActivityStack.class) {
                // 再次检查实例是否存在，如果不存在才真正的创建实例
                if (instance == null) {
                    instance = new ActivityStack();
                }
            }
        }
        return instance;
    }
    private void initActivityStack() {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
    }
    public void addActivity(Activity activity) {
        initActivityStack();
        activityStack.add(activity);
    }
    public void removeActivity(Activity activity) {
        if(activityStack!=null&&activityStack.size()>0){
            finishActivity(activity);
        }
    }

    public Activity topActivity() {
        Activity activity = activityStack.get(0);
        return activity;
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     * @return
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity){
        if(activity!=null){
            activityStack.remove(activity);
            activity.finish();
        }
    }
    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls){
        for (Activity activity : activityStack) {
            if(activity.getClass().equals(cls) ){
                finishActivity(activity);
            }
        }
    }
    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        //获取到当前Activity
        Activity activity = activityStack.lastElement();
        //结束指定Activity
        finishActivity(activity);
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity(){
        for (int i = 0, size = activityStack.size(); i < size; i++){
            if (null != activityStack.get(i)){
                Activity activity = activityStack.get(i);
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
        activityStack.clear();
    }
    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) { }
    }

    public void finishTopActivity(Class<?> cls){
        for(int i=activityStack.size()-1;i>0;i--){
            if(!activityStack.get(i).getClass().equals(cls)){
                finishActivity(activityStack.get(i));
            }
        }
    }
}
