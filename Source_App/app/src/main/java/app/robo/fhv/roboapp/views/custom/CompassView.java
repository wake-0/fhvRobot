package app.robo.fhv.roboapp.views.custom;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import app.robo.fhv.roboapp.R;

/**
 * Speedometer with needle.
 *
 * Created by danon on 26.02.14.
 * @version 1.0
 * @author Anton Danshin <a href="mailto:anton.danshin@frtk.ru">anton.danshin@frtk.ru</a>
 */
public class CompassView extends View {

    private static final String LOG_TAG = CompassView.class.getSimpleName();

    public static final int DEFAULT_MINOR_TICKS = 5;
    public static final int DEFAULT_LABEL_TEXT_SIZE_DP = 12;
    public static final int COMPASS_MAX_VALUE = 360;
    public static final int ANGLE_RANGE = 60;

    private double angle = 0;
    private int defaultColor = Color.rgb(180, 180, 180);
    private double majorTickStep = 10;
    private int minorTicks = DEFAULT_MINOR_TICKS;

    private Paint backgroundPaint;
    private Paint backgroundInnerPaint;
    private Paint maskPaint;
    private Paint needlePaint;
    private Paint ticksPaint;
    private Paint txtPaint;
    private Paint colorLinePaint;
    private int labelTextSize;

    public CompassView(Context context) {
        super(context);
        init();

        float density = getResources().getDisplayMetrics().density;
        setLabelTextSize(Math.round(DEFAULT_LABEL_TEXT_SIZE_DP * density));
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);

