package myapplication.nomasp.com.custommenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * Created by nomasp on 2015/09/24.
 */

public class MainUI extends RelativeLayout
{
    private Context context;  // 上下文
    private FrameLayout leftMenu;    // 左边部分
    private FrameLayout middleMenu;  // 中间部分
    private FrameLayout rightMenu;   // 右边部分
    private FrameLayout middleMask;  // 蒙版效果
    private Scroller mScroller;    // 滑动动画
    public static final int ID = 0;    // ID
    public static final int LEFT_ID = ID+0xaabbcc;
    public static final int MIDDLE_ID = ID+0xaaccbb;
    public static final int RIGHT_ID = ID+0xccbbaa;

    public MainUI(Context context) {
        super(context);
        initView(context);
    }

    public MainUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    // 初始化视图
    private void initView(Context context){
        this.context=context;
        mScroller = new Scroller(context,new DecelerateInterpolator());
        leftMenu=new FrameLayout(context);
        middleMenu = new FrameLayout(context);
        rightMenu = new FrameLayout(context);
        middleMask = new FrameLayout(context);
        leftMenu.setBackgroundColor(Color.RED);  // 设置背景颜色
        middleMenu.setBackgroundColor(Color.GREEN);
        rightMenu.setBackgroundColor(Color.RED);
        middleMask.setBackgroundColor(0x88000000);
        leftMenu.setId(LEFT_ID);
        middleMenu.setId(MIDDLE_ID);
        rightMenu.setId(RIGHT_ID);
        addView(leftMenu);   // 添加至View
        addView(middleMenu);
        addView(rightMenu);
        addView(middleMask);
        middleMask.setAlpha(0);  // 设置middleMask的透明度
    }

    public float onMiddleMask(){
        System.out.println("透明度"+middleMask.getAlpha());
        return middleMask.getAlpha();
    }

    @Override
    public void scrollTo(int x, int y){
        super.scrollTo(x,y);
        onMiddleMask();  // 输出透明度
        int curX = Math.abs(getScrollX());
        float scale = curX/(float)leftMenu.getMeasuredWidth();  // 设置透明度的渐变
        middleMask.setAlpha(scale);
    }

    @Override
    protected void onMeasure(int widthMeasureSepc, int heightMeasureSpec){
        super.onMeasure(widthMeasureSepc, heightMeasureSpec);
        middleMenu.measure(widthMeasureSepc, heightMeasureSpec);
        middleMask.measure(widthMeasureSepc,heightMeasureSpec);
        int realWidth = MeasureSpec.getSize(widthMeasureSepc);  // 获取实际（屏幕）宽度
        int tempWidthMeasure = MeasureSpec.makeMeasureSpec(
                (int)(realWidth*0.7f), MeasureSpec.EXACTLY);  // 左右侧的宽度为中间宽度的0.7
        leftMenu.measure(tempWidthMeasure, heightMeasureSpec);  // 左右侧的高度和中间的一样
        rightMenu.measure(tempWidthMeasure, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r,int b){  // l,t,r,b分别为中间部分的左、上、右、下边界
        super.onLayout(changed, l, t, r, b);  // 设置布局
        middleMenu.layout(l, t, r, b);   // 中间部分的四个边界不变
        middleMask.layout(l, t, r, b);  // 蒙版的四个边界和中间部分一样
        leftMenu.layout(l - leftMenu.getMeasuredWidth(), t, r, b); // 左侧部分的左边边界等于中间部分的左边边界减去左侧部分的宽度
        rightMenu.layout(l + middleMenu.getMeasuredWidth(), t,  // 右侧部分的左边边界则等于中间部分的左边边界加上中间部分的宽度
                l + middleMenu.getMeasuredWidth() +   // 右侧部分的右边边界等于中间部分的左边边界加上中间部分的宽度加上右侧部分的宽度
                        rightMenu.getMeasuredWidth(), b);
    }

    private boolean isTestComete;  // 测试是否完成
    private boolean isleftrightevent;  // 判断是否是左右滑动

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if(!isTestComete){  // 没有完成滑动动作
            getEventType(ev);   // 继续调用事件进行判断
            return true;
        }
        if(isleftrightevent){   // 左右滑动
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                    int curScrollX = getScrollX();
                    int dis_x = (int) (ev.getX() - point.x);
                    int expectX = -dis_x + curScrollX;
                    int finalX = 0;
                    if (expectX < 0) {
                        finalX = Math.max(expectX, -leftMenu.getMeasuredWidth());
                    } else {
                        finalX = Math.min(expectX, rightMenu.getMeasuredWidth());
                    }
                    scrollTo(finalX, 0);
                    point.x = (int) ev.getX();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    curScrollX = getScrollX();
                    if (Math.abs(curScrollX) > leftMenu.getMeasuredWidth() >> 1) {
                        if (curScrollX < 0) {
                            mScroller.startScroll(curScrollX, 0, -leftMenu.getMeasuredWidth() - curScrollX, 0, 200);
                        } else {
                            mScroller.startScroll(curScrollX, 0, leftMenu.getMeasuredWidth() - curScrollX, 0, 200);
                        }
                    } else {
                        mScroller.startScroll(curScrollX, 0, -curScrollX, 0, 200);
                    }
                    invalidate();
                    isleftrightevent = false;
                    isTestComete = false;
                    break;
            }
        }else{
            switch (ev.getActionMasked()){
                case MotionEvent.ACTION_UP:
                    isleftrightevent = false;
                    isTestComete = false;
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void computeScroll(){
        super.computeScroll();
        if(!mScroller.computeScrollOffset()){
            return;
        }
        int tempX = mScroller.getCurrX();
        scrollTo(tempX, 0);
    }

    private Point point = new Point();
    private static final int TEST_DIS = 20;

    private void getEventType(MotionEvent ev){
        switch (ev.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                point.x = (int)ev.getX();
                point.y = (int) ev.getY();
                super.dispatchTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                int dX = Math.abs((int)ev.getX() - point.x);
                int dY = Math.abs((int)ev.getY() - point.y);
                if(dX >= TEST_DIS && dX>dY ){ // 左右滑动
                    isleftrightevent = true;
                    isTestComete = true;
                    point.x = (int)ev.getX();
                    point.y = (int)ev.getY();
                }else if(dY>=TEST_DIS && dY>dX ){   // 上下滑动
                    isleftrightevent = false;
                    isTestComete = true;
                    point.x = (int)ev.getX();
                    point.y = (int)ev.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                super.dispatchTouchEvent(ev);
                isleftrightevent = false;
                isTestComete = false;
                break;
        }
    }
}
