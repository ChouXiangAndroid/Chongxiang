package com.wangze.chouxiang.wangze;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.wangze.chouxiang.DrawerActivity;
import com.wangze.chouxiang.R;

import java.io.File;


/*
 *
 *   首页功能按钮静态工厂类，所有首页的功能按键均以枚举类提供单例模式
 *   用于首页Activity的onCreate时，加载进List，传递给Adapter
 * */
public class HomeBtnItemFactory
{
    public enum HomeBtnItem implements View.OnClickListener
    {
        CAMERA(R.mipmap.ic_camera, "相机")
                {
                    public void onClick(View v)
                    {
                        Toast.makeText(v.getContext(), "相机", Toast.LENGTH_SHORT).show();
                    }
                },
        UPLOAD(R.mipmap.ic_upload, "上传")
                {
                    public void onClick(View v)
                    {
                        Toast.makeText(v.getContext(), "上传", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(R.id.nav_upload);
                    }
                },
        OPEN(R.mipmap.ic_photoes, "相册")
                {
                    public void onClick(View v)
                    {
                        Toast.makeText(v.getContext(), "相册", Toast.LENGTH_SHORT).show();
                    }
                },
        SETTING(R.mipmap.ic_setting, "设置")
                {
                    public void onClick(View v)
                    {
                        Toast.makeText(v.getContext(), "设置", Toast.LENGTH_SHORT).show();
                    }
                };

        private final int imageSrc;
        private final String btnName;

        public int getImageSrc() { return imageSrc; }

        public String getBtnName() { return btnName; }

        HomeBtnItem(int res, String name)
        {
            imageSrc = res;
            btnName = name;
        }

        public HomeBtnItem getBtnItem() { return this; }

    }

    public static HomeBtnItem getBtnByName(HomeBtnItem buttonType)
    {
        switch (buttonType)
        {
            case SETTING:
                return HomeBtnItem.SETTING.getBtnItem();
            case OPEN:
                return HomeBtnItem.OPEN.getBtnItem();
            case CAMERA:
                return HomeBtnItem.CAMERA.getBtnItem();
            case UPLOAD:
                return HomeBtnItem.UPLOAD.getBtnItem();
            default:
                throw new IllegalArgumentException("buttonType " + buttonType + " have been implemented.");
        }
    }
}