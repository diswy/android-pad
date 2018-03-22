package gorden.widget.selector;

/**
 * selector holder
 * Created by gorden on 2016/8/5.
 */
public class SelectorHolder {
    protected int s_solid_color;//默认填充颜色
    protected int s_solid_pressed_color;//按下颜色
    protected int s_solid_checked_color;//选择后的颜色
    protected int s_solid_enable_color;//不可用的颜色

    protected int s_stroke_color;//边框线
    protected int s_stroke_pressed_color;
    protected int s_stroke_checked_color;
    protected int s_stroke_enable_color;

    protected int s_text_color;//文字颜色
    protected int s_text_pressed_color;
    protected int s_text_checked_color;
    protected int s_text_enable_color;

    protected float s_radius;
    protected float[] s_radius_array = new float[4];
    protected float s_stroke_width;

    protected boolean radiusEqual(){
        if(s_radius_array[0] == s_radius_array[1] && s_radius_array[0] == s_radius_array[2] && s_radius_array[0] == s_radius_array[3])
            return true;
        return false;
    }
}
