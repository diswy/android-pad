package gorden.album.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 刷新媒体库
 * Created by Gorden on 2017/4/2.
 */

public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private MediaScannerConnection mMs;
    private String filePath;

    public SingleMediaScanner(Context context) {
        mMs = new MediaScannerConnection(context, this);
    }

    public void scanFile(String filePath) {
        this.filePath = filePath;
        if (!TextUtils.isEmpty(filePath))
            mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(filePath, null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mMs.disconnect();
    }
}
