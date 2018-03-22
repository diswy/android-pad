package gorden.widget.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import gorden.lib.R;

/**
 * 加载动画VIEW
 * Created by gorden on 2016/5/13.
 */
public class LoadingView extends View{
    private static final float RADIU_DEFAULT=10;
    private int[] alpha=new int[]{225,255,255,255,255,255,255,255};
    private int state[]=new int[]{1,1,0,0,-1,-1,-1,-1};
    private float radiu=10;
    private boolean isRunning=false;
    private boolean autoRunning=false;
    private boolean error=false;
    private int duration=1000;
    private Paint paint;
    private int viewWidth,viewHeight;

    private float mProgress=1.0f;


    private int[] colors=new int[]{Color.parseColor("#26adfb"),Color.parseColor("#f16262"),Color.parseColor("#edac64"),Color.parseColor("#f9d981"),
            Color.parseColor("#a1e962"),Color.parseColor("#87dcb6"),Color.parseColor("#7dbfe9"),Color.parseColor("#9b6ae2"),Color.parseColor("#e37bae")};
    private int errorColor=Color.parseColor("#aeaeae");

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
        radiu=array.getDimension(R.styleable.LoadingView_dot_radiu,RADIU_DEFAULT);
        duration=array.getInt(R.styleable.LoadingView_duration,1000);
        autoRunning=array.getBoolean(R.styleable.LoadingView_autoRunning,false);
        error=array.getBoolean(R.styleable.LoadingView_error,false);
        array.recycle();
        if(autoRunning&&!isInEditMode())
            start();
        if(error){
            colors=new int[]{Color.parseColor("#26adfb"),errorColor,errorColor,errorColor,errorColor,errorColor,errorColor,errorColor,errorColor};
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth=getMeasuredSize(widthMeasureSpec,true);
        viewHeight=getMeasuredSize(heightMeasureSpec,false);

        setMeasuredDimension(viewWidth,viewHeight);
    }

    private int getMeasuredSize(int length, boolean isWidth){
        int specMode = MeasureSpec.getMode(length);
        int specSize = MeasureSpec.getSize(length);
        // 计算所得的实际尺寸，要被返回
        int retSize = 0;
        // 对不同的指定模式进行判断
        if(specMode==MeasureSpec.EXACTLY){
            retSize = specSize;
            if(retSize<8*radiu){
                retSize = (int) (8*radiu);
            }
        }else{                              // 如使用wrap_content
            retSize = (int) (8*radiu);
            if(specMode==MeasureSpec.UNSPECIFIED){
                retSize = Math.min(retSize, specSize);
            }
        }
        return retSize;
    }

    public void setDotRadius(float radiu){
        this.radiu=radiu;
        invalidate();
    }
    public void setDuration(int duration){
        this.duration=duration;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        List<PointF> pointfList=calculatePoints();
        for(int i=0;i<pointfList.size();i++){
            paint.setColor(colors[i+1]);
            paint.setAlpha(alpha[i]);
            canvas.drawCircle(pointfList.get(i).x,pointfList.get(i).y,radiu,paint);
        }
    }

    private List<PointF> calculatePoints(){
        float angle=45f;
        float radio=(Math.min(viewHeight,viewWidth)/2-radiu)*mProgress;
        float circleX=viewWidth/2;
        float circleY=viewHeight/2;
        List<PointF> points=new ArrayList<PointF>();
        for(int i=0;i<8;i++){
            float tmpAngle=angle*i;
            float tmpX= (float) (Math.sin(tmpAngle*Math.PI/180)*radio);
            float tmpY= (float) (Math.cos(tmpAngle*Math.PI/180)*radio);
            points.add(new PointF(tmpX+circleX,-tmpY+circleY));
        }
        return points;
    }

    /**
     * 开启动画
     */
    public void start(){
        if(isRunning){
            return;
        }
        alpha=new int[]{225,15,15,15,105,135,165,195};
        state=new int[]{1,1,0,0,-1,-1,-1,-1};
        isRunning=true;
        post(new Runnable() {
            @Override
            public void run() {
                if(!isRunning) return;
                for(int i=0;i<alpha.length;i++){
                    if(state[i]==-1){
                        alpha[i]-=30;
                        if(alpha[i]<=15){
                            state[i]=0;
                            alpha[i]=15;
                        }
                    }else if(state[i]==1){
                        alpha[i]+=80;
                        if(alpha[i]>=255){
                            state[i]=0;
                            alpha[i]=255;
                        }
                    } else if(state[i]==0){
                        int last=i-1>=0?i-1:7;
                        int next=i+1<=7?i+1:0;
                        if(alpha[i]==15&&alpha[last]>=255){
                            state[i]=1;
                        }
                        if(alpha[i]==255&&alpha[next]>=255){
                            state[i]=-1;
                        }
                    }
                }
                invalidate();
                postDelayed(this,duration/24);
            }
        });
    }

    /**
     * 停止动画
     */
    public void stop(){
        isRunning=false;
    }
    public boolean isStart(){
        return isRunning;
    }

    /**
     * 更具进度调整大小
     * @param progress
     */
    public void setProgress(float progress){
        mProgress=progress;
        invalidate();
    }

    /**
     * 重置
     */
    public void reset(){
        alpha=new int[]{225,255,255,255,255,255,255,255};
        invalidate();
    }
}
