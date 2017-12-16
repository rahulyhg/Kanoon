package com.rvsoftlab.kanoon.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.rvsoftlab.kanoon.R;

/**
 * Created by User on 16-12-2017.
 */

public class KextView extends android.support.v7.widget.AppCompatTextView {

    private interface TYPE{
        int REGULAR = 0;
        int BLACK = 1;
        int BOLD = 2;
        int HEAVY = 5;
        int THIN = 3;
        int LIGHT = 4;
    }

    private interface DISTANCE{
        float NORMAL = 0;
        float TITLE = 0.3f;
        float BIG_TITLE = 0.6f;
    }

    public KextView(Context context) {
        super(context);
        init(null,0);
    }

    public KextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    public KextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }

    /*public KextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs,defStyleAttr);
    }*/

    private void init(AttributeSet attrs, int style) {
        if (attrs!=null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.KextView,0,0);
            try {
                int font = typedArray.getInt(R.styleable.KextView_fontType,0);
                int spacing = typedArray.getInt(R.styleable.KextView_fontSpacing,0);
                Typeface typeface;
                switch (font){
                    case TYPE.REGULAR:
                        typeface = ResourcesCompat.getFont(getContext(),R.font.regular_xml);
                        break;
                    case TYPE.BLACK:
                        typeface = ResourcesCompat.getFont(getContext(),R.font.black_xml);
                        break;
                    case TYPE.BOLD:
                        typeface = ResourcesCompat.getFont(getContext(),R.font.bold_xml);
                        break;
                    case TYPE.HEAVY:
                        typeface = ResourcesCompat.getFont(getContext(),R.font.heavy_xml);
                        break;
                    case TYPE.LIGHT:
                        typeface = ResourcesCompat.getFont(getContext(),R.font.light_xml);
                        break;
                    case TYPE.THIN:
                        typeface = ResourcesCompat.getFont(getContext(),R.font.thin_xml);
                        break;
                    default:
                        typeface = ResourcesCompat.getFont(getContext(),R.font.regular_xml);
                }
                float space;
                switch (spacing){
                    case 0:
                        space = DISTANCE.NORMAL;
                        break;
                    case 1:
                        space = DISTANCE.TITLE;
                        break;
                    case 2:
                        space = DISTANCE.BIG_TITLE;
                        break;
                    default:
                        space = DISTANCE.NORMAL;
                }
                setTypeface(typeface,style);
                setLetterSpacing(space);
            }finally {
                typedArray.recycle();
            }
        }else {
            apply(ResourcesCompat.getFont(getContext(),R.font.regular_xml),style,DISTANCE.NORMAL);
            setTypeface(ResourcesCompat.getFont(getContext(),R.font.regular_xml),style);
            setLetterSpacing(0);
        }
    }

    private void apply(Typeface typeface,int style, float space){
        setTypeface(typeface,style);
        setLetterSpacing(space);
    }
}
