package gorden.system;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class Clipboard {

    public static void copy(String text, Context context) {

        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("copy", text));

    }

    public static ClipData paste(Context context) {
        return ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip();
    }

}
