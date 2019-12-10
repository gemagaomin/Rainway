package com.soft.railway.inspection.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import com.soft.railway.inspection.R;
import java.util.Timer;
import java.util.TimerTask;

public class CircleProgressView extends View {
    public int mStrokeWidth;
    public int mRadius;
    public int mBigRadius;
    public Paint mBgPaint;
    public Paint mBigBgPaint;
    public int mWidth;
    public int mHeight;
    public Paint mRecordPaint;
    public RectF mRectF;
    public boolean shortPress;
    private static final int LONGPRESSTIME= 500;
    private Timer timer;
    private TimerTask timerTask;
    //记录上次点击的位置，用来进行移动的模糊处理
    int lastX=0;
    int lastY=0;
    //此处可以视为将View划分为10行10列的方格，在方格内移动看作没有移动。
    private static final int MOHUFANWEI=10;
    public int mMaxValue=100;
    public int mMaxTime=11;
    public int mProgressValue;
    public int mArcValue;
    private boolean mIsStartRecord=false;
    public long mRecordTime;
    public CircleListener mListener;
    public Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ++mProgressValue;
            postInvalidate();
            if(mProgressValue<mMaxValue){
                mHandler.sendEmptyMessageDelayed(0,100);
            }else{
                int actualRecordTime=(int)((System.currentTimeMillis()-mRecordTime)/1000);
                mListener.stop(actualRecordTime);
            }
        }
    };


    public CircleListener getmListener() {
        return mListener;
    }

    public void setmListener(CircleListener mListener) {
        this.mListener = mListener;
    }

    public CircleProgressView(Context context) {
        this(context, null);

    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mHeight=getHeight();
        mWidth=getWidth();
        if(mHeight!=mWidth){
            int min= Math.min(mHeight,mWidth);
            mHeight=min;
            mWidth=min;
        }
        if(mIsStartRecord){
            canvas.drawCircle((mWidth)/2,(mHeight)/2,(float) (mBigRadius*1.2),mBigBgPaint);
            canvas.drawCircle(mWidth/2,mHeight/2,(float)(mRadius*0.5),mBgPaint);
            if(mProgressValue<=mMaxValue){
                mRectF.left=mArcValue;
                mRectF.top=mArcValue;
                mRectF.bottom=mHeight-mArcValue;
                mRectF.right=mWidth-mArcValue;
                canvas.drawArc(mRectF,-90,((float)mProgressValue / mMaxValue) * 360,false,mRecordPaint);
                if (mProgressValue == mMaxValue) {
                    mProgressValue = 0;
                    mHandler.removeMessages(0);
                    mIsStartRecord = false;
                    mListener.stop(mMaxValue);
                }
            }
        }else{
            canvas.drawCircle((mWidth)/2,(mHeight)/2,mBigRadius,mBigBgPaint);
            canvas.drawCircle(mWidth/2,mHeight/2,mRadius,mBgPaint);
        }
    }

    public int getmMaxTime() {
        return mMaxTime;
    }

    public void setmMaxTime(int mMaxTime) {
        this.mMaxTime = mMaxTime+1;
    }

    private void initPaint(Context context){
        mArcValue=mStrokeWidth=px2dip(context,6);
        mMaxValue=mMaxTime*1000/100;
        mBigBgPaint=new Paint();
        mBigBgPaint.setColor(context.getResources().getColor(R.color.circleOut));
        mBigBgPaint.setStrokeWidth(mStrokeWidth);
        mBigBgPaint.setAntiAlias(true);
        mBigBgPaint.setStyle(Paint.Style.FILL);

        mBgPaint=new Paint();
        mBgPaint.setColor(context.getResources().getColor(R.color.circleInner));
        mBgPaint.setStrokeWidth(mStrokeWidth);
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStyle(Paint.Style.FILL);

        mRecordPaint=new Paint();
        mRecordPaint.setColor(context.getResources().getColor(R.color.circleProgress));
        mRecordPaint.setAntiAlias(false);
        mRecordPaint.setStrokeWidth(mStrokeWidth);
        mRecordPaint.setStyle(Paint.Style.STROKE);

        mRadius=px2dip(context,24);
        mBigRadius=px2dip(context,40);
        mRectF=new RectF();
    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public static int px2dip(Context context, int pxValue) {
        return ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxValue, context.getResources().getDisplayMetrics()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //实现获取点击位置
        float X = event.getX();
        float Y = event.getY();
        //手指移动的模糊范围，手指移动超出该范围则取消事件处理
        int length=getWidth()/MOHUFANWEI;
        final int indexX=(int)(Y/length);
        final int indexY=(int)(X/length);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(event.getPointerCount() == 1){
                    timer=new Timer();
                    timerTask=new TimerTask() {
                        @Override
                        public void run() {
                            //长按逻辑触发，isClick置为false，手指移开后，不触发点击事件
                            shortPress=false;
                            mIsStartRecord=true;
                            mRecordTime= System.currentTimeMillis();
                            mHandler.sendEmptyMessage(0);
                            mListener.longOpen();
                        }
                     };
                    shortPress=true;
                    timer.schedule(timerTask,LONGPRESSTIME);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(event.getPointerCount() == 1){
                    if(shortPress){
                        mListener.open();
                    }else{
                        int actualRecordTime=0;
                        if(mRecordTime>0){
                            actualRecordTime=(int)((System.currentTimeMillis()-mRecordTime)/1000);
                        }
                        mListener.stop(actualRecordTime);
                        mHandler.removeMessages(0);
                        mProgressValue=0;
                        mRecordTime=0;
                        mIsStartRecord=false;
                        postInvalidate();
                    }
                    timerTask.cancel();
                    timer.cancel();
                }
                break;
                case MotionEvent.ACTION_MOVE:
                    //如果在一定范围内移动，不处理移动事件
                    if(lastX==indexX&&lastY==indexY)
                    {
                        return true;
                    }
                    shortPress=false;
                    timerTask.cancel();
                    timer.cancel();
                    break;
        }
        lastY=indexY;
        lastX=indexX;
        return true;
    }

    public interface CircleListener{
        void open();
        void longOpen();
        void stop(int actualRecordTime);
    }
}
