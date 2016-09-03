package com.ericsson.addroneapplication.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ericsson.addroneapplication.model.UIDataPack;

/**
 * Created by Kamil on 8/26/2016.
 */
public class HudView extends View {

    private static final float PI2 = (float) (Math.PI / 2);
    private static final float PI4 = (float) (Math.PI / 4);
    private static final float PI12 = (float) (Math.PI / 12);

    private static final String[] YAW_LABELS = {
            "N", "15", "30", "45", "60", "75",
            "E", "105", "120", "135", "150", "165",
            "S", "195", "210", "225", "240", "255",
            "W", "285", "300", "315", "330", "345" };

    private Paint linePaint;
    private Paint borderLinePaint;
    private Paint textPaint;
    private Paint borderTextPaint;

    private boolean advancedMode;
    private boolean isAutopilotAvailable;

    private float width, height;

    private float lineWidth;
    private float borderWidth;
    private float borderLineWidth;
    private float textHeight;


    private HudTextFactory hudTextFactory;
    private UIDataPack uiDataPack;

    private Rect drawingRect = new Rect();

    public HudView(Context context, AttributeSet attrs) {
        super(context, attrs);

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);

        borderLinePaint = new Paint();
        borderLinePaint.setColor(Color.BLACK);

        textPaint = new Paint();
        textPaint.setColor(Color.BLUE);
        textPaint.setTextSize(32);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        borderTextPaint = new Paint();
        borderTextPaint.setColor(Color.WHITE);
        borderTextPaint.setTextSize(32);
        borderTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        borderTextPaint.setStyle(Paint.Style.STROKE);
        borderTextPaint.setStrokeWidth(2);

        advancedMode = true;
        isAutopilotAvailable = false;

        hudTextFactory = new HudTextFactory(context);

        uiDataPack = new UIDataPack();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;

        lineWidth = 6;
        borderWidth = 2;
        borderLineWidth = lineWidth + 2 * borderWidth;

        linePaint.setStrokeWidth(lineWidth);
        borderLinePaint.setStrokeWidth(borderLineWidth);

        Rect rect = new Rect();
        borderTextPaint.getTextBounds("xX1", 0, 3, rect);
        textHeight = rect.height();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw drone state

        // draw ping and autopilot availability
        String pingString = hudTextFactory.getLatencyString(uiDataPack.ping);
        drawTextWithBorder(pingString, 10, 10 + textHeight, Paint.Align.LEFT, canvas);

        // draw autopilot state
        String autopilotString = hudTextFactory.getAutopilotString(isAutopilotAvailable);
        drawTextWithBorder(autopilotString, 10, 10 + 3 * textHeight, Paint.Align.LEFT, canvas);

        // draw battery state
        String batteryString = hudTextFactory.getBatteryStateString(uiDataPack.battery, 10);
        drawTextWithBorder(batteryString, width * .5f, 10 + textHeight, Paint.Align.CENTER, canvas);

        // draw current time
        String dateString = hudTextFactory.getDateString();
        textPaint.getTextBounds(dateString, 0, dateString.length(), drawingRect);
        drawTextWithBorder(dateString, width, 10 + textHeight, Paint.Align.RIGHT, canvas);

        // draw current position
        String positionString = hudTextFactory.getPositionString(uiDataPack.lat, uiDataPack.lng);
        drawTextWithBorder(positionString, width * .5f, height - 10, Paint.Align.CENTER, canvas);

