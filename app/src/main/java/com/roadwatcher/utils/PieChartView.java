package com.roadwatcher.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class PieChartView extends View {
    private List<Float> data;
    private List<Integer> colors;
    private final Paint paint = new Paint();

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
    }

    public void setData(List<Float> data, List<Integer> colors) {
        this.data = data;
        this.colors = colors;
        invalidate(); // Redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null || colors == null || data.isEmpty()) return;

        float total = 0;
        for (float value : data) total += value;

        float startAngle = 0;
        int radius = Math.min(getWidth(), getHeight()) / 2 - 50;

        for (int i = 0; i < data.size(); i++) {
            paint.setColor(colors.get(i));
            float sweepAngle = (data.get(i) / total) * 360;
            canvas.drawArc(50, 50, radius * 2, radius * 2, startAngle, sweepAngle, true, paint);
            startAngle += sweepAngle;
        }
    }
}
