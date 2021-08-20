package com.GymnasticsWisdom.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;


import com.GymnasticsWisdom.R;

import java.util.ArrayList;
import java.util.List;

public class TextStickerView extends View {
    public static final float TEXT_SIZE_DEFAULT = 40;
    public static final int PADDING = 32;
    //public static final int PADDING = 0;

    public static final int TEXT_TOP_PADDING = 10;

    //public static final int CHAR_MIN_HEIGHT = 60;


    //private String mText;
    private TextPaint mPaint = new TextPaint();
    private Paint debugPaint = new Paint();
    private Paint mHelpPaint = new Paint();

    private Rect mTextRect = new Rect();// warp text rect record
    private RectF mHelpBoxRect = new RectF();
    private Rect mDeleteRect = new Rect();//删除按钮位置
    private Rect mRotateRect = new Rect();//旋转按钮位置
//    private Rect mScaleRect = new Rect();//旋转按钮位置
    private Rect mEditRect = new Rect();//旋转按钮位置

    private OperationListener operationListener;
    private RectF mDeleteDstRect = new RectF();
    private RectF mRotateDstRect = new RectF();
//    private RectF mScaleDstRect = new RectF();
    private RectF mEditDstRect = new RectF();
    String getfontfile;
    private Bitmap mDeleteBitmap;
    private Bitmap mRotateBitmap;
//    private Bitmap mScaleBitmap;
    private Bitmap mEditBitmap;
    Bitmap mBitmap;
    private int mScreenwidth, mScreenHeight;

    Boolean isInEdit = true;

    private int mCurrentMode = IDLE_MODE;
    //控件的几种模式
    private static final int IDLE_MODE = 2;
    private static final int MOVE_MODE = 3;
    private static final int ROTATE_MODE = 4;
    private static final int DELETE_MODE = 5;
    private static final int EDIT_MODE = 6;

    private EditText mEditText;//输入控件
    private Matrix matrix = new Matrix();

    public int layout_x = 0;
    public static final int STICKER_BTN_HALF_SIZE = 20;
    public int layout_y = 0;

    private float last_x = 0;
    private float last_y = 0;

    public float mRotateAngle = 0;
    public float mScale = 1;
    private boolean isInitLayout = true;

    public static boolean isShowHelpBox = true;
    public  boolean isEdit = true;

    private boolean mAutoNewLine = false;//是否需要自动换行
    private List<String> mTextContents = new ArrayList<String>(2);//存放所写的文字内容
    private String mText;
    private float MIN_SCALE = 0.5f;
    private float MAX_SCALE = 1.5f;
    private DisplayMetrics dm;


    private Point mPoint = new Point(0, 0);

    public TextStickerView(Context context) {
        super(context);
        initView(context);
    }

    public TextStickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TextStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setEditText(EditText textView) {
        this.mEditText = textView;
    }

    private void initView(Context context) {
        debugPaint.setColor(Color.parseColor("#66ff0000"));


        mDeleteBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.border_cancel_icon);
        mRotateBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.border_move_icon);
//        mScaleBitmap = BitmapFactory.decodeResource(context.getResources(),
//                R.drawable.edit);

        mEditBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.edit);

        mDeleteRect.set(0, 0, mDeleteBitmap.getWidth(), mDeleteBitmap.getHeight());
        mRotateRect.set(0, 0, mRotateBitmap.getWidth(), mRotateBitmap.getHeight());
//        mScaleRect.set(0, 0, mScaleBitmap.getWidth(), mScaleBitmap.getHeight());
        mEditRect.set(0, 0, mEditBitmap.getWidth(), mEditBitmap.getHeight());

        mDeleteDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1, STICKER_BTN_HALF_SIZE << 1);
        mRotateDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1, STICKER_BTN_HALF_SIZE << 1);
//        mScaleDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1, STICKER_BTN_HALF_SIZE << 1);
        mEditDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1, STICKER_BTN_HALF_SIZE << 1);
