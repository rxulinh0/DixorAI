package com.ai.dixorai;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class CustomDefTypefaceSpan extends MetricAffectingSpan {
    private final Typeface typeface;

    public CustomDefTypefaceSpan(Context context, String fontPath) {
        typeface = Typeface.createFromAsset(context.getAssets(), fontPath);
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        apply(p);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        apply(tp);
    }

    private void apply(Paint paint) {
        paint.setTypeface(typeface);
        paint.setFlags(paint.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}