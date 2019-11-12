package com.wangze.chouxiang.sjx;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.wangze.chouxiang.DrawerActivity;
import com.wangze.chouxiang.R;
import com.wangze.chouxiang.ui.gallery.GalleryFragment;

import java.util.ArrayList;

public class ShowRecognizeResultFragment extends Fragment
{
    private ListView personLv;
    private PersonAdapter personAdapter;
    private ArrayList<Person> personList;
    private RecognizeImageView showFaceIv;
    private int errorCode;
    private Bitmap bitmap;
    private AlertDialog resultDialog;

    private enum BestFitSize
    {
        Width(800F),
        Height(1000F);

        private final float value;
        public float getValue() {return value;}
        BestFitSize(float v) { value = v;}

    }

    public void setPersonList(ArrayList<Person> personList)
    {
        this.personList = personList;
    }

    public RecognizeImageView getShowFaceIv() { return showFaceIv; }

    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }


    private Matrix scaleFitForWidth()
    {
        float bestWidth = BestFitSize.Width.getValue();
        float bestHeight = bestWidth * ((float) bitmap.getHeight() / (float) bitmap.getWidth());
        Matrix scalar = new Matrix();
        scalar.postScale(bestHeight / (float) bitmap.getHeight(), bestWidth / (float) bitmap.getWidth());
        return scalar;
    }

    private Matrix scaleFitForHeight()
    {
        float bestHeight = BestFitSize.Height.getValue();
        float bestWidth = bestHeight * ((float) bitmap.getWidth() / (float) bitmap.getHeight());
        Matrix scalar = new Matrix();
        scalar.postScale(bestHeight / (float) bitmap.getHeight(), bestWidth / (float) bitmap.getWidth());
        return scalar;
    }

    private void scaleBitmap()
    {
        Matrix scalar ;
        if (bitmap.getWidth() > BestFitSize.Width.getValue())
        {
            scalar = scaleFitForWidth();
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), scalar, true);
        }
        if (bitmap.getHeight() > BestFitSize.Height.getValue())
            {
            scalar = scaleFitForHeight();
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), scalar, true);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_recognizeresult, container, false);
        personLv = root.findViewById(R.id.rrf_personLv);
        showFaceIv = root.findViewById(R.id.rrf_showFaceIv);

        resultDialog = new AlertDialog.Builder(getContext())
                .setTitle("错误")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("确定", (dialogInterface, i) -> getActivity().getSupportFragmentManager().popBackStack())
                .create();

        bitmap = ((GalleryFragment) getFragmentManager().findFragmentByTag("GalleryFragment")).getBitmap();
        return root;
    }


    @Override
    public void onStart()
    {
        super.onStart();
        scaleBitmap();
        showFaceIv.setBitmap(bitmap);

        Thread toServer = new Thread(() -> AccessBaiduManager.sendPhotoToMultiRecognize(getActivity(), bitmap));
        toServer.start();
        try
        {
            toServer.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        String res = ((DrawerActivity) getActivity()).getRecognizeResult();
        AccessBaiduManager.handleResponseOfSearch(res, this);

        if (errorCode == AccessBaiduManager.CODE_SUCCESS)
        {
            // just for safe,  the error code has been checked at handleResponseOfSearch
            personAdapter = new PersonAdapter(getContext(), R.layout.person_item, personList);
            personAdapter.setFaceBitmap(bitmap);
            personLv.setAdapter(personAdapter);
        }

    }

}
