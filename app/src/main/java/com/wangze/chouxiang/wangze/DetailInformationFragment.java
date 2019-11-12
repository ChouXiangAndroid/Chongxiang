package com.wangze.chouxiang.wangze;


import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangze.chouxiang.DrawerActivity;
import com.wangze.chouxiang.R;
import com.wangze.chouxiang.sjx.AccessBaiduManager;
import com.wangze.chouxiang.sjx.Person;
import com.wangze.chouxiang.wangze.User;


public class DetailInformationFragment extends Fragment
{
    private Bitmap bitmap;
    private Person theUserFace;
    private TextView age, beauty, emo ,userName;
    private ImageView showFaceIv;
    public DetailInformationFragment()
    {
        // Required empty public constructor
    }


    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }

    public void setTheUserFace(Person theUserFace) { this.theUserFace = theUserFace; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_detect_detail, container, false);;
        age = root.findViewById(R.id.rrdf_user_age);
        beauty = root.findViewById(R.id.rrdf_user_beauty);
        emo = root.findViewById(R.id.rrdf_user_emotion);
        userName = root.findViewById(R.id.rrdf_user_name);
        showFaceIv = root.findViewById(R.id.rrdf_showFaceIv);
        return root;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Thread thread = new Thread(()->AccessBaiduManager.sendFaceForMoreInformation(getActivity(), bitmap));
        thread.start();
        try
        {
            thread.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        String res = ((DrawerActivity) getActivity()).getRecognizeResult();
        AccessBaiduManager.handleResponseOfDetect(res, this, theUserFace);
        showFaceIv.setImageBitmap(bitmap);
        userName.setText(theUserFace.getName());
        age.setText(String.valueOf(theUserFace.getAge()));
        beauty.setText(String.valueOf(theUserFace.getBeauty()));
        emo.setText(theUserFace.getEmotion());
    }
}
