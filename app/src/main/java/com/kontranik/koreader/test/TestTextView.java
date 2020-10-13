package com.kontranik.koreader.test;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import com.kontranik.koreader.R;
import com.kontranik.koreader.model.MyStyle;
import com.kontranik.koreader.model.Word;

public class TestTextView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_text_view);

        TextView textView = findViewById(R.id.textViewTemp);
        ImageView imageView = findViewById(R.id.imageViewTemp);

        textView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.text_size));
        textView.setLineSpacing(0, 1f);

        Word word = new Word("TEST", MyStyle.H1);

        textView.setText(word.getData());

        textView.measure(0, 0);       //must call measure!
        int h = textView.getMeasuredHeight(); //get width
        int w = textView.getMeasuredWidth();

        textView.append("\n");
        textView.append("\n");
        textView.append(new Word( "h: " + h, MyStyle.Paragraph ).getData());
        textView.append(new Word( ", w: " + w, MyStyle.Paragraph ).getData());

        TextPaint mTextPaint = textView.getPaint();

        mTextPaint.setTextSize( mTextPaint.getTextSize()  * word.getwRelativeTextSize() );
        mTextPaint.setTypeface( Typeface.create( mTextPaint.getTypeface() , word.getWStyle()));

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        int _lineHeight = (int) Math.ceil((fm.bottom - fm.top + fm.leading));
        int _wordWidth = (int) Math.ceil(mTextPaint.measureText(word.getData().toString()));

        textView.append("\n");
        textView.append("\n");
        textView.append(new Word( "H: " + _lineHeight, MyStyle.Paragraph ).getData());
        textView.append(new Word( ", W: " + _wordWidth, MyStyle.Paragraph ).getData());

        Bitmap bitmap = Bitmap.createBitmap((int) getWindowManager()
                .getDefaultDisplay().getWidth(), (int) getWindowManager()
                .getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        imageView.setImageBitmap(bitmap);

        // Rectangle

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        float leftx = imageView.getPaddingLeft();
        float topy = 0;
        float rightx = _wordWidth;
        float bottomy = _lineHeight;
        canvas.drawRect(leftx, topy, rightx, bottomy, paint);
    }
}