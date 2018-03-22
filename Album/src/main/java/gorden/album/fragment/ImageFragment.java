package gorden.album.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import gorden.album.R;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayListener;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.ImageFrom;

/**
 * document
 * Created by Gordn on 2017/4/7.
 */

public class ImageFragment extends Fragment implements ImageZoomer.OnViewTapListener {
    public static final String PARAM_IMAGE_URI = "PARAM_IMAGE_URI";
    SketchImageView imageView;
    private String imageUri;

    ProgressBar progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image1, container, false);
        imageView = (SketchImageView) rootView.findViewById(R.id.imageView);
        imageUri = getArguments().getString(PARAM_IMAGE_URI, "");

        progress = (ProgressBar) rootView.findViewById(R.id.progress);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupZoomAndLargeImage();
        imageView.getOptions().setCorrectImageOrientation(true);
        if (imageView.isSupportLargeImage()) {
            imageView.getLargeImageViewer().setPause(!isVisibleToUser());
        }

        imageView.setDisplayListener(new DisplayListener() {
            @Override
            public void onCompleted(ImageFrom imageFrom, String mimeType) {
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onStarted() {
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(ErrorCause errorCause) {
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onCanceled(CancelCause cancelCause) {
                progress.setVisibility(View.GONE);
            }
        });

        imageView.displayImage(imageUri);
    }

    private void setupZoomAndLargeImage() {
        imageView.setSupportZoom(true);
        imageView.setSupportLargeImage(true);
        imageView.getImageZoomer().setOnViewTapListener(this);
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        ((AlbumPreViewFragment) getParentFragment()).toggleToolbarVisibleState();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            onUserVisibleChanged(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserVisibleChanged(true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            onUserVisibleChanged(!isVisibleToUser);
            if (!isVisibleToUser) {
                imageView.getImageZoomer().zoom(imageView.getImageZoomer().getBaseZoomScale());
            }
        }
    }

    public boolean isVisibleToUser() {
        return isResumed() && getUserVisibleHint();
    }

    protected void onUserVisibleChanged(boolean paush) {
        // 不可见的时候暂停超大图查看器，节省内存
        if (imageView != null && imageView.isSupportLargeImage()) {
            imageView.getLargeImageViewer().setPause(paush);
        }
    }
}
