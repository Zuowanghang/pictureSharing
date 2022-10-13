package com.example.picturesharing.ui.dashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;

public class GetPhotoFromBitMap extends Fragment {

    private static Bitmap bitmap = null;

    // 将图片转为二进制
    public static byte[] photoToBit(Context context, Uri uri)
    {
        byte[] imageBytes = null;
        //将图片转换成二进制
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            imageBytes = baos.toByteArray();

        } catch (Exception e) {
        }
        return imageBytes;
    }

    // 将二进制图片转为图片
    public static Drawable bitToPhoto(byte[] picture)
    {
        bitmap = BitmapFactory.decodeByteArray(picture,0,picture.length,null);
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }
}
