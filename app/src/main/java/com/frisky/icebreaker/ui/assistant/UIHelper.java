package com.frisky.icebreaker.ui.assistant;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.frisky.icebreaker.R;

public class UIHelper {
    private static final UIHelper ourInstance = new UIHelper();

    public static UIHelper getInstance() {
        return ourInstance;
    }

    private UIHelper() {
    }

    public int getRatingBadgeBackground(double rating) {
        if (rating >= 4.0) {
            return R.drawable.pub_rating_very_high;
        }
        else if (rating >= 3.5) {
            return R.drawable.pub_rating_high;
        }
        else if (rating >= 2.5) {
            return R.drawable.pub_rating_low;
        }
        else {
            return R.drawable.pub_rating_very_low;
        }
    }

    public int getRatingBadgeColor(double rating) {
        if (rating >= 4.0) {
            return R.color.rating_very_high;
        }
        else if (rating >= 3.5) {
            return R.color.rating_high;
        }
        else if (rating >= 2.5) {
            return R.color.rating_low;
        }
        else {
            return R.color.rating_very_low;
        }
    }

    public Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
}
