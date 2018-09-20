package com.lecootech.market.common;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Gallery;

public class MyGallery extends Gallery {

    public MyGallery(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
    }

    public MyGallery(Context context, AttributeSet attrs) {
            super(context, attrs);
    }

    public MyGallery(Context context) {
            super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (getChildCount() > 0) {
                    Log.v("onLayout", "" + getChildAt(0).getLeft());
                    scrollTo(getChildAt(0).getLeft(), 0);
            }
    }
}