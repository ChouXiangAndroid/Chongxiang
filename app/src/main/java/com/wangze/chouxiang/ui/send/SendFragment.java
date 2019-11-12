package com.wangze.chouxiang.ui.send;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import androidx.annotation.NonNull;


import com.wangze.chouxiang.DrawerActivity;
import com.wangze.chouxiang.R;
import com.wangze.chouxiang.sjx.AccessBaiduManager;
import com.wangze.chouxiang.sjx.Person;

import com.wangze.chouxiang.wangze.ImageViewFragment;

import java.util.ArrayList;


public class SendFragment extends ImageViewFragment
{
    /* ----------- View Item --------------*/
    private Button takePictureBtn;
    private Button openAlbum;
    private Button confirm;
    private ImageView showPhotoIv;
    private EditText userName;
    private EditText userInfo;

    /* ----------- List View for All person ----------- */
    private AlertDialog resultDialog;
    private ArrayList<Person> personList;
    private int errorCode;

    public ArrayList<Person> getPersonList()
    {
        return personList;
    }

    public void setPersonList(ArrayList<Person> personList)
    {
        this.personList = personList;
    }

    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }


    @Override
    public void setBitmapForIV()
    {
        showPhotoIv.setImageBitmap(bitmap);
    }

    public void checkField() throws IllegalArgumentException
    {
        // TODO may not to throw error
        if (userName.length() < 1 || userInfo.length() < 1)
            throw new IllegalArgumentException("user name and user info can't be empty");
        if (bitmap == null)
            throw new IllegalArgumentException("image can't be empty");
    }

    private void go()
    {
        checkField();
        final String uname = userName.getText().toString();
        final String uinform = userInfo.getText().toString();

        Thread toServer = new Thread(() -> AccessBaiduManager.sendPhotoToMultiRecognize(getActivity(), bitmap));

        toServer.start();
        try
        {
            toServer.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        String result = ((DrawerActivity) getActivity()).getRecognizeResult();
        AccessBaiduManager.handleResponseOfSearch(result, SendFragment.this);
        if (errorCode == AccessBaiduManager.CODE_SUCCESS)
        {
            // always get the first recognize
            if (personList.get(0).getScore() >= 80)
            {
                resultDialog.setMessage("此人信息已存在！");
                resultDialog.show();
            } else
            {

                Log.i("UF", "开始上传信息");
                Thread upLoad = new Thread(() -> AccessBaiduManager.uploadFaceInform(bitmap, uname, uinform));
                upLoad.start();
                try
                {
                    upLoad.join();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                resultDialog.setMessage("成功添加至信息库！");
                resultDialog.show();
            }
        } else
        {
            if (errorCode == AccessBaiduManager.CODE_UNKNOWN)
            {
                resultDialog.setMessage("未识别到人脸");
                resultDialog.show();
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        System.out.println("开始创建SendFragment");
        View root = inflater.inflate(R.layout.fragment_send, container, false);

        takePictureBtn = root.findViewById(R.id.up_take_picture);
        openAlbum = root.findViewById(R.id.up_open_album);
        confirm = root.findViewById(R.id.up_confirm);
        showPhotoIv = root.findViewById(R.id.rf_showPhotoIv);
        userName = root.findViewById(R.id.up_userName);
        userInfo = root.findViewById(R.id.up_email);

        takePictureBtn.setOnClickListener(v -> takePicture());
        openAlbum.setOnClickListener(v -> openAlbum());
        confirm.setOnClickListener(v -> go());
        resultDialog = new AlertDialog.Builder(getContext())
                .setTitle("错误")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("确定", (dialogInterface, i) -> {})
                .create();
        getFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, SendFragment.this, "SendFragment")
                //.addToBackStack(null)
                .commit();
        return root;
    }


    @Override
    public void onStart()
    {
        super.onStart();
        setBitmapForIV();
    }
}