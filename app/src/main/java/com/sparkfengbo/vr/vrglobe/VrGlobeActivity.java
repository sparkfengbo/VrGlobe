package com.sparkfengbo.vr.vrglobe;

import com.sparkfengbo.vr.vrglobe.panorama.VrPanoramaView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class VrGlobeActivity extends Activity {

    private VrPanoramaView mPanoSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vr_globe);
        mPanoSurfaceView = (VrPanoramaView)findViewById(R.id.vr_pana_view);

//        mPanoSurfaceView.startLoad();
    }

}
