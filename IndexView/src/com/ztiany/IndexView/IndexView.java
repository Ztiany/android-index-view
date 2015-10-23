package com.ztiany.IndexView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.SectionIndexer;


public class IndexView extends View {
    public IndexView(Context context) {
        this(context, null);
    }

    public IndexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private String[] mSections;//所有索引
    private int mTextSize = 12;//默认字体大小
    private int mHPadding = 10;//水平内边距
    private int mVPadding = 10;//锤子边距
    private int mWidth;//宽度
    private int mHeight;//高度

    private int mTextRectHeight;//单个文字区域高度


    private int mRound = 5;//弧度

    private TextPaint mTextPaint;//索引文字画笔
    private Paint mRectPaint;//灰色区域画笔
    private Paint mPopPaint;//气泡画笔
    private TextPaint mPopTextPaint;//汽包文字画笔

    private SectionIndexer mIndexer;

    private int mTextWidth;//文字宽度

    public static final byte STATE_SHOWING = 1;
    public static final byte STATE_HIDE = 2;
    public static final byte STATE_NORMAL = 3;
    private byte STATE = STATE_NORMAL;

    private int mTouchIndex = -1 ;//触摸的位置所在角标
    private boolean drawPop;//是否画
    private int mTouchY =-1;//y的位置
    private int mCircleRightMargin = 20 ;//圆圈右边距离
    private float mCircleRadius = 40;//圆圈半径
    private float mDynamicCircleRadius ;//动态圆圈班级
    private int mAnimTime = 300;//动画时间

    private float scaledDensity;

    private Path mPath;



