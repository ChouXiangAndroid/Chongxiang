package com.wangze.chouxiang.ui.gallery;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.wangze.chouxiang.R;


import com.wangze.chouxiang.sjx.ShowRecognizeResultFragment;
import com.wangze.chouxiang.wangze.ImageViewFragment;


public class GalleryFragment extends ImageViewFragment
{

    Button openAlbum;
    Button go;
    Button takePhoto;
    ImageView showPhotoIv;

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    @Override
    public void setBitmapForIV()
    {
        // this bitmap is the member of ImageViewFragment
        showPhotoIv.setImageBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case IVF_REQ_ALBUM_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //判断若成功获取权限
                    openAlbum();
                } else
                    Toast.makeText(getContext(), "读相册的操作被拒绝", Toast.LENGTH_LONG).show();
            default:
                Log.e("TAG", "onRequestPermissionsResult: error request code " + requestCode);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {

        final View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        showPhotoIv = root.findViewById(R.id.rf_showPhotoIv);
        openAlbum = root.findViewById(R.id.rec_open);
        openAlbum.setOnClickListener(v -> openAlbum());
        takePhoto = root.findViewById(R.id.rec_make);
        takePhoto.setOnClickListener(v->takePicture());
        go = root.findViewById(R.id.rec_go);
        go.setOnClickListener(v->getFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, new ShowRecognizeResultFragment(), "ShowRecognizeResultFragment")
                .addToBackStack("upload")
                .commit());

        getFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, GalleryFragment.this, "GalleryFragment")
                //.addToBackStack("gallery")
                .commit();
        return root;
    }

    @Override
    public void onStart()
    {
        // set bitmap for this fragment
        setBitmapForIV();
        super.onStart();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        System.out.println("识别fragment分离");
    }
}