package com.wangze.chouxiang.sjx;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.wangze.chouxiang.DrawerActivity;
import com.wangze.chouxiang.ui.gallery.GalleryFragment;
import com.wangze.chouxiang.ui.send.SendFragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

//处理照片请求类
public final class PhotoManager
{
    private static File outImg;
    public static int requestCode;
    public static final String PM_TEMP_IMAGE_FILENAME = "temp.jpg";
    public static final String PM_OPEN_CAMERA_ACTION = "android.media.action.IMAGE_CAPTURE";
    public static final String PM_FILE_PROVIDER_AUTHORITY = "com.wangze.chouxiang.FileProvider"; // 'wangze' should be changed to project's name

    public static final int REQ_TAKE_PICTURE = 1;

    @Deprecated
    public static void takePhoto(Activity activity, Fragment fragment)
    {
        //删除并创建临时文件，用于保存拍照后的照片
        //android 6以后，写Sdcard是危险权限，需要运行时申请，但此处使用的是"项目关联目录"，无需！

        outImg = new File(activity.getExternalCacheDir(), PM_TEMP_IMAGE_FILENAME);
        try
        {
            boolean success = false;
            if (outImg.exists())
                success = outImg.delete() && outImg.createNewFile();
            if (!success)
                throw new IllegalStateException("Temp cache file operation fail. Can't delete out-of-time cache or create new file");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        Uri imgUri = FileProvider.getUriForFile(activity, PM_FILE_PROVIDER_AUTHORITY, outImg);

        Class<? extends Fragment> fgClass = fragment.getClass();

        // for debug
        Log.d("TAG", "takePhoto: fragment = " + fgClass.getName() + "\ttag=" + fragment.getTag());

        if (fgClass == GalleryFragment.class)
        {
            ((GalleryFragment) fragment).setOutImg(outImg);
            requestCode = 1;
        } else if (fgClass == SendFragment.class)
        {
            ((SendFragment) fragment).setOutImg(outImg);
            requestCode = 2;
        }

        //利用actionName和Extra,启动相机Activity
        Intent intent = new Intent(PM_OPEN_CAMERA_ACTION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Deprecated
    public static Bitmap handleTakenPhoto(Activity activity, File outImg) throws FileNotFoundException
    {
        Uri imgUri = FileProvider.getUriForFile(activity, PM_FILE_PROVIDER_AUTHORITY, outImg);
        Bitmap map = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imgUri));
        OutputStream out = new FileOutputStream(outImg);
        map.compress(Bitmap.CompressFormat.JPEG, 10, out);
        map = BitmapFactory.decodeStream(new FileInputStream(outImg));
        Log.i("Rec2", "image size = " + map.getByteCount());
        return map;
    }

    public static String ChangeBitmapToBase64(Bitmap bitmap)
    {
        String result = null;
        ByteArrayOutputStream baos = null;
        try
        {
            if (bitmap != null)
            {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (baos != null)
                {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;

    }

    public static Bitmap compressScale(Bitmap image)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 1024)
        {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        float hh = 512f;
        float ww = 512f;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww)
        {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh)
        { // 如果高度高的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be; // 设置缩放比例
        // newOpts.inPreferredConfig = Config.RGB_565;//降低图片从ARGB888到RGB565
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
        //return bitmap;
    }


    public static Bitmap compressImage(Bitmap image)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 100)
        { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