        float density = getResources().getDisplayMetrics().density;
        init();
    }


    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        if (angle < 0)
            throw new IllegalArgumentException("Non-positive value specified as a angle.");
        while(angle > COMPASS_MAX_VALUE)
            angle -= COMPASS_MAX_VALUE;
        this.angle = angle;
        invalidate();
    }


    @TargetApi(11)
    public ValueAnimator setAngle(double progress, long duration, long startDelay) {
        if (progress <= 0)
            throw new IllegalArgumentException("Non-positive value specified as a angle.");

        while(progress > COMPASS_MAX_VALUE)
            progress -= COMPASS_MAX_VALUE;

        ValueAnimator va = ValueAnimator.ofObject(new TypeEvaluator<Double>() {
            @Override
            public Double evaluate(float fraction, Double startValue, Double endValue) {
                return startValue + fraction*(endValue-startValue);
            }
        }, Double.valueOf(getAngle()), Double.valueOf(progress));

        va.setDuration(duration);
        va.setStartDelay(startDelay);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Double value = (Double) animation.getAnimatedValue();
                if (value != null)
                    setAngle(value);
            }
        });
        va.start();
        return va;
    }

    @TargetApi(11)
    public ValueAnimator setAngle(double progress, boolean animate) {
        return setAngle(progress, 1500, 200);
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
        invalidate();
    }

    public double getMajorTickStep() {
        return majorTickStep;
    }

    public void setMajorTickStep(double majorTickStep) {
        if (majorTickStep <= 0)
            throw new IllegalArgumentException("Non-positive value specified as a major tick step.");
        this.majorTickStep = majorTickStep;
        invalidate();
    }

    public int getMinorTicks() {
        return minorTicks;
    }

    public void setMinorTicks(int minorTicks) {
        this.minorTicks = minorTicks;
        invalidate();
    }


    public int getLabelTextSize() {
        return labelTextSize;
    }

    public void setLabelTextSize(int labelTextSize) {
        this.labelTextSize = labelTextSize;
        if (txtPaint != null) {
            txtPaint.setTextSize(labelTextSize);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Clear canvas
        canvas.drawColor(Color.TRANSPARENT);

        // Draw Metallic Arc and background
        drawBackground(canvas);

        // Draw Ticks and colored arc
        drawTicks(canvas);

        // Draw Needle
        drawNeedle(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
            //Must be this size
            width = widthSize;
        } else {
            width = -1;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST) {
            //Must be this size
            height = heightSize;
        } else {
            height = -1;
        }

        if (height >= 0 && width >= 0) {
            width = Math.min(height, width);
            height = width/2;
        } else if (width >= 0) {
            height = width/2;
        } else if (height >= 0) {
            width = height*2;
        } else {
            width = 0;
            height = 0;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }


    private void drawNeedle(Canvas canvas) {
        canvas.drawLine(
                (float) (canvas.getWidth()/2),
                (float) (0),
                (float) (canvas.getWidth()/2),
                (float) (canvas.getHeight()),
                needlePaint
        );
    }

    private void drawTicks(Canvas canvas) {
        int half = canvas.getWidth()/2;
        int startTick = (int)(angle - 30);

        startTick -= (startTick % minorTicks);
        int tickWidth = ANGLE_RANGE /minorTicks;

        for(int i = 0; i < tickWidth; i++) {
            float x = (canvas.getWidth()/tickWidth)*i;
            canvas.drawLine(
                    (float) (x),
                    (float) (0),
                    (float) (x),
                    (float) (canvas.getHeight()/4),
                    ticksPaint
            );
        }

        tickWidth = ANGLE_RANGE /(int)majorTickStep;

        for(int i = 0; i < tickWidth; i++) {
            float x = (canvas.getWidth()/tickWidth)*i;
            canvas.drawLine(
                    (float) (x),
                    (float) (0),
                    (float) (x),
                    (float) (canvas.getHeight()/2),
                    ticksPaint
            );
        }
    }



    /*
    private void drawTicks(Canvas canvas) {
        float availableAngle = 160;
        float majorStep = (float) (majorTickStep/ maxSpeed *availableAngle);
        float minorStep = majorStep / (1 + minorTicks);

        float majorTicksLength = 30;
        float minorTicksLength = majorTicksLength/2;

        RectF oval = getOval(canvas, 1);
        float radius = oval.width()*0.35f;

        float currentAngle = 10;
        double curProgress = 0;
        while (currentAngle <= 170) {

            canvas.drawLine(
                    (float) (oval.centerX() + Math.cos((180-currentAngle)/180*Math.PI)*(radius-majorTicksLength/2)),
                    (float) (oval.centerY() - Math.sin(currentAngle/180*Math.PI)*(radius-majorTicksLength/2)),
                    (float) (oval.centerX() + Math.cos((180-currentAngle)/180*Math.PI)*(radius+majorTicksLength/2)),
                    (float) (oval.centerY() - Math.sin(currentAngle/180*Math.PI)*(radius+majorTicksLength/2)),
                    ticksPaint
            );

            for (int i=1; i<=minorTicks; i++) {
                float angle = currentAngle + i*minorStep;
                if (angle >= 170 + minorStep/2) {
                    break;
                }
                canvas.drawLine(
                        (float) (oval.centerX() + Math.cos((180 - angle) / 180 * Math.PI) * radius),
                        (float) (oval.centerY() - Math.sin(angle / 180 * Math.PI) * radius),
                        (float) (oval.centerX() + Math.cos((180 - angle) / 180 * Math.PI) * (radius + minorTicksLength)),
                        (float) (oval.centerY() - Math.sin(angle / 180 * Math.PI) * (radius + minorTicksLength)),
                        ticksPaint
                );
            }

            if (labelConverter != null) {

                canvas.save();
                canvas.rotate(180 + currentAngle, oval.centerX(), oval.centerY());
                float txtX = oval.centerX() + radius + majorTicksLength/2 + 8;
                float txtY = oval.centerY();
                canvas.rotate(+90, txtX, txtY);
                canvas.drawText(labelConverter.getLabelFor(curProgress, maxSpeed), txtX, txtY, txtPaint);
                canvas.restore();
            }

            currentAngle += majorStep;
            curProgress += majorTickStep;
        }

        RectF smallOval = getOval(canvas, 0.7f);
        colorLinePaint.setColor(defaultColor);
        canvas.drawArc(smallOval, 185, 170, false, colorLinePaint);

        for (ColoredRange range: ranges) {
            colorLinePaint.setColor(range.getColor());
            canvas.drawArc(smallOval, (float) (190 + range.getBegin()/ maxSpeed *160), (float) ((range.getEnd() - range.getBegin())/ maxSpeed *160), false, colorLinePaint);
        }
    }
    */


    private void drawBackground(Canvas canvas) {
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
    }


    @SuppressWarnings("NewApi")
    private void init() {
        if (Build.VERSION.SDK_INT >= 11 && !isInEditMode()) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(Color.rgb(127, 127, 127));
        backgroundPaint.setAlpha((int)(255*0.8d));

        backgroundInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundInnerPaint.setStyle(Paint.Style.FILL);
        backgroundInnerPaint.setColor(Color.rgb(150, 150, 150));

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(Color.WHITE);
        txtPaint.setTextSize(labelTextSize);
        txtPaint.setTextAlign(Paint.Align.CENTER);

        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setDither(true);

        ticksPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ticksPaint.setStrokeWidth(3.0f);
        ticksPaint.setStyle(Paint.Style.STROKE);
        ticksPaint.setColor(defaultColor);

        colorLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        colorLinePaint.setStyle(Paint.Style.STROKE);
        colorLinePaint.setStrokeWidth(5);
        colorLinePaint.setColor(defaultColor);

        needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        needlePaint.setStrokeWidth(5);
        needlePaint.setStyle(Paint.Style.STROKE);
        needlePaint.setColor(Color.argb(200, 255, 0, 0));
    }

}
