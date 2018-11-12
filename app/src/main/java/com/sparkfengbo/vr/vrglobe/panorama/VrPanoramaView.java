package com.sparkfengbo.vr.vrglobe.panorama;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by fengbo on 2018/11/12.
 */

public class VrPanoramaView extends GLSurfaceView {
    private VrPanoramaViewRenderer mRenderer;

    private float mDownX = 0.0f;
    private float mDownY = 0.0f;

    public VrPanoramaView(Context context) {
        super(context);
        init();
    }

    public VrPanoramaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mRenderer = new VrPanoramaViewRenderer();
        setRenderer(mRenderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                return true;
            case MotionEvent.ACTION_MOVE:
                float mX = event.getX();
                float mY = event.getY();
                mRenderer.mLightX += (mX-mDownX)/10;
                mRenderer.mLightY -= (mY-mDownY)/10;
                mDownX = mX;
                mDownY = mY;
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    //TODO startLoad bitmap or  asset file or bitmap file
    public void startLoad() {

    }
}
