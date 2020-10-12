package com.kontranik.koreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap getBitmap(Context c, ImageEnum imageEnum, String path) {
        byte[] b = getBitmapData(c, imageEnum, path );
        if ( b != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);

            int width = 100;
            int height = 100;

            Bitmap background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            float originalWidth = bmp.getWidth();
            float originalHeight = bmp.getHeight();

            Canvas canvas = new Canvas(background);

            float scale = width / originalWidth;

            float xTranslation = 0.0f;
            float yTranslation = (height - originalHeight * scale) / 2.0f;

            Matrix transformation = new Matrix();
            transformation.postTranslate(xTranslation, yTranslation);
            transformation.preScale(scale, scale);

            Paint paint = new Paint();
            paint.setFilterBitmap(true);

            canvas.drawBitmap(bmp, transformation, paint);

            return background;
        }
        return  null;
    }

    private static byte[] getBitmapData(Context c, ImageEnum imageEnum, String path) {
        Drawable d = null;
        byte[] cover = null;
        switch (imageEnum) {
            case Parent:
                d = c.getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
                break;
            case SD:
                d = c.getResources().getDrawable(R.drawable.ic_sd_card_black_24dp);
                break;
            case Dir:
                d = c.getResources().getDrawable(R.drawable.ic_folder_black_24dp);
                break;
            case Epub:
                cover = EpubHelper.getCover(path);
                break;
            default:
                d = c.getResources().getDrawable(R.drawable.ic_book_black_24dp);
                break;
        }

        if ( d == null && cover == null) {
            d = c.getResources().getDrawable(R.drawable.ic_book_black_24dp);
        }
        if ( d != null ) {
            Bitmap icon = ImageUtils.drawableToBitmap(d);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (icon != null) {
                icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
                cover = stream.toByteArray();
            }
        }

        return cover;
    }


}
