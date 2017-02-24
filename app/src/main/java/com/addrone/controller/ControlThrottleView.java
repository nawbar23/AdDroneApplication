package com.addrone.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.addrone.R;

/**
 * Created by Kamil on 8/24/2016.
 */
public class ControlThrottleView extends View {

    public interface OnControlThrottlePadChanged {
        void onControlThrottlePadChanged(float x, float y);
    }

    Bitmap bitmapPadLayout;
    Bitmap bitmapPadPointer;

    float pointerWidth2, pointerHeight2;

    float left, top;
    float width, height;

    float width2;

    float scaleX, scaleY;

    float currentPointerX, currentPointerY;

    float y;

    private OnControlThrottlePadChanged listener;

    public ControlThrottleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        bitmapPadLayout = BitmapFactory.decodeResource(getResources(), R.drawable.pad_throttle);
        bitmapPadPointer = BitmapFactory.decodeResource(getResources(), R.drawable.pad_pointer);

        pointerWidth2 = bitmapPadPointer.getWidth() / 2;
        pointerHeight2 = bitmapPadPointer.getHeight() / 2;

        y = 1.f;
    }

    public void reset() {
        currentPointerX = width2;
        currentPointerY = height;
        y = 1.f;
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

        scaleX = width / bitmapPadLayout.getWidth();
        scaleY = height / bitmapPadLayout.getHeight();

        currentPointerX = width2;
        currentPointerY = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmapPadPointer, currentPointerX - pointerWidth2, currentPointerY - pointerHeight2, null);

        canvas.scale(scaleX, scaleY);
        canvas.drawBitmap(bitmapPadLayout, 0, 0, null);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if(action ==  MotionEvent.ACTION_BUTTON_RELEASE ||
                action == MotionEvent.ACTION_UP) {
            currentPointerX = width2;

            if(listener != null) {
                listener.onControlThrottlePadChanged(0.f, 1.f - y);
            }

        } else if(action == MotionEvent.ACTION_BUTTON_PRESS ||
                action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_MOVE ||
                action == MotionEvent.ACTION_SCROLL) {

            float x = ((event.getRawX() - getX()) - width2) / width2;
            y = (event.getRawY() - getY()) / height;

            float xlen = Math.abs(x);

            if(xlen > 1) {
                x /= xlen;
            }

            y = Math.min(y, 1);
            y = Math.max(0, y);

            currentPointerX = (x * width2 + width2);
            currentPointerY = y * height;

            if(listener != null) {
                listener.onControlThrottlePadChanged(x, 1 - y);
            }
        }

        invalidate();

        return true;
    }

    public void setOnControlThrottlePadChangedListener(OnControlThrottlePadChanged listener) {
        this.listener = listener;
    }
}