//
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(TEXT_SIZE_DEFAULT);
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.LEFT);

        mHelpPaint.setColor(Color.RED);
        mHelpPaint.setStyle(Paint.Style.STROKE);
        mHelpPaint.setAntiAlias(true);
        mHelpPaint.setStrokeWidth(2);
    }

    public void setText(String text) {
        this.mText = text;
        invalidate();
    }

    public String getText(){
        return mText;
    }

    public void setTextSize(int size) {
        mPaint.setTextSize(size);
        invalidate();
    }


    public void setTypeFace(Context context, String fontfile) {
        Typeface titleFont = Typeface.createFromAsset(context.getAssets(), fontfile);
        mPaint.setTypeface(titleFont);
        invalidate();
    }

    public void setTextColor(int newColor) {
        mPaint.setColor(newColor);
        invalidate();
    }

    public void setTextShadow(int color, int sizes) {
        mPaint.setShadowLayer(sizes, 0, 0, color);
        invalidate();
    }

    public void setTextFontFile(Context context, String fontfile) {
        Typeface titleFont = Typeface.createFromAsset(context.getAssets(), "font/" + fontfile);
//        setTypeface = titleFont;
        mPaint.setTypeface(titleFont);
        invalidate();
        getfontfile = fontfile;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isInitLayout) {
            isInitLayout = false;
            resetView();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (TextUtils.isEmpty(mText))
            return;


        parseText();
        drawContent(canvas);
    }

    protected void parseText() {
        if (TextUtils.isEmpty(mText))
            return;

        mTextContents.clear();

        String[] splits = mText.split("\n");
        for (String text : splits) {
            mTextContents.add(text);
        }//end for each
    }

    private void drawContent(Canvas canvas) {
        drawText(canvas);
        float[] arrayOfFloat = new float[9];
        Matrix matrix = new Matrix();
        matrix.getValues(arrayOfFloat);
        float f1 = 0.0F * arrayOfFloat[0] + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0F * arrayOfFloat[3] + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = arrayOfFloat[0] * canvas.getWidth() + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
        float f4 = arrayOfFloat[3] * canvas.getWidth() + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
        float f5 = 0.0F * arrayOfFloat[0] + arrayOfFloat[1] * canvas.getHeight() + arrayOfFloat[2];
        float f6 = 0.0F * arrayOfFloat[3] + arrayOfFloat[4] * canvas.getHeight() + arrayOfFloat[5];
        float f7 = arrayOfFloat[0] * canvas.getWidth() + arrayOfFloat[1] * canvas.getHeight() + arrayOfFloat[2];
        float f8 = arrayOfFloat[3] * canvas.getWidth() + arrayOfFloat[4] * canvas.getHeight() + arrayOfFloat[5];

        canvas.save();
        int offsetValue = ((int) mDeleteDstRect.width()) >> 1;
        mDeleteDstRect.offsetTo(mHelpBoxRect.left - offsetValue, mHelpBoxRect.top - offsetValue);
        mRotateDstRect.offsetTo(mHelpBoxRect.right - offsetValue, mHelpBoxRect.bottom - offsetValue);
        mEditDstRect.offsetTo(mHelpBoxRect.right - offsetValue, mHelpBoxRect.top - offsetValue);
//        mEditDstRect.offsetTo(mHelpBoxRect.left - offsetValue, mHelpBoxRect.bottom - offsetValue);

        RectUtil.rotateRect(mDeleteDstRect, mHelpBoxRect.centerX(),
                mHelpBoxRect.centerY(), mRotateAngle);
        RectUtil.rotateRect(mRotateDstRect, mHelpBoxRect.centerX(),
                mHelpBoxRect.centerY(), mRotateAngle);
        RectUtil.rotateRect(mEditDstRect, mHelpBoxRect.centerX(),
                mHelpBoxRect.centerY(), mRotateAngle);

//        RectUtil.rotateRect(mEditDstRect, mHelpBoxRect.centerX(),
//                mHelpBoxRect.centerY(), mRotateAngle);



        if (isInEdit) {

            canvas.save();
            canvas.rotate(mRotateAngle, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
            canvas.drawRoundRect(mHelpBoxRect, 2, 2, mHelpPaint);
            canvas.restore();

            canvas.drawBitmap(mDeleteBitmap, mDeleteRect, mDeleteDstRect, null);
            canvas.drawBitmap(mRotateBitmap, mRotateRect, mRotateDstRect, null);
            canvas.drawBitmap(mEditBitmap, mEditRect, mEditDstRect, null);
//            canvas.drawBitmap(mEditBitmap, mEditRect, mEditDstRect, null);

        }


    }

    public void setInEdit(boolean isInEdit) {
        this.isInEdit = isInEdit;
        invalidate();
    }

    private void drawText(Canvas canvas) {
        drawText(canvas, layout_x, layout_y, mScale, mRotateAngle);
    }

    public void drawText(Canvas canvas, int _x, int _y, float scale, float rotate) {
        if (ListUtil.isEmpty(mTextContents))
            return;

        int x = _x;
        int y = _y;
        int text_height = 0;

        mTextRect.set(0, 0, 0, 0);//clear
        Rect tempRect = new Rect();
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int charMinHeight = Math.abs(fontMetrics.top) + Math.abs(fontMetrics.bottom);//字体高度
        text_height = charMinHeight;
        //System.out.println("top = "+fontMetrics.top +"   bottom = "+fontMetrics.bottom);
        for (int i = 0; i < mTextContents.size(); i++) {
            String text = mTextContents.get(i);
            mPaint.getTextBounds(text, 0, text.length(), tempRect);
            //System.out.println(i + " ---> " + tempRect.height());
            //text_height = Math.max(charMinHeight, tempRect.height());
            if (tempRect.height() <= 0) {//处理此行文字为空的情况
                tempRect.set(0, 0, 0, text_height);
            }

            RectUtil.rectAddV(mTextRect, tempRect, 0, charMinHeight);
        }//end for i

        mTextRect.offset(x, y);

        mHelpBoxRect.set(mTextRect.left - PADDING, mTextRect.top - PADDING
                , mTextRect.right + PADDING, mTextRect.bottom + PADDING);
        RectUtil.scaleRect(mHelpBoxRect, scale);

        canvas.save();
        canvas.scale(scale, scale, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.rotate(rotate, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());


        int draw_text_y = y + (text_height >> 1) + PADDING;
        for (int i = 0; i < mTextContents.size(); i++) {
            canvas.drawText(mTextContents.get(i), x, draw_text_y, mPaint);
            draw_text_y += text_height;
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mDeleteDstRect.contains(x, y)) {
                    isShowHelpBox = true;
                    mCurrentMode = DELETE_MODE;
                } else if (mRotateDstRect.contains(x, y)) {
                    isShowHelpBox = true;
                    mCurrentMode = ROTATE_MODE;
                    last_x = mRotateDstRect.centerX();

                    last_y = mRotateDstRect.centerY();
                    ret = true;
                } else if (mEditDstRect.contains(x,y)){


                    if (operationListener != null) {
                        operationListener.onEditSticker(this);
                    }

                    last_x = x;
                    last_y = y;
                    ret = true;

                }else if (detectInHelpBox(x, y)) {// 移动模式
                    isShowHelpBox = true;
                    mCurrentMode = MOVE_MODE;
                    last_x = x;
                    last_y = y;
                    ret = true;
                } else {
                    isShowHelpBox = false;
                    invalidate();
                }// end if

                if (mCurrentMode == DELETE_MODE) {
                    mCurrentMode = IDLE_MODE;
                    clearTextContent();
                    isShowHelpBox = false;
                    invalidate();
                }// end if
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                if (mCurrentMode == MOVE_MODE) {
                    mCurrentMode = MOVE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;

                    layout_x += dx;
                    layout_y += dy;

                    invalidate();

                    last_x = x;
                    last_y = y;

                } else if (mCurrentMode == ROTATE_MODE) {// 旋转 缩放文字操作
                    mCurrentMode = ROTATE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;

                    updateRotateAndScale(dx, dy);

                    invalidate();
                    last_x = x;
                    last_y = y;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                invalidate();
            case MotionEvent.ACTION_UP:
                invalidate();
                ret = false;
                mCurrentMode = IDLE_MODE;




                break;
        }


        if (ret && operationListener != null) {
            operationListener.onEdit(this);
        }

        return ret;
    }



    private boolean detectInHelpBox(float x, float y) {
        //mRotateAngle
        mPoint.set((int) x, (int) y);
                RectUtil.rotatePoint(mPoint, mHelpBoxRect.centerX(), mHelpBoxRect.centerY(), -mRotateAngle);
        return mHelpBoxRect.contains(mPoint.x, mPoint.y);
    }

    public void clearTextContent() {

        if (operationListener != null) {
            operationListener.onDeleteClick(this);
        }

    }

    public void setOperationListener(OperationListener operationListener) {
        this.operationListener = operationListener;
    }



    public void updateRotateAndScale(final float dx, final float dy) {

        float c_x = mHelpBoxRect.centerX();
        float c_y = mHelpBoxRect.centerY();

        float x = mRotateDstRect.centerX();
        float y = mRotateDstRect.centerY();

        float n_x = x + dx;
        float n_y = y + dy;

        float xa = x - c_x;
        float ya = y - c_y;

        float xb = n_x - c_x;
        float yb = n_y - c_y;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        float scale = curLen / srcLen;

        mScale *= scale;
        float newWidth = mHelpBoxRect.width() * mScale;

        if (newWidth < 70) {
            mScale /= scale;
            return;
        }

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1)
            return;
        float angle = (float) Math.toDegrees(Math.acos(cos));
        float calMatrix = xa * yb - xb * ya;

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        mRotateAngle += angle;
    }

    public void resetView() {
        layout_x = getMeasuredWidth() / 2;
        layout_y = getMeasuredHeight() / 2;
        mRotateAngle = 0;
        mScale = 1;
        mTextContents.clear();
    }

    public float getScale() {
        return mScale;
    }

    public interface OperationListener {
        void onDeleteClick(TextStickerView textStickerView);

        void onEdit(TextStickerView bubbleTextView);

        void onEditSticker(TextStickerView bubbleTextView);

        void onClick(TextStickerView bubbleTextView);

        void onTop(TextStickerView bubbleTextView);

    }


}