        if(advancedMode) {
            // draw yaw bar
            updateYawBar(canvas);

            // draw vel bar
            drawVerticalDividedLine(.25f, .25f, .75f, .3f, .1f, .075f * height / width, canvas);

            // draw alt bar
            updateAltitudeBar(canvas);

            // draw tilt line
        }
    }

    /**
     * @param x1 x coordinate of beginning of the line
     * @param x2 x coordinate of the end of the line
     * @param y y coordinate of the line
     * @param divX x coordinate of first dividing line
     * @param divD length between neighbouring dividing lines
     * @param divH height of the dividing lines
     * @param canvas canvas to draw on
     */
    private void drawHorizontalDividedLine(float x1, float x2, float y, float divX, float divD, float divH, String[] labels, int pos, Canvas canvas) {
        float pHeight = y * height;
        float pDivY1 = (y - divH / 2) * height;
        float pDivY2 = (y + divH / 2) * height;
        float px;

        canvas.drawLine(width * x1 - borderWidth, pHeight, width * x2 + borderWidth, pHeight, borderLinePaint);

        for(float x = divX; x < x2; x += divD) {
            px = x * width;
            canvas.drawLine(px, pDivY1 - borderWidth, px, pDivY2 + borderWidth, borderLinePaint);
        }

        canvas.drawLine(width * x1, pHeight, width * x2, pHeight, linePaint);

        int i = pos >= labels.length ? 0 : pos;
        for(float x = divX; x < x2; x += divD) {
            px = x * width;
            canvas.drawLine(px, pDivY1, px, pDivY2, linePaint);

            drawTextWithBorder(labels[i], px, pDivY1 - textHeight, Paint.Align.CENTER, canvas);

            if(++i >= labels.length)
                i = 0;
        }
    }

    /**
     * @param x x coordinate of the line
     * @param y1 y coordinate of beginning of the line
     * @param y2 y coordinate of the end of the line
     * @param divY y coordinate of the first dividing line
     * @param divD length between neighbouring dividing lines
     * @param divW width of the dividing lines
     * @param canvas canvas to draw on
     */
    private void drawVerticalDividedLine(float x, float y1, float y2, float divY, float divD, float divW, Canvas canvas) {
        float pWidth = x * width;
        float pDivX1 = (x - divW / 2) * width;
        float pDivX2 = (x + divW / 2) * width;
        float py;

        canvas.drawLine(pWidth, y1 * height - borderWidth, pWidth, y2 * height + borderWidth, borderLinePaint);

        for(float y = divY; y < y2; y += divD) {
            py = y * height;
            canvas.drawLine(pDivX1 - borderWidth, py, pDivX2 + borderWidth, py, borderLinePaint);
        }

        canvas.drawLine(pWidth, y1 * height, pWidth, y2 * height, linePaint);

        for(float y = divY; y < y2; y += divD) {
            py = y * height;
            canvas.drawLine(pDivX1, py, pDivX2, py, linePaint);
        }
    }

    private void drawTextWithBorder(String text, float x, float y, Paint.Align align, Canvas canvas) {
        borderTextPaint.setTextAlign(align);
        textPaint.setTextAlign(align);

        canvas.drawText(text, x, y, borderTextPaint);
        canvas.drawText(text, x, y, textPaint);
    }

    public void updateYawBar(Canvas canvas) {
        float yaw = uiDataPack.yaw;
        float leftBound = yaw - PI4;
        if(leftBound < 0) leftBound += Math.PI * 2;
        int firstBarNumber = (int)(leftBound / PI12) + 1;
        float firstBar = firstBarNumber * PI12;

        if(yaw < PI4) {
            yaw += 2 * Math.PI;
        }

        float delta = Math.abs(yaw - firstBar) / PI2;

        drawHorizontalDividedLine(.25f, .75f, .15f, .5f - delta * .5f, .5f * (PI12 / PI2), .075f, YAW_LABELS, firstBarNumber, canvas);
        String yawText = hudTextFactory.getYawText(uiDataPack.yaw);
        drawTextWithBorder(yawText, width * 0.5f, .225f * height + textHeight, Paint.Align.CENTER, canvas);
    }

    public void updateAltitudeBar(Canvas canvas) {
        float upperBound = uiDataPack.altitude - 25;
        int firstBarNumber = (int)(upperBound / 10) + 1;

        float delta = Math.abs(uiDataPack.altitude - firstBarNumber * 10) / 25.f;

        Log.i("ABS", String.valueOf(delta));

        drawVerticalDividedLine(.75f, .25f, .75f, .5f - delta, .5f * .2f, .065f, canvas);
        String altText = hudTextFactory.getAltText(uiDataPack.altitude);
        drawTextWithBorder(altText, .685f * width - textHeight, height * .5f, Paint.Align.RIGHT, canvas);
    }

    public void setAdvancedMode(boolean advancedMode) {
        this.advancedMode = advancedMode;
    }

    public void setAutopilotAvailability(boolean isAutopilotAvailable) {
        this.isAutopilotAvailable = isAutopilotAvailable;
    }

    public void updateUiDataPack(UIDataPack uiDataPack) {
        this.uiDataPack = uiDataPack;
        invalidate();
    }
}
