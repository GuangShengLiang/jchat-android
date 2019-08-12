package jiguang.chat.view.my;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import jiguang.chat.R;

public class StickyNavLayout extends LinearLayout {
    private static final String TAG = "StickyNavLayout";
    //Banner条
    private View mTop;
    //导航的Indicator
    private View mNav;
    //Banner条的高度
    private int mTopViewHeight;
    //    private ViewGroup mInnerScrollView;
    //判断Banner条是否隐藏的标志位
    private boolean isTopHidden = false;
    private OverScroller mScroller;
    //显示内容的列表组件
    private RecyclerView mRecycleView;
    //和滑动相关的参数
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMaximumVelocity, mMinimumVelocity;
    private float mLastY;
    private boolean mDragging;
    //Indicator是否置顶的标志位
    private boolean isStickNav;
    private boolean isInControl = false;
    private int stickOffset;
    //内容组件的宽度和高度
    private int mViewPagerMaxHeight;
    private int mTopViewMaxHeight;
    private boolean isScroll = true;

    public StickyNavLayout(Context context) {
        this(context, null);
    }

    public StickyNavLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyNavLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
        //取出xml文件中设置的参数
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.StickNavLayout);
        isStickNav = a.getBoolean(R.styleable.StickNavLayout_isStickNav, false);
        stickOffset = a.getDimensionPixelSize(R.styleable.StickNavLayout_stickOffset, 0);
        a.recycle();
        //初始化滑动相关的数据
        mScroller = new OverScroller(context);
        mVelocityTracker = VelocityTracker.obtain();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();
    }

    public void setIsStickNav(boolean isStickNav) {
        this.isStickNav = isStickNav;
    }

    /**
     * 设置悬浮,并自动滚动到悬浮位置(即把top区域滚动上去)
     */
    public void setStickNavAndScrollToNav() {
        this.isStickNav = true;
        scrollTo(0, mTopViewHeight);
    }

    /****
     * 设置顶部区域的高度
     *
     * @param height height
     */
    public void setTopViewHeight(int height) {
        mTopViewHeight = height;
        if (isStickNav)
            scrollTo(0, mTopViewHeight);
    }

    /****
     * 设置顶部区域的高度
     *
     * @param height height
     * @param offset offset
     */
    public void setTopViewHeight(int height, int offset) {
        mTopViewHeight = height;
        if (isStickNav)
            scrollTo(0, mTopViewHeight - offset);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //在布局加载完成的时候初始化各个View
        mTop = findViewById(R.id.header);
        mNav = findViewById(R.id.snlIindicator);
        mRecycleView = (RecyclerView) findViewById(R.id.rv_content);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams params = mRecycleView.getLayoutParams();
//        //修复键盘弹出后键盘关闭布局高度不对问题
        int height = getMeasuredHeight() - mNav.getMeasuredHeight() - 20;
        mViewPagerMaxHeight = (height >= mViewPagerMaxHeight ? height : mViewPagerMaxHeight);
        params.height = /*mViewPagerMaxHeight - stickOffset*/height;
        mRecycleView.setLayoutParams(params);

        //修复键盘弹出后Top高度不对问题
        int topHeight = mTop.getMeasuredHeight();
        ViewGroup.LayoutParams topParams = mTop.getLayoutParams();

        mTopViewMaxHeight = (topHeight >= mTopViewMaxHeight ? topHeight : mTopViewMaxHeight);
        topParams.height = /*mTopViewMaxHeight*/topHeight;
        mTop.setLayoutParams(topParams);

        //设置mTopViewHeight
        mTopViewHeight = topParams.height;
    }

    /**
     * 更新top区域的视图,如果是处于悬浮状态,隐藏top区域的控件是不起作用的!!
     */
    public void updateTopViews() {
        if (isTopHidden) {
            return;
        }
        final ViewGroup.LayoutParams params = mTop.getLayoutParams();
        mTop.post(new Runnable() {
            @Override
            public void run() {
                if (mTop instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) mTop;
                    int height = viewGroup.getChildAt(0).getHeight();
                    mTopViewHeight = height - stickOffset;
                    params.height = height;
                    mTop.setLayoutParams(params);
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                } else {
                    mTopViewHeight = mTop.getMeasuredHeight() - stickOffset;
                }
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //在尺寸发生变化的时候重新初始化数据
        final ViewGroup.LayoutParams params = mTop.getLayoutParams();
        Log.d(TAG, "onSizeChanged-mTopViewHeight:" + mTopViewHeight);
        mTop.post(new Runnable() {
            @Override
            public void run() {
                if (mTop instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) mTop;
                    int height = viewGroup.getChildAt(0).getHeight();
                    mTopViewHeight = height - stickOffset;
                    params.height = height;
                    mTop.setLayoutParams(params);
                    mTop.requestLayout();
                } else {
                    mTopViewHeight = mTop.getMeasuredHeight() - stickOffset;
                }
            }
        });
    }

    /*
     *接下来是三个重要的方法，主要是对滑动事件的处理，避免冲突
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;

                //header隐藏并且向上滑动
                if (!isInControl && android.support.v4.view.ViewCompat.canScrollVertically(mRecycleView, -1) && isTopHidden
                        && dy > 0) {
                    isInControl = true;
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    MotionEvent ev2 = MotionEvent.obtain(ev);
                    dispatchTouchEvent(ev);
                    ev2.setAction(MotionEvent.ACTION_DOWN);
                    isSticky = true;
                    return dispatchTouchEvent(ev2);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP://处理悬停后立刻抬起的处理
                float distance = y - mLastY;
                if (isSticky && /*distance==0.0f*/Math.abs(distance) <= mTouchSlop) {
                    isSticky = false;
                    return true;
                } else {
                    isSticky = false;
                    return super.dispatchTouchEvent(ev);
                }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isSticky;//mNav-view 是否悬停的标志

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                if (Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                    //header没有隐藏或者header隐藏并且向下滑动，拦截滑动事件，并且调用接下来的onTouc方法处理接下来的事件
                    if (!isTopHidden || (!android.support.v4.view.ViewCompat.canScrollVertically(mRecycleView, -1) && isTopHidden && dy > 0)) {
                        initVelocityTrackerIfNotExists();
                        mVelocityTracker.addMovement(ev);
                        mLastY = y;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mDragging = false;
                recycleVelocityTracker();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished())
                    mScroller.abortAnimation();
                mLastY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (isScroll) {
                    float dy = y - mLastY;

                    if (!mDragging && Math.abs(dy) > mTouchSlop) {
                        mDragging = true;
                    }
                    if (mDragging) {
                        //在这里才是真正的滑动，这个方法又会调用接下来的scrollTo方法实现滑动
                        scrollBy(0, (int) -dy);
                        //如果topView隐藏，且上滑动时，则改变当前事件为ACTION_DOWN
                        if (getScrollY() == mTopViewHeight && dy < 0) {
                            event.setAction(MotionEvent.ACTION_DOWN);
                            dispatchTouchEvent(event);
                            isInControl = false;
                            return true;
                        } else {
                            isSticky = false;
                        }
                    }
                    mLastY = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                recycleVelocityTracker();
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    fling(-velocityY);
                }
                //up事件的时候回收资源
                recycleVelocityTracker();
                break;
        }
        return super.onTouchEvent(event);
    }

    public void fling(int velocityY) {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
        invalidate();
    }

    //scrollTo方法很重要，主要做了三件事
    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        //1.实现ViewGroup的滑动
        //2.处理滑动误差，并且根据滑动距离，初始化isTopHidden参数
        if (y > mTopViewHeight) {
            y = mTopViewHeight;
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }

        isTopHidden = getScrollY() == mTopViewHeight;


        //3.set  listener 设置悬浮监听回调，通过回调处理Indicator根据滑动位置的颜色渐变和SwipeRefreshLayout对滑动事件的拦截
        if (listener != null) {
//            if(lastIsTopHidden!=isTopHidden){
//                lastIsTopHidden=isTopHidden;
            listener.isStick(isTopHidden);
//            }
            listener.scrollPercent((float) getScrollY() / (float) mTopViewHeight);
        }
    }
//    private  boolean lastIsTopHidden;//记录上次是否悬浮

    //实现滑动
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    //回收资源
    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private OnStickStateChangeListener listener;

    /**
     * 悬浮状态回调
     */
    public interface OnStickStateChangeListener {
        /**
         * 是否悬浮的回调
         *
         * @param isStick true 悬浮 ,false 没有悬浮
         */
        void isStick(boolean isStick);

        /**
         * 距离悬浮的距离的百分比
         *
         * @param percent 0~1(向上) or 1~0(向下) 的浮点数
         */
        void scrollPercent(float percent);
    }

    public void setOnStickStateChangeListener(OnStickStateChangeListener listener) {
        this.listener = listener;
    }

    public boolean isScroll() {
        return isScroll;
    }

    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }
}
