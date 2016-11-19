package com.bodekjan.uyweather.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.blankj.utilcode.utils.ConvertUtils;

/**
 * Created by bodekjan on 2016/10/26.
 */
public class PMMeter extends View {
    private int maxvalue;
    private int minvalue;
    private int value;
    int angle=0;
    Context context;
    int c_angle1;
    int start_radian=0;
    int end_radian=180;
    public PMMeter(Context context) {
        super(context);
        this.context=context;
        // TODO Auto-generated constructor stub
        //	new PointThread().start();
    }

    public PMMeter(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.context=context;
        // TODO Auto-generated constructor stub
        //new PointThread().start();
    }

    public PMMeter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
        // TODO Auto-generated constructor stub
    }

    Handler handler2 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if(value==minvalue){
                angle=start_radian;
            }else if(value==maxvalue){
                angle=end_radian;
            }else{
                double m=(double)value/(double)maxvalue;
                angle=(int)(m*180);
            }
            invalidate();
            super.handleMessage(msg);
        }

    };

    public void setValue(int value) {
        this.value = value;
        this.angle=value;
        handler2.sendEmptyMessage(0);
    }

    public void setMaxValue(int maxvalue) {
        this.maxvalue = maxvalue;

    }

    public void setMinValue(int min) {
        this.minvalue = min;

    }
    public void setColors(int s[]){
        int i=s[1]-s[0];
        double iDouble=(double)i;
        double xDouble=(double)(maxvalue-minvalue);
        double k=iDouble/xDouble;
        double a=(k*(double)180);
        c_angle1=(int)a;

    }
    /**
     * 范围0和正数
     * */
    public void setStartRadian(int radian) {
        this.start_radian = radian;
    }
    /**
     * 范围0和正数
     * */
    public void setEndRadian(int radian) {
        this.end_radian = radian;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.translate(0,-30);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        int h=this.getMeasuredHeight();
        int w=this.getMeasuredWidth();
        int pl=this.getPaddingLeft();
        paint.setColor(Color.WHITE);
        int x0 = w/2-20, y0 = h-50, r = w/2-80;
        int cx0 = x0 + 25, cy0 = y0, r2 = r - 20;
        int cx1=x0+23;
        int cy1=y0;
        paint.setStyle(Paint.Style.STROKE);
        double pingjun = (maxvalue - minvalue) / ((end_radian-start_radian)/20);
        paint.setTextSize(20);
        int count=-1;
        for (int i = start_radian; i <= end_radian; i += 20) {
            count++;
            int x1 = (int) (x0 + (r+30) * Math.cos((-i) * -Math.PI / 180))+10;
            int y1 = (int) (y0 + (r +20)* Math.sin((-i) * -Math.PI / 180));
            canvas.drawText((int)(count*pingjun) + "", x1, y1, paint);
//			}

        }
        int dis_move=(end_radian-start_radian)/3;
        paint.setStrokeWidth(15);
//		paint.setColor(Color.argb(100, 1,204,0));//绿
        paint.setColor(Color.GREEN);//绿
        canvas.drawArc(new RectF(x0 - r + 25, y0 - r + 10, x0 + r +20, y0 + r),
                start_radian,dis_move, false, paint);
//		paint.setColor(Color.argb(100, 255,234,1));//黄
        paint.setColor(Color.YELLOW);//黄
        canvas.drawArc(new RectF(x0 - r + 25, y0 - r + 10, x0 + r + 20, y0 + r),
                start_radian+dis_move, dis_move, false, paint);
//		paint.setColor(Color.argb(100, 254,0,0));//红
        paint.setColor(Color.RED);//红
        canvas.drawArc(new RectF(x0 - r + 25, y0 - r + 10, x0 + r + 20, y0 + r),
                start_radian+dis_move*2, dis_move, false, paint);
        paint.setStrokeWidth(5);
        for (int i = start_radian; i <= end_radian; i += 20) {
            int x1 = (int) (cx1+ + (r2 + 5)
                    * Math.cos((-i) * -Math.PI / 180));
            int y1 = (int) (cy1 + (r2 )
                    * Math.sin(( -i) * -Math.PI / 180));
            int x2 = (int) (cx1+ (r2 - 10)
                    * Math.cos((- i) * -Math.PI / 180));
            int y2 = (int) (cy1 + (r2 - 10)
                    * Math.sin((- i) * -Math.PI / 180));
            canvas.drawLine(x1, y1, x2, y2, paint);
        }
        Double rawExtra = new Double((180-angle)*0.11111);
        int angleExtra=rawExtra.intValue();
        int x3 = (int) (cx0 + (r2 - 20) * Math.cos((180 - angle + angleExtra) * -Math.PI / 180));
        int y3 = (int) (cy0 + (r2 - 20) * Math.sin((180 - angle + angleExtra) * -Math.PI / 180));
        canvas.drawLine(cx0, cy0 ,x3 , y3, paint);//ָ��
        canvas.drawCircle(cx0,cy0,5,paint);




    }
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
    }
}