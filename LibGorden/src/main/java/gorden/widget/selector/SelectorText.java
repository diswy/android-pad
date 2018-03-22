package gorden.widget.selector;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import gorden.lib.R;


/**
 * SelectorTextView
 * Created by gorden on 2016/8/4.
 */
public class SelectorText extends AppCompatTextView{
    private SelectorHolder holder;

    public SelectorText(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public SelectorText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public SelectorText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    //初始化
    private void initialize(Context context, AttributeSet attrs, int defStyleAttr){
        holder = new SelectorHolder();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectorText, defStyleAttr,0);

        holder.s_solid_color = a.getColor(R.styleable.SelectorText_s_solid_color, Color.TRANSPARENT);
        holder.s_solid_pressed_color = a.getColor(R.styleable.SelectorText_s_solid_pressed_color, Color.TRANSPARENT);
        holder.s_solid_checked_color = a.getColor(R.styleable.SelectorText_s_solid_checked_color, Color.TRANSPARENT);
        holder.s_solid_enable_color = a.getColor(R.styleable.SelectorText_s_solid_enable_color, Color.TRANSPARENT);

        holder.s_stroke_width = a.getDimension(R.styleable.SelectorText_s_stroke_width,0);
        holder.s_stroke_color = a.getColor(R.styleable.SelectorText_s_stroke_color, Color.TRANSPARENT);
        holder.s_stroke_pressed_color = a.getColor(R.styleable.SelectorText_s_stroke_pressed_color, Color.TRANSPARENT);
        holder.s_stroke_checked_color = a.getColor(R.styleable.SelectorText_s_stroke_checked_color, Color.TRANSPARENT);
        holder.s_stroke_enable_color = a.getColor(R.styleable.SelectorText_s_stroke_enable_color, Color.TRANSPARENT);

        holder.s_text_color = a.getColor(R.styleable.SelectorText_s_text_color, Color.TRANSPARENT);
        holder.s_text_pressed_color = a.getColor(R.styleable.SelectorText_s_text_pressed_color, Color.TRANSPARENT);
        holder.s_text_checked_color = a.getColor(R.styleable.SelectorText_s_text_checked_color, Color.TRANSPARENT);
        holder.s_text_enable_color = a.getColor(R.styleable.SelectorText_s_text_enable_color, Color.TRANSPARENT);

        holder.s_radius = a.getDimension(R.styleable.SelectorText_s_radius,0);
        holder.s_radius_array[0] = a.getDimension(R.styleable.SelectorText_s_tl_radius,holder.s_radius);
        holder.s_radius_array[1] = a.getDimension(R.styleable.SelectorText_s_tr_radius,holder.s_radius);
        holder.s_radius_array[2] = a.getDimension(R.styleable.SelectorText_s_br_radius,holder.s_radius);
        holder.s_radius_array[3] = a.getDimension(R.styleable.SelectorText_s_bl_radius,holder.s_radius);

        a.recycle();
        setup();
    }

