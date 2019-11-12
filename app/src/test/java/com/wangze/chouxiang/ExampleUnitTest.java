package com.wangze.chouxiang;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Request.Builder;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleUnitTest
{
    @Test
    public void testT() throws Exception
    {
        String src = "E:\\file\\AndroidProj\\ChouXiang\\app\\src\\main\\java\\com\\wangze\\chouxiang";
        File newFile = new File(src, "ok.java");
        FileOutputStream os = new FileOutputStream(newFile);
        os.write(1);
        os.close();
    }
    @Test
    public void addition_isCorrect() throws Exception
    {
        String img = "E:\\file\\javaProj\\lab\\src\\main\\java\\d321.jpg";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(new File(img), MediaType.parse("image/jpeg"));
        RequestBody requestBody = new MultipartBody.Builder().
                        setType(MultipartBody.FORM).
                        addFormDataPart("image", "d123.jpg", body)
                        .build();

        client = new OkHttpClient.Builder().readTimeout(1, TimeUnit.MINUTES).build();
        Request request = new Request.Builder()
                .url("http://49.233.142.11:3000/api/posts/search")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response.code());
        if (response.isSuccessful())
            System.out.println(response.body().string());
        else
            System.out.println("fail");

    }
}