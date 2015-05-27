/*
package com.nillsondg.theirlife;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.graphics.Matrix;

*/
/**
 * Created by Dmitry on 27.05.2015.
 *//*

public class TouchImageViewSample extends ImageView {

    private Paint borderPaint = null;
    private Paint backgroundPaint = null;

    private float mPosX = 0f;
    private float mPosY = 0f;

    private float mLastTouchX;
    private float mLastTouchY;
    Matrix savedMatrix = new Matrix();
    Matrix start = new Matrix();
    private static final int INVALID_POINTER_ID = -1;
    private static final String LOG_TAG = "TouchImageView";

    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    public TouchImageViewSample(Context context) {
        this(context, null, 0);
    }

    public TouchImageViewSample(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    // Existing code ...
    public TouchImageViewSample(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Create our ScaleGestureDetector
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        borderPaint = new Paint();
        borderPaint.setARGB(255, 255, 128, 0);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);

        backgroundPaint = new Paint();
        backgroundPaint.setARGB(32, 255, 255, 255);
        backgroundPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub

        ImageView view = (ImageView) v;
        dumpEvent(event);

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // ...
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    Log.d(LOG_TAG, "newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true;
    }

    private void dumpEvent(MotionEvent event) {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
        sb.append("]");
        Log.d(LOG_TAG, sb.toString());
    }

    */
/** Determine the space between the first two fingers *//*

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    */
/** Calculate the mid point of the first two fingers *//*

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    */
/*
     * (non-Javadoc)
     *
     * @see android.view.View#draw(android.graphics.Canvas)
     *//*

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawRect(0, 0, getWidth() - 1, getHeight() - 1, borderPaint);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth() - 1, getHeight() - 1, backgroundPaint);
        if (this.getDrawable() != null) {
            canvas.save();
            canvas.translate(mPosX, mPosY);

            Matrix matrix = new Matrix();
            matrix.postScale(mScaleFactor, mScaleFactor, pivotPointX,
                    pivotPointY);
            // canvas.setMatrix(matrix);

            canvas.drawBitmap(
                    ((BitmapDrawable) this.getDrawable()).getBitmap(), matrix,
                    null);

            // this.getDrawable().draw(canvas);
            canvas.restore();
        }
    }

    */
/*
     * (non-Javadoc)
     *
     * @see
     * android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable
     * )
     *//*

    @Override
    public void setImageDrawable(Drawable drawable) {
        // Constrain to given size but keep aspect ratio
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        mLastTouchX = mPosX = 0;
        mLastTouchY = mPosY = 0;

        int borderWidth = (int) borderPaint.getStrokeWidth();
        mScaleFactor = Math.min(((float) getLayoutParams().width - borderWidth)
                / width, ((float) getLayoutParams().height - borderWidth)
                / height);
        pivotPointX = (((float) getLayoutParams().width - borderWidth) - (int) (width * mScaleFactor)) / 2;
        pivotPointY = (((float) getLayoutParams().height - borderWidth) - (int) (height * mScaleFactor)) / 2;
        super.setImageDrawable(drawable);
    }

    float pivotPointX = 0f;
    float pivotPointY = 0f;

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            pivotPointX = detector.getFocusX();
            pivotPointY = detector.getFocusY();

            Log.d(LOG_TAG, "mScaleFactor " + mScaleFactor);
            Log.d(LOG_TAG, "pivotPointY " + pivotPointY + ", pivotPointX= "
                    + pivotPointX);
            mScaleFactor = Math.max(0.05f, mScaleFactor);

            invalidate();
            return true;
        }
    }*/
