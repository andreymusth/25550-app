package ru.tzkt.lifetime;

/**
 * Created by user on 25.04.2018.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.util.ArrayList;

/**
 * 滚轮视图，可设置是否循环模式，实现OnScrollChangedListener接口以监听滚轮变化
 * Created by huzn on 2016/10/27.
 */
public class EasyPickerView extends View {

    private int textSize;
    private int textColor;
    private int textPadding;
    private float textMaxScale;
    private float textMinAlpha;
    private boolean isRecycleMode;
    private int maxShowNum;

    private TextPaint textPaint;
    private Paint.FontMetrics fm;

    private Scroller scroller;
    private VelocityTracker velocityTracker;
    private int minimumVelocity;
    private int maximumVelocity;
    private int scaledTouchSlop;

    private ArrayList<String> dataList = new ArrayList<>();
    private int cx;
    private int cy;
    private float maxTextWidth;
    private int textHeight;
    private int contentWidth;
    private int contentHeight;
    private float downY;
    private float offsetY;
    private float oldOffsetY;
    private int curIndex;
    private int offsetIndex;

    private float bounceDistance;
    private boolean isSliding = false;

    public EasyPickerView(Context context) {
        this(context, null);
    }

    public EasyPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EasyPickerView, defStyleAttr, 0);
        textSize = a.getDimensionPixelSize(R.styleable.EasyPickerView_epvTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        textColor = a.getColor(R.styleable.EasyPickerView_epvTextColor, Color.BLACK);
        textPadding = a.getDimensionPixelSize(R.styleable.EasyPickerView_epvTextPadding, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
        textMaxScale = a.getFloat(R.styleable.EasyPickerView_epvTextMaxScale, 2.0f);
        textMinAlpha = a.getFloat(R.styleable.EasyPickerView_epvTextMinAlpha, 0.4f);
        isRecycleMode = a.getBoolean(R.styleable.EasyPickerView_epvRecycleMode, true);
        maxShowNum = a.getInteger(R.styleable.EasyPickerView_epvMaxShowNum, 3);
        a.recycle();

        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        fm = textPaint.getFontMetrics();
        textHeight = (int) (fm.bottom - fm.top);

        scroller = new Scroller(context);
        minimumVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        maximumVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        contentWidth = (int) (maxTextWidth * textMaxScale + getPaddingLeft() + getPaddingRight());
        if (mode != MeasureSpec.EXACTLY) { // wrap_content
            width = contentWidth;
        }

        mode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        contentHeight = textHeight * maxShowNum + textPadding * maxShowNum;
        if (mode != MeasureSpec.EXACTLY) { // wrap_content
            height = contentHeight + getPaddingTop() + getPaddingBottom();
        }

        cx = width / 2;
        cy = height / 2;

        setMeasuredDimension(width, height);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        addVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished()) {
                    scroller.forceFinished(true);
                    finishScroll();
                }
                downY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                offsetY = event.getY() - downY;
                if (isSliding || Math.abs(offsetY) > scaledTouchSlop) {
                    isSliding = true;
                    reDraw();
                }
                break;

            case MotionEvent.ACTION_UP:
                int scrollYVelocity = 2 * getScrollYVelocity() / 3;
                if (Math.abs(scrollYVelocity) > minimumVelocity) {
                    oldOffsetY = offsetY;
                    scroller.fling(0, 0, 0, scrollYVelocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                    invalidate();
                } else {
                    finishScroll();
                }

                // 没有滑动，则判断点击事件
                if (!isSliding) {
                    if (downY < contentHeight / 3)
                        moveBy(-1);
                    else if (downY > 2 * contentHeight / 3)
                        moveBy(1);
                }

                isSliding = false;
                recycleVelocityTracker();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null != dataList && dataList.size() > 0) {
            canvas.clipRect(
                    cx - contentWidth / 2,
                    cy - contentHeight / 2,
                    cx + contentWidth / 2,
                    cy + contentHeight / 2
            );

            // 绘制文字，从当前中间项往前、后一共绘制maxShowNum个字
            int size = dataList.size();
            int centerPadding = textHeight + textPadding;
            int half = maxShowNum / 2 + 1;
            for (int i = -half; i <= half; i++) {
                int index = curIndex - offsetIndex + i;

                if (isRecycleMode) {
                    if (index < 0)
                        index = (index + 1) % dataList.size() + dataList.size() - 1;
                    else if (index > dataList.size() - 1)
                        index = index % dataList.size();
                }

                if (index >= 0 && index < size) {

                    int tempY = cy + i * centerPadding;
                    tempY += offsetY % centerPadding;

                    // scale
                    float scale = 1.0f - (1.0f * Math.abs(tempY - cy) / centerPadding);

                    // textMaxScale，tempScale，text，textMaxScale
                    float tempScale = scale * (textMaxScale - 1.0f) + 1.0f;
                    tempScale = tempScale < 1.0f ? 1.0f : tempScale;

                    //alpha
                    float textAlpha = textMinAlpha;
                    if (textMaxScale != 1) {
                        float tempAlpha = (tempScale - 1) / (textMaxScale - 1);
                        textAlpha = (1 - textMinAlpha) * tempAlpha + textMinAlpha;
                    }

                    textPaint.setTextSize(textSize * tempScale);
                    textPaint.setAlpha((int) (255 * textAlpha));


                    Paint.FontMetrics tempFm = textPaint.getFontMetrics();
                    String text = dataList.get(index);
                    float textWidth = textPaint.measureText(text);
                    canvas.drawText(text, cx - textWidth / 2, tempY - (tempFm.ascent + tempFm.descent) / 2, textPaint);
                }
            }
        }
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            offsetY = oldOffsetY + scroller.getCurrY();

            if (!scroller.isFinished())
                reDraw();
            else
                finishScroll();
        }
    }

    private void addVelocityTracker(MotionEvent event) {
        if (velocityTracker == null)
            velocityTracker = VelocityTracker.obtain();

        velocityTracker.addMovement(event);
    }

    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    private int getScrollYVelocity() {
        velocityTracker.computeCurrentVelocity(1000, maximumVelocity);
        int velocity = (int) velocityTracker.getYVelocity();
        return velocity;
    }

    private void reDraw() {
        // curIndex
        int i = (int) (offsetY / (textHeight + textPadding));
        if (isRecycleMode || (curIndex - i >= 0 && curIndex - i < dataList.size())) {
            if (offsetIndex != i) {
                offsetIndex = i;

                if (null != onScrollChangedListener)
                    onScrollChangedListener.onScrollChanged(getNowIndex(-offsetIndex));
            }
            postInvalidate();
        } else {
            finishScroll();
        }
    }

    private void finishScroll() {

        int centerPadding = textHeight + textPadding;
        float v = offsetY % centerPadding;
        if (v > 0.5f * centerPadding)
            ++offsetIndex;
        else if (v < -0.5f * centerPadding)
            --offsetIndex;

        // curIndex
        curIndex = getNowIndex(-offsetIndex);

        //
        bounceDistance = offsetIndex * centerPadding - offsetY;
        offsetY += bounceDistance;

        if (null != onScrollChangedListener)
            onScrollChangedListener.onScrollFinished(curIndex);

        reset();
        postInvalidate();
    }

    private int getNowIndex(int offsetIndex) {
        int index = curIndex + offsetIndex;
        if (isRecycleMode) {
            if (index < 0)
                index = (index + 1) % dataList.size() + dataList.size() - 1;
            else if (index > dataList.size() - 1)
                index = index % dataList.size();
        } else {
            if (index < 0)
                index = 0;
            else if (index > dataList.size() - 1)
                index = dataList.size() - 1;
        }
        return index;
    }

    private void reset() {
        offsetY = 0;
        oldOffsetY = 0;
        offsetIndex = 0;
        bounceDistance = 0;
    }


    public void setDataList(ArrayList<String> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);

        // 更新maxTextWidth
        if (null != dataList && dataList.size() > 0) {
            int size = dataList.size();
            for (int i = 0; i < size; i++) {
                float tempWidth = textPaint.measureText(dataList.get(i));
                if (tempWidth > maxTextWidth)
                    maxTextWidth = tempWidth;
            }
            curIndex = 0;
        }
        requestLayout();
        invalidate();
    }


    public int getCurIndex() {
        return getNowIndex(-offsetIndex);
    }


    public void moveTo(int index) {
        if (index < 0 || index >= dataList.size() || curIndex == index)
            return;

        if (!scroller.isFinished())
            scroller.forceFinished(true);

        finishScroll();

        int dy = 0;
        int centerPadding = textHeight + textPadding;
        if (!isRecycleMode) {
            dy = (curIndex - index) * centerPadding;
        } else {
            int offsetIndex = curIndex - index;
            int d1 = Math.abs(offsetIndex) * centerPadding;
            int d2 = (dataList.size() - Math.abs(offsetIndex)) * centerPadding;

            if (offsetIndex > 0) {
                if (d1 < d2)
                    dy = d1; // ascent
                else
                    dy = -d2; // descent
            } else {
                if (d1 < d2)
                    dy = -d1; // descent
                else
                    dy = d2; // ascent
            }
        }
        scroller.startScroll(0, 0, 0, dy, 500);
        invalidate();
    }


    public void moveBy(int offsetIndex) {
        moveTo(getNowIndex(offsetIndex));
    }


    public interface OnScrollChangedListener {
        public void onScrollChanged(int curIndex);

        public void onScrollFinished(int curIndex);
    }

    private OnScrollChangedListener onScrollChangedListener;

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }
}