    private void setup() {
        int stateCount = 0 ;
        StateListDrawable mStateListDrawable = new StateListDrawable();

        if(holder.s_solid_enable_color != Color.TRANSPARENT || (holder.s_stroke_enable_color!= Color.TRANSPARENT && holder.s_stroke_width>0)){
            stateCount++;
            GradientDrawable mEnabledDrawable = new GradientDrawable();
            mEnabledDrawable.setColor(holder.s_solid_enable_color);
            mEnabledDrawable.setStroke((int) holder.s_stroke_width,holder.s_stroke_enable_color);
            if(holder.radiusEqual()){
                mEnabledDrawable.setCornerRadius(holder.s_radius);
            }else{
                mEnabledDrawable.setCornerRadii(new float[]{holder.s_radius_array[0],holder.s_radius_array[0],holder.s_radius_array[1],holder.s_radius_array[1],
                        holder.s_radius_array[2],holder.s_radius_array[2],holder.s_radius_array[3],holder.s_radius_array[3]});
            }
            mStateListDrawable.addState(new int[]{-android.R.attr.state_enabled},mEnabledDrawable);
        }

        if(holder.s_solid_pressed_color != Color.TRANSPARENT || (holder.s_stroke_pressed_color!= Color.TRANSPARENT && holder.s_stroke_width>0)){
            stateCount++;
            GradientDrawable mPressedDrawable = new GradientDrawable();
            mPressedDrawable.setColor(holder.s_solid_pressed_color);
            mPressedDrawable.setStroke((int) holder.s_stroke_width,holder.s_stroke_pressed_color);
            if(holder.radiusEqual()){
                mPressedDrawable.setCornerRadius(holder.s_radius);
            }else{
                mPressedDrawable.setCornerRadii(new float[]{holder.s_radius_array[0],holder.s_radius_array[0],holder.s_radius_array[1],holder.s_radius_array[1],
                        holder.s_radius_array[2],holder.s_radius_array[2],holder.s_radius_array[3],holder.s_radius_array[3]});
            }
            mStateListDrawable.addState(new int[]{android.R.attr.state_enabled,android.R.attr.state_pressed},mPressedDrawable);
        }

        if(holder.s_solid_checked_color != Color.TRANSPARENT || (holder.s_stroke_checked_color!= Color.TRANSPARENT && holder.s_stroke_width>0)){
            stateCount++;
            GradientDrawable mCheckedDrawable = new GradientDrawable();
            mCheckedDrawable.setColor(holder.s_solid_checked_color);
            mCheckedDrawable.setStroke((int) holder.s_stroke_width,holder.s_stroke_checked_color);
            if(holder.radiusEqual()){
                mCheckedDrawable.setCornerRadius(holder.s_radius);
            }else{
                mCheckedDrawable.setCornerRadii(new float[]{holder.s_radius_array[0],holder.s_radius_array[0],holder.s_radius_array[1],holder.s_radius_array[1],
                        holder.s_radius_array[2],holder.s_radius_array[2],holder.s_radius_array[3],holder.s_radius_array[3]});
            }
            mStateListDrawable.addState(new int[]{android.R.attr.state_enabled,android.R.attr.state_checked},mCheckedDrawable);
        }

        if(holder.s_solid_color != Color.TRANSPARENT || (holder.s_stroke_color!= Color.TRANSPARENT && holder.s_stroke_width>0)){
            stateCount++;
            GradientDrawable mDefaultDrawable = new GradientDrawable();
            mDefaultDrawable.setColor(holder.s_solid_color);
            mDefaultDrawable.setStroke((int) holder.s_stroke_width,holder.s_stroke_color);
            if(holder.radiusEqual()){
                mDefaultDrawable.setCornerRadius(holder.s_radius);
            }else{
                mDefaultDrawable.setCornerRadii(new float[]{holder.s_radius_array[0],holder.s_radius_array[0],holder.s_radius_array[1],holder.s_radius_array[1],
                        holder.s_radius_array[2],holder.s_radius_array[2],holder.s_radius_array[3],holder.s_radius_array[3]});
            }
            mStateListDrawable.addState(new int[]{android.R.attr.state_enabled},mDefaultDrawable);
        }
        if(stateCount > 0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(mStateListDrawable);
            }else{
                setBackgroundDrawable(mStateListDrawable);
            }
        }

        if(holder.s_text_color == Color.TRANSPARENT) holder.s_text_color = getCurrentTextColor();
        if(holder.s_text_pressed_color == Color.TRANSPARENT) holder.s_text_pressed_color = getCurrentTextColor();
        if(holder.s_text_checked_color == Color.TRANSPARENT) holder.s_text_checked_color = getCurrentTextColor();
        if(holder.s_text_enable_color == Color.TRANSPARENT) holder.s_text_enable_color = getCurrentTextColor();

