package com.ericsson.addroneapplication.controller;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by Kamil on 8/25/2016.
 */
public class HudGLSurfaceView extends GLSurfaceView {

    private final HudRenderer hudRenderer;

    public HudGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);
        hudRenderer = new HudRenderer();
        setRenderer(hudRenderer);
    }
}
