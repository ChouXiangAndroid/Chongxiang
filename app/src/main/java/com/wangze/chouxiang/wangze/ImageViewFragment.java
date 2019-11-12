package com.wangze.chouxiang.wangze;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.wangze.chouxiang.R;
import com.wangze.chouxiang.sjx.PhotoManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public abstract class ImageViewFragment extends Fragment
{
    private File outImg;
    public Bitmap bitmap;

    // the prefix PM means PhotoManager from sjx.PhotoManger
    public static final String PM_TEMP_IMAGE_FILENAME = "temp.jpg";
    public static final String PM_OPEN_CAMERA_ACTION = "android.media.action.IMAGE_CAPTURE";
    public static final String PM_OPEN_ALBUM_ACTION = "android.intent.action.GET_CONTENT";
    public static final String PM_FILE_PROVIDER_AUTHORITY = "com.wangze.chouxiang.FileProvider"; // 'wangz' should be changed
    public static final int IVF_REQ_TAKE_PICTURE = 1;
    public static final int IVF_REQ_OPEN_ALBUM = 2;
    public static final int IVF_REQ_ALBUM_PERMISSION = 3;

    private void handleChoosePhoto(Intent intent)
    {
        assert getActivity() != null;
        assert intent.getData() != null;

        byte[] fileBuff;
        Cursor cursor;
        Uri uri = intent.getData();
        ContentResolver contentResolver = getActivity().getContentResolver();
        cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor.moveToFirst())
        {
            try (InputStream inputStream = contentResolver.openInputStream(uri))
            {
                fileBuff = convertToBytes(inputStream);
                bitmap = BitmapFactory.decodeByteArray(fileBuff, 0, fileBuff.length);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        cursor.close();
    }

    private byte[] convertToBytes(InputStream inputStream) throws Exception
    {
        byte[] buf = new byte[1024];
        int len;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            while ((len = inputStream.read(buf)) > 0)
                out.write(buf, 0, len);
            return out.toByteArray();
        }
    }

    public void openAlbum()
    {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};  //存放请求哪种权限
        Activity activity = getActivity();
        assert activity != null;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), permissions, IVF_REQ_ALBUM_PERMISSION);
        } else
        {
            Intent intent = new Intent(PM_OPEN_ALBUM_ACTION);
            //通过Intent来启动Activity,此处为获取内容的action
            intent.setType("image/*");  //获取内容为图片
            startActivityForResult(intent, IVF_REQ_OPEN_ALBUM);
        }
    }

    public void takePicture()
    {
        assert getActivity() != null;
        Activity activity = getActivity();
        File tempCachedImgOutFile = new File(activity.getExternalCacheDir(), PM_TEMP_IMAGE_FILENAME);
        try
        {
            boolean success = false;
            if (tempCachedImgOutFile.exists())
                success = tempCachedImgOutFile.delete() && tempCachedImgOutFile.createNewFile();
            if (!success)
                throw new IllegalStateException("Temp cache file operation fail. Can't delete out-of-time cache or create new file");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        Uri imgUri = FileProvider.getUriForFile(activity, PM_FILE_PROVIDER_AUTHORITY, tempCachedImgOutFile);
        Intent intent = new Intent(PM_OPEN_CAMERA_ACTION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, IVF_REQ_TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
        {
            return;
        }

        switch (requestCode)
        {
            case IVF_REQ_TAKE_PICTURE:
                try
                {
                    Activity activity = getActivity();
                    assert activity!=null; // just for suppressing the warning.
                    File tempCachedImgOutFile = new File(activity.getExternalCacheDir(), PM_TEMP_IMAGE_FILENAME);
                    Uri imgUri = FileProvider.getUriForFile(activity, PM_FILE_PROVIDER_AUTHORITY, tempCachedImgOutFile);
                    bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imgUri));
                } catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                break;
            case IVF_REQ_OPEN_ALBUM:
                assert data != null;
                handleChoosePhoto(data);
                break;
            // other case may delay to subclass's onActivityResult,
            // so there not need to define the default behave.
        }

    }

    // this is a hook for subclass to set its showImageIV field
    public abstract void setBitmapForIV();


    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public void setOutImg(File outImg)
    {
        this.outImg = outImg;
    }
}