    private void init() {

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP , mTextSize , getResources().getDisplayMetrics()));
        mTextPaint.setColor(Color.BLACK);
        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setColor(Color.GRAY);
        mPopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPopPaint.setColor(Color.BLUE);

        mPopTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPopTextPaint.setColor(Color.GREEN);
        mPath = new Path();
        scaledDensity =  getContext().getResources().getDisplayMetrics().scaledDensity;
    }

    public void setAdapter(Adapter adapter) {
        if (adapter instanceof SectionIndexer) {
            mIndexer = (SectionIndexer) adapter;
            mSections = (String[]) mIndexer.getSections();
            calc();
            requestLayout();
            invalidate();
        }
    }

    private void calc() {
        for(String s: mSections) {
            mTextWidth = (int) Math.max(mTextWidth, mTextPaint.measureText(s));
        }

        mHPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHPadding, getResources().getDisplayMetrics());
        mVPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mVPadding, getResources().getDisplayMetrics());
        mRound = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mRound, getResources().getDisplayMetrics());
        mCircleRightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mCircleRightMargin, getResources().getDisplayMetrics());
        mCircleRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mCircleRadius, getResources().getDisplayMetrics());
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;mHeight=h;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthResult;
        if (widthMode == MeasureSpec.EXACTLY) {
            widthResult = widthSize;
        }
        else{

            widthResult = (int) (mCircleRightMargin + mCircleRadius*2  + mTextWidth + 2 * mHPadding);
            if (widthMode == MeasureSpec.AT_MOST) {
                widthResult = Math.min(widthSize, widthResult);
            }
        }
        setMeasuredDimension(widthResult, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mSections == null || mSections.length ==0){
            return;
        }
        String[] sections = mSections;
        int length = mSections.length;

        int drawRectWidth = mTextWidth + 2* mHPadding;
        int left = mWidth - drawRectWidth;


        //画背景
        if(drawPop ) {
            canvas.drawRoundRect(new RectF(left, 0, mWidth, mHeight), mRound, mRound, mRectPaint);
        }
        mTextRectHeight = (mHeight - 2*mVPadding )/ length;

        canvas.save();
        canvas.translate(0 , mVPadding);
        float baseY = mTextRectHeight/2 +  ((Math.abs(mTextPaint.ascent()-Math.abs(mTextPaint.descent()))) / 2);


        for (int i = 0 ; i < length ; i ++){
            int startX =   left +     (int) (drawRectWidth/2 - mTextPaint.measureText(sections[i])/2);
            canvas.drawText(sections[i], startX, baseY, mTextPaint);
            canvas.translate(0 , mTextRectHeight);
        }
        canvas.restore();

        if(drawPop || STATE == STATE_HIDE ) {


            Log.e(getClass().getSimpleName(), "mTouchY = " + mTouchY);
//            float touchY = (mTouchY < 0) ? 0 : ((mTouchY > mHeight) ? mHeight : mTouchY) - mDynamicCircleRadius;
            float circleX = (mCircleRadius*2)-mDynamicCircleRadius;
//            touchY = touchY < (mCircleRadius / 2) ? (mCircleRadius / 2) : (touchY > (mHeight - mCircleRadius / 2) ? (mHeight - mCircleRadius / 2) : touchY) ;
            float touchY = mTouchY - mDynamicCircleRadius;
            if(touchY < mDynamicCircleRadius){
                touchY =  mDynamicCircleRadius;
            }else if(touchY > mHeight - mDynamicCircleRadius) {
                touchY = mHeight-mDynamicCircleRadius;
            }


            //画圆圈
            canvas.drawCircle(circleX , touchY, mDynamicCircleRadius, mPopPaint);


            //画曲线
            float arcX = mCircleRadius*2 + mCircleRightMargin;
            mPath.reset();
            mPath.moveTo(circleX - mDynamicCircleRadius  , touchY);
            mPath.quadTo(circleX ,touchY+mDynamicCircleRadius*2 , arcX ,  touchY+mDynamicCircleRadius );
            mPath.quadTo(circleX ,touchY+mDynamicCircleRadius*2 , circleX , touchY);
            canvas.drawPath(mPath , mPopPaint);


            //画文字
            mPopTextPaint.setTextSize(mDynamicCircleRadius*2/scaledDensity);
            float textStartX = circleX - mPopTextPaint.measureText(mSections[mTouchIndex]  )/2;
            float textBaseY = touchY +  (     Math.abs(mPopTextPaint.ascent()) - Math.abs(mPopTextPaint.descent())  )/ 2;
            canvas.drawText(mSections[mTouchIndex] , textStartX , textBaseY ,  mPopTextPaint);



        }

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        mTouchY = y;
        if(!drawPop){
            if( x < mCircleRadius*2 + mCircleRightMargin){
                return false;
            }
                drawPop = true;
        }



        mTouchIndex = getTouchIndexFromEvent(event);
        Log.d(this.getClass().getSimpleName() , "index = "+ mTouchIndex);
        Log.d(this.getClass().getSimpleName() , "drawPop = "+ drawPop);


        switch (action) {
            case MotionEvent.ACTION_DOWN:
                STATE = STATE_SHOWING;
                Log.d(this.getClass().getSimpleName(), "ACTION_DOWN");
                          startShow();
                break;
            case MotionEvent.ACTION_MOVE:
                if(STATE == STATE_NORMAL){
                    Log.d(this.getClass().getSimpleName(), "MOVE Call Invalidate");
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                drawPop = false;
                STATE = STATE_HIDE;
                Log.d(this.getClass().getSimpleName(), "ACTION_UP");
                startHide();
                break;
        }

        return true;
    }



    private void startHide() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat( mDynamicCircleRadius,0F);
        valueAnimator.setDuration(mAnimTime).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                if(STATE == STATE_HIDE){
                    if(mDynamicCircleRadius <= 1){
                        valueAnimator.cancel();
                        STATE = STATE_NORMAL;
                        invalidate();
                    }else {
                        mDynamicCircleRadius = (Float)valueAnimator.getAnimatedValue();
                        invalidate();
                    }
                }else {
                    valueAnimator.cancel();
                }

            }
        });
        if(STATE == STATE_HIDE)
        valueAnimator.start();
    }

    private void startShow() {

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mDynamicCircleRadius,mCircleRadius );
        valueAnimator.setDuration(mAnimTime).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                Log.d(getClass().getSimpleName() , "mDynamicCircleRadius = " +mDynamicCircleRadius + "  "+(mDynamicCircleRadius == mCircleRadius));

                if(STATE == STATE_SHOWING){
                    if(mDynamicCircleRadius >= mCircleRadius-1){
                        STATE = STATE_NORMAL;
                        valueAnimator.cancel();
                        invalidate();
                    }else {
                        mDynamicCircleRadius = (Float)valueAnimator.getAnimatedValue();
                        invalidate();
                    }
                }else {
                    valueAnimator.cancel();
                }
            }
        });
        if(STATE == STATE_SHOWING){
            valueAnimator.start();
        }

    }

    private int getTouchIndexFromEvent(MotionEvent event) {
        int y = (int) event.getY();
        int index = 0;
        int tempHeight = mVPadding;
        while (index < mHeight){
            if(y < tempHeight + (  (index+1 )* mTextRectHeight)){
                return index>mSections.length-1?mSections.length-1:index;
            }
            index ++;
        }
        return -1;
    }
}
