package org.book2words.models

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan

public class WordSpannable(private val background: Int,
                           private val foreground: Int,
                           private val padding: Float) : ReplacementSpan() {
    private val rectangle = RectF()

    override fun getSize(paint: Paint?, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        return Math.round(paint!!.measureText(text, start, end) + padding);
    }

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        rectangle.set(x.toFloat(), top.toFloat(), (x + paint.measureText(text, start, end) + padding).toFloat() , bottom.toFloat());
        paint.setColor(background);
        canvas.drawRect(rectangle, paint);

        // Text
        paint.setColor(foreground);
        val xPos = Math.round(x + (padding / 2));
        val yPos = ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2))  ;
        canvas.drawText(text, start, end, xPos.toFloat(), yPos.toFloat(), paint);
    }
}

/*extends ReplacementSpan {

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        // Background
        mRect.set(x, top, x + paint.measureText(text, start, end) + PADDING, bottom);
        paint.setColor(mBackgroundColor);
        canvas.drawRect(mRect, paint);

        // Text
        paint.setColor(mForegroundColor);
        int xPos = Math.round(x + (PADDING / 2));
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;
        canvas.drawText(text, start, end, xPos, yPos, paint);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
        return Math.round(paint.measureText(text, start, end) + PADDING);
    }
}*/