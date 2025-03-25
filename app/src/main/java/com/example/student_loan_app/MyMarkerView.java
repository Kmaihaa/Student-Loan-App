package com.example.student_loan_app;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.MPPointF;

public class MyMarkerView extends MarkerView {

    private final TextView tvContent;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        // find your layout components
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // Update the marker text with the current entry's Y value
        tvContent.setText(String.format("$%.2f", e.getY()));
        super.refreshContent(e, highlight);
    }

    /**
     * If you only want a single offset for all points, override getOffset().
     * This example centers the marker horizontally and places it above the selected point.
     */
    @Override
    public MPPointF getOffset() {
        // Center the marker horizontally, and position it just above the point.
        // getWidth() / 2f moves it left by half its width so it’s centered,
        // and -getHeight() moves it above the point.
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }

    /*
     * Alternatively, if you need different offsets depending on the draw location,
     * you can override getOffsetForDrawingAtPoint(float x, float y):
     *
     * @Override
     * public MPPointF getOffsetForDrawingAtPoint(float x, float y) {
     *     return new MPPointF(-(getWidth() / 2f), -getHeight());
     * }
     */
}
