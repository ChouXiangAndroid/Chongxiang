package com.wangze.chouxiang.sjx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.util.ArrayList;


class CoordinateInformation
{
    private int left;
    private int right;
    private int top;
    private int bottom;
    private static CoordinateInformation my = null;

    public int getLeft() { return left; }

    public int getRight() { return right; }

    public int getTop() { return top; }

    public int getBottom() { return bottom; }

    private CoordinateInformation(int left, int right, int top, int bottom)
    {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public static CoordinateInformation Of(Person person)
    {
        if (my == null)
        {
            my = new CoordinateInformation(person.getLeft(), person.getLeft() + person.getWidth(), person.getTop(), person.getTop() + person.getHeight());
        } else
        {
            my.left = person.getLeft();
            my.top = person.getTop();
            my.right = person.getLeft() + person.getWidth();
            my.bottom = person.getTop() + person.getHeight();
        }
        return my;
    }


}

public class RecognizeImageView extends ImageView
{
    private ArrayList<Person> personList;
    private Paint maLiang = new Paint();
    private Bitmap bitmap;
    Rect mSrcRect, mDestRect;

    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }

    public void setPersonList(ArrayList<Person> personList) { this.personList = personList; }

    public RecognizeImageView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        setImageBitmap(bitmap);
        mSrcRect = new Rect(0, 0, getWidth(), getHeight());
        mDestRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawBitmap(bitmap, mSrcRect, mDestRect, maLiang);
        if (personList == null)
            return;
        for (Person user : personList)
        {
            CoordinateInformation cf = CoordinateInformation.Of(user);

            maLiang.setColor(Color.BLACK);
            maLiang.setTextSize(100);
            canvas.drawText(String.valueOf(user.getId()), cf.getLeft(), cf.getTop(), maLiang);

            maLiang.setColor(Color.WHITE);
            maLiang.setStrokeWidth(2);
            canvas.drawLine(cf.getLeft(), cf.getTop(), cf.getRight(), cf.getTop(), maLiang);
            canvas.drawLine(cf.getLeft(), cf.getTop(), cf.getLeft(), cf.getBottom(), maLiang);
            canvas.drawLine(cf.getRight(), cf.getTop(), cf.getRight(), cf.getBottom(), maLiang);
            canvas.drawLine(cf.getLeft(), cf.getBottom(), cf.getRight(), cf.getBottom(), maLiang);
        }
    }

}
