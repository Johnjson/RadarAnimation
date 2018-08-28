

package com.im.jhonjson.radaranimation.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.im.jhonjson.radaranimation.R;


/**
 * 圆形ImageView
 */
public class CircleImageView extends android.support.v7.widget.AppCompatImageView {

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 1;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private int DEFAULT_BORDER_COLOR = Color.BLACK;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();
    private final Paint mCornerPaint = new Paint();

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    private int mCornerRadius = 0;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mDrawableRadius;
    private float mBorderRadius;

    private boolean mReady;
    private boolean mSetupPending;

    private int icon_left_top = 0, icon_left_bottom = 0, icon_right_top = 0, icon_right_bottom = 0;

    private boolean corner_left_top_round, icon_left_bottom_round, corner_right_top_round, corner_right_bottom_round;

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setScaleType(SCALE_TYPE);
//        DEFAULT_BORDER_COLOR = ResUtil.getColor(MyApplication.appContext, R.color.lyg_common_bg);
        DEFAULT_BORDER_COLOR = Color.TRANSPARENT;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_CircleImageView_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.CircleImageView_CircleImageView_border_color, DEFAULT_BORDER_COLOR);
        mCornerRadius = a.getDimensionPixelSize(R.styleable.CircleImageView_CircleImageView_corner_radius, 0);
        icon_left_top = a.getResourceId(R.styleable.CircleImageView_CircleImageView_corner_left_top_icon, 0);
        icon_left_bottom = a.getResourceId(R.styleable.CircleImageView_CircleImageView_corner_left_bottom_icon, 0);
        icon_right_top = a.getResourceId(R.styleable.CircleImageView_CircleImageView_corner_right_top_icon, 0);
        icon_right_bottom = a.getResourceId(R.styleable.CircleImageView_CircleImageView_corner_right_bottom_icon, 0);

        corner_left_top_round = a.getBoolean(R.styleable.CircleImageView_CircleImageView_corner_left_top_round, false);
        icon_left_bottom_round = a.getBoolean(R.styleable.CircleImageView_CircleImageView_corner_left_bottom_round, false);
        corner_right_top_round = a.getBoolean(R.styleable.CircleImageView_CircleImageView_corner_right_top_round, false);
        corner_right_bottom_round = a.getBoolean(R.styleable.CircleImageView_CircleImageView_corner_right_bottom_round, false);

        a.recycle();
        mReady = true;
        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    /**
     * 设置上下左右的icon
     *
     * @param leftTopIcon     左上
     * @param leftBottomIcon  左下
     * @param rightTopIcon    右上
     * @param rightBottomIcon 右下
     */
    public void setCornerIcon(int leftTopIcon, int leftBottomIcon, int rightTopIcon, int rightBottomIcon) {
        icon_left_top = leftTopIcon;
        icon_left_bottom = leftBottomIcon;
        icon_right_top = rightTopIcon;
        icon_right_bottom = rightBottomIcon;
        setup();
    }

    public void setCornerIcon(int rightTopIcon) {
        icon_right_top = rightTopIcon;
        setup();
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        int halfWidth = getWidth() / 2;
        int halfHeight = getHeight() / 2;

        if (icon_left_top != 0) {
            canvas.drawRect(0, 0, halfWidth, halfHeight, mCornerPaint);
        }
        if (icon_left_bottom != 0) {
            canvas.drawRect(0, halfHeight, halfWidth, getHeight(), mCornerPaint);
        }
        if (icon_right_top != 0) {
            canvas.drawRect(halfWidth, 0, getWidth(), halfHeight, mCornerPaint);
        }
        if (icon_right_bottom != 0) {
            canvas.drawRect(halfWidth, halfHeight, getWidth(), getHeight(), mCornerPaint);
        }

        if (mCornerRadius == 0) {
            // 边框宽度是0，不绘制边框
            if (mBorderWidth != 0) {
                canvas.drawCircle(halfWidth, halfHeight, mBorderRadius, mBorderPaint);
            }
            canvas.drawCircle(halfWidth, halfHeight, mDrawableRadius, mBitmapPaint);
        } else {
            // 边框宽度是0，不绘制边框
            if (mBorderWidth != 0) {
                canvas.drawRoundRect(mBorderRect, mCornerRadius + mBorderWidth * 2, mCornerRadius + mBorderWidth * 2, mBorderPaint);
            } else {
                // 设置某几个圆角暂时不可以和边框一起使用
                if (corner_left_top_round) {
                    canvas.drawRect(0, 0, mCornerRadius, mCornerRadius, mBitmapPaint);
                }
                if (icon_left_bottom_round) {
                    canvas.drawRect(0, getHeight() - mCornerRadius, mCornerRadius, getHeight(), mBitmapPaint);
                }
                if (corner_right_top_round) {
                    canvas.drawRect(getWidth() - mCornerRadius, 0, getWidth(), mCornerRadius, mBitmapPaint);
                }
                if (corner_right_bottom_round) {
                    canvas.drawRect(getWidth() - mCornerRadius, getHeight() - mCornerRadius, getWidth(), getHeight(), mBitmapPaint);
                }
            }
            canvas.drawRoundRect(mDrawableRect, mCornerRadius, mCornerRadius, mBitmapPaint);
        }
        //左下
        if (icon_left_bottom != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icon_left_bottom);
            canvas.drawBitmap(bitmap,
                    mBorderWidth,
                    getHeight() - mBorderWidth - bitmap.getHeight(),
                    null);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        //左上
        if (icon_left_top != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icon_left_top);
            canvas.drawBitmap(bitmap,
                    mBorderWidth,
                    mBorderWidth,
                    null);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        //右下
        if (icon_right_bottom != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icon_right_bottom);
            canvas.drawBitmap(bitmap,
                    getWidth() - mBorderWidth - bitmap.getWidth(),
                    getHeight() - mBorderWidth - bitmap.getHeight(),
                    null);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        //右上
        if (icon_right_top != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icon_right_top);
            canvas.drawBitmap(bitmap,
                    getWidth() - mBorderWidth - bitmap.getWidth(),
                    mBorderWidth,
                    null);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }
        mBorderColor = borderColor;
        setup();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        int width = dip2px(getContext(), borderWidth);
        if (width == mBorderWidth) {
            return;
        }
        mBorderWidth = width;
        setup();
    }

    @Deprecated
    public void setmCornerRadius(int mCornerRadius) {
        this.mCornerRadius = mCornerRadius;
    }

    public void setCornerRadius(int mCornerRadius) {
        this.mCornerRadius = mCornerRadius;
    }

    private int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth() < COLORDRAWABLE_DIMENSION ? COLORDRAWABLE_DIMENSION : drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight() < COLORDRAWABLE_DIMENSION ? COLORDRAWABLE_DIMENSION : drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (mBitmap == null) {
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mCornerPaint.setStyle(Paint.Style.FILL);
        mCornerPaint.setAntiAlias(true);
        mCornerPaint.setColor(mBorderColor);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderRect.set(0, 0, getWidth(), getHeight());

        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2);

        mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth);
        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);

        updateShaderMatrix();
        invalidate();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) + mBorderWidth);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

}