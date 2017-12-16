package com.rvsoftlab.kanoon.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.Button;

import com.rvsoftlab.kanoon.R;

/**
 * Created by User on 16-12-2017.
 */

public class Kutton extends android.support.v7.widget.AppCompatButton {
    public Kutton(Context context) {
        super(context);
        init(null);
    }

    public Kutton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public Kutton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setTypeface(ResourcesCompat.getFont(getContext(), R.font.thin_xml), Typeface.BOLD);
        setLetterSpacing(0.3f);
        setTransformationMethod(null);
    }

}