        ColorStateList mColorStateList = new ColorStateList(new int[][]{new int[]{-android.R.attr.state_enabled},new int[]{android.R.attr.state_enabled,android.R.attr.state_pressed}
                ,new int[]{android.R.attr.state_enabled,android.R.attr.state_checked},new int[]{android.R.attr.state_enabled}},
                new int[]{holder.s_text_enable_color,holder.s_text_pressed_color,holder.s_text_checked_color,holder.s_text_color});
        setTextColor(mColorStateList);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        holder.s_text_color = color;
        holder.s_text_checked_color = color;
        holder.s_text_enable_color = color;
        holder.s_text_pressed_color = color;
    }

    public int getS_solid_color() {
        return holder.s_solid_color;
    }

    public void setS_solid_color(int s_solid_color) {
        this.holder.s_solid_color = s_solid_color;
        setup();
    }

    public int getS_solid_pressed_color() {
        return holder.s_solid_pressed_color;
    }

    public void setS_solid_pressed_color(int s_solid_pressed_color) {
        this.holder.s_solid_pressed_color = s_solid_pressed_color;
        setup();
    }

    public int getS_solid_checked_color() {
        return holder.s_solid_checked_color;
    }

    public void setS_solid_checked_color(int s_solid_checked_color) {
        this.holder.s_solid_checked_color = s_solid_checked_color;
        setup();
    }

    public int getS_solid_enable_color() {
        return holder.s_solid_enable_color;
    }

    public void setS_solid_enable_color(int s_solid_enable_color) {
        this.holder.s_solid_enable_color = s_solid_enable_color;
        setup();
    }

    public int getS_stroke_color() {
        return holder.s_stroke_color;
    }

    public void setS_stroke_color(int s_stroke_color) {
        this.holder.s_stroke_color = s_stroke_color;
        setup();
    }

    public int getS_stroke_pressed_color() {
        return holder.s_stroke_pressed_color;
    }

    public void setS_stroke_pressed_color(int s_stroke_pressed_color) {
        this.holder.s_stroke_pressed_color = s_stroke_pressed_color;
        setup();
    }

    public int getS_stroke_checked_color() {
        return holder.s_stroke_checked_color;
    }

    public void setS_stroke_checked_color(int s_stroke_checked_color) {
        this.holder.s_stroke_checked_color = s_stroke_checked_color;
        setup();
    }

    public int getS_stroke_enable_color() {
        return holder.s_stroke_enable_color;
    }

    public void setS_stroke_enable_color(int s_stroke_enable_color) {
        this.holder.s_stroke_enable_color = s_stroke_enable_color;
        setup();
    }

    public int getS_text_color() {
        return holder.s_text_color;
    }

    public void setS_text_color(int s_text_color) {
        this.holder.s_text_color = s_text_color;
        setup();
    }

    public int getS_text_pressed_color() {
        return holder.s_text_pressed_color;
    }

    public void setS_text_pressed_color(int s_text_pressed_color) {
        this.holder.s_text_pressed_color = s_text_pressed_color;
        setup();
    }

    public int getS_text_checked_color() {
        return holder.s_text_checked_color;
    }

    public void setS_text_checked_color(int s_text_checked_color) {
        this.holder.s_text_checked_color = s_text_checked_color;
        setup();
    }

    public int getS_text_enable_color() {
        return holder.s_text_enable_color;
    }

    public void setS_text_enable_color(int s_text_enable_color) {
        this.holder.s_text_enable_color = s_text_enable_color;
        setup();
    }

    public float getS_radius() {
        return holder.s_radius;
    }

    public void setS_radius(float s_radius) {
        this.holder.s_radius = s_radius;
        setup();
    }

    public float[] getS_radius_array() {
        return holder.s_radius_array;
    }

    public void setS_radius_array(float[] s_radius_array) {
        this.holder.s_radius_array = s_radius_array;
        setup();
    }

    public float getS_stroke_width() {
        return holder.s_stroke_width;
    }

    public void setS_stroke_width(float s_stroke_width) {
        this.holder.s_stroke_width = s_stroke_width;
        setup();
    }
}
