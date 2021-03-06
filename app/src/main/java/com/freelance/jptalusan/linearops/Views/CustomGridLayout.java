package com.freelance.jptalusan.linearops.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.freelance.jptalusan.linearops.R;

/**
 * Created by JPTalusan on 30/04/2017.
 */

public class CustomGridLayout extends RelativeLayout {
    private static String TAG = "CustomGridLayout";
    private int rows = 0;
    private int cols = 0;
    private CustomGridLayout customGridLayout = this;
    private Dimensions dimensions = new Dimensions();
    private Dimensions scaledDimensons = new Dimensions();
    private Context context;

    public CustomGridLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomGridLayoutOptions,
                0, 0);

        try {
            rows = a.getInteger(R.styleable.CustomGridLayoutOptions_rows, 0);
            cols = a.getInteger(R.styleable.CustomGridLayoutOptions_cols, 0);
        } finally {
            a.recycle();
        }
        init();
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
        invalidate();
        requestLayout();
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
        invalidate();
        requestLayout();
    }

    private void init() {
        getViewDimensions();
    }

    private void getViewDimensions() {
        customGridLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                customGridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                dimensions.width  = customGridLayout.getMeasuredWidth();
                dimensions.height = customGridLayout.getMeasuredHeight();

                scaledDimensons.width  = dimensions.width  / cols;
                scaledDimensons.height = dimensions.height / rows;

                Log.d(TAG, "dimensions:" + dimensions.toString());
                Log.d(TAG, "scaled:" + scaledDimensons.toString());
            }
        });
    }

    public RelativeLayout.LayoutParams generateParams() {
        //(width, height)
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int) scaledDimensons.width,
                (int) scaledDimensons.height);

//        Log.d(TAG, "generateParams:" + scaledDimensons.toString());
        int rowFactor = getChildCount() / cols;
        int colFactor = getChildCount() % cols;

        double leftMargin = colFactor * scaledDimensons.width;
        double topMargin  = rowFactor * scaledDimensons.height;

        params.topMargin = (int) topMargin;
        params.leftMargin = (int) leftMargin;

        return params;
    }

    //TODO: too much work since the slider is triggering the listener multiple times
    public boolean addScaledImage(int imageResource) {
        if (getChildCount() < rows * cols) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(imageResource);
            imageView.setLayoutParams(generateParams());
//            Log.d(TAG, "imageView: " + imageView.getLayoutParams().width + "," + imageView.getLayoutParams().height);
            addView(imageView);
            return true;
        } else {
            Log.d(TAG, "layout is full.");
            return false;
        }
    }

    public void reset() {
    }

}
