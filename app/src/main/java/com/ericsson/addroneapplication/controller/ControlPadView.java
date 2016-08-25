package com.ericsson.addroneapplication.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ericsson.addroneapplication.R;

/**
 * Created by Kamil on 8/24/2016.
 */
public class ControlPadView extends View {

    public interface OnControlPadChangedListener {
        void onControlPadChanged(float x, float y);
    }

    Bitmap bitmapPadLayout;
    Bitmap bitmapPadPointer;

    float pointerWidth2, pointerHeight2;

    float left, top;
    float width, height;

    // half width and height
    float width2, height2;

    float diameter;
    float scale;

    float currentPointerX, currentPointerY;

    private OnControlPadChangedListener listener;

    public ControlPadView(Context context, AttributeSet attrs) {
        super(context, attrs);

        bitmapPadLayout = BitmapFactory.decodeResource(getResources(), R.drawable.pad_layout);
        bitmapPadPointer = BitmapFactory.decodeResource(getResources(), R.drawable.pad_pointer);

        pointerWidth2 = bitmapPadPointer.getWidth() / 2;
        pointerHeight2 = bitmapPadPointer.getHeight() / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float xpad = (float)(getPaddingRight() + getPaddingLeft());
        float ypad = (float)(getPaddingBottom() + getPaddingTop());

        left = getLeft() + getPaddingLeft();
        top = getTop() + getPaddingTop();

        width = w - xpad;
        width2 = width / 2;

        height = h - ypad;
        height2 = height / 2;

        diameter = Math.min(width, height);
        scale = diameter / bitmapPadLayout.getWidth();

        currentPointerX = width2;
        currentPointerY = height2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmapPadPointer, currentPointerX - pointerWidth2, currentPointerY - pointerHeight2, null);

        canvas.scale(scale, scale);
        canvas.drawBitmap(bitmapPadLayout, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if(action ==  MotionEvent.ACTION_BUTTON_RELEASE ||
                action == MotionEvent.ACTION_UP) {
            currentPointerX = width2;
            currentPointerY = height2;

            if(listener != null) {
                listener.onControlPadChanged(0, 0);
            }

        } else if(action == MotionEvent.ACTION_BUTTON_PRESS ||
                action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_MOVE ||
                action == MotionEvent.ACTION_SCROLL) {

            float x = ((event.getRawX() - getX()) - width2) / width2;
            float y = ((event.getRawY() - getY()) - height2) / height2;

            float len = (float) Math.sqrt(x*x + y*y);

            if(len > 1) {
                x /= len;
                y /= len;
            }

            currentPointerX = (x * width2 + width2);
            currentPointerY = (y * height2 + height2);

            if(listener != null) {
                listener.onControlPadChanged(x, y);
            }
        }

        invalidate();

        return true;
    }

    public void setOnControlPadChangedListener(OnControlPadChangedListener listener) {
        this.listener = listener;
    }
}
