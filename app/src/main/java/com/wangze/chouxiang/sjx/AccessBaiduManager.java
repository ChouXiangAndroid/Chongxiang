package com.wangze.chouxiang.sjx;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.wangze.chouxiang.DrawerActivity;
import com.wangze.chouxiang.wangze.DetailInformationFragment;
import com.wangze.chouxiang.ui.send.SendFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccessBaiduManager
{
    private static Response response;
    private static int errorCode;
    private static ArrayList<Person> personList;
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_UNKNOWN = 222202;

    public static void sendPhotoToMultiRecognize(Activity activity, Bitmap bitmap)
    {
        OkHttpClient client = new OkHttpClient();
        String Base64Photo = PhotoManager.ChangeBitmapToBase64(bitmap);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("image", Base64Photo);
            obj.put("image_type", "BASE64");
            obj.put("group_id_list", "group1");
            obj.put("max_face_num", 10);
            obj.put("match_threshold", 10);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"), "" + obj.toString());

        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/multi-search?access_token=24.29062b34a7fc64f68465706de4b9a2a8.2592000.1575358784.282335-17603369")
                .post(requestBody)
                .build();

        try
        {
            response = client.newCall(request).execute();
            String res = response.body().string();
            ((DrawerActivity) activity).setRecognizeResult(res);
            System.out.println(res);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void sendFaceForMoreInformation(Activity activity, Bitmap bitmap)
    {
        OkHttpClient client = new OkHttpClient();
        String Base64Photo = PhotoManager.ChangeBitmapToBase64(bitmap);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("image", Base64Photo);
            obj.put("image_type", "BASE64");
            obj.put("group_id_list", "group1");
            obj.put("max_face_num", 1);
            obj.put("face_field", "age,beauty,emotion");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"), "" + obj.toString());
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/detect?access_token=24.29062b34a7fc64f68465706de4b9a2a8.2592000.1575358784.282335-17603369")
                .post(requestBody)
                .build();
        try
        {
            response = client.newCall(request).execute();
            String res = response.body().string();
            ((DrawerActivity) activity).setRecognizeResult(res);
            System.out.println(res);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void handleResponseOfDetect(String jsonData, Fragment fragment, Person theOne)
    {
        try
        {
            JSONObject jsonResult = new JSONObject(jsonData);
            errorCode = jsonResult.getInt("error_code");

            if (errorCode == CODE_SUCCESS)
            {
                JSONObject result = jsonResult.getJSONObject("result");
                JSONArray faceList = result.getJSONArray("face_list");
                int age = (int) faceList.getJSONObject(0).getDouble("age");
                int beauty = (int) faceList.getJSONObject(0).getDouble("beauty");
                JSONObject expression = faceList.getJSONObject(0).getJSONObject("emotion");
                String emotion = expression.getString("type");
                Person he = new Person();
                he.setAge(age);
                he.setBeauty(beauty);
                he.setEmotion(emotion);
                he.setName(theOne.getName());
                ((DetailInformationFragment) fragment).setTheUserFace(he);
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    public static void handleResponseOfSearch(String jsonData, Fragment fragment)
    {
        try
        {
            JSONObject object = new JSONObject(jsonData);
            errorCode = object.getInt("error_code");

            if (errorCode == CODE_SUCCESS)
            {
                JSONObject result = object.getJSONObject("result");
                JSONArray faceList = result.getJSONArray("face_list");

                int face_num = result.getInt("face_num");
                personList = new ArrayList<>(face_num);
                for (int idx = 0; idx < face_num; idx++)
                {
                    Person newPerson = new Person();
                    JSONObject location = faceList.getJSONObject(idx).getJSONObject("location");
                    newPerson.setHeight(location.getInt("height"));
                    newPerson.setWidth(location.getInt("width"));
                    newPerson.setLeft(location.getInt("left"));
                    newPerson.setTop(location.getInt("top"));
                    newPerson.setRotation(location.getInt("rotation"));
                    JSONArray userList = faceList.getJSONObject(idx).getJSONArray("user_list");
                    JSONObject p = userList.getJSONObject(0);
                    String str = p.getString("user_info");
                    String[] strs = str.split("////");
                    newPerson.setScore(p.getInt("score"));
                    newPerson.setId((short) idx);
                    if (p.getInt("score") < 80)
                    {
                        newPerson.setName("第" + newPerson.getId() + "张脸");
                        newPerson.setInfo("未检测到此人信息");
                    } else
                    {
                        newPerson.setName(strs[0]);
                        newPerson.setInfo(strs[1]);
                        newPerson.setScore(p.getInt("score"));
                    }
                    personList.add(newPerson);
                }
            }

            if (personList.size()!=0)
            {
                Collections.sort(personList, (p1, p2)-> p2.getScore()-p1.getScore());
            }
            switch (fragment.getTag())
            {
                case "ShowRecognizeResultFragment":
                    ((ShowRecognizeResultFragment) fragment).setPersonList(personList);
                    ((ShowRecognizeResultFragment) fragment).getShowFaceIv().setPersonList(personList);
                    ((ShowRecognizeResultFragment) fragment).setErrorCode(errorCode);
                    break;
                case "SendFragment":
                    ((SendFragment) fragment).setPersonList(personList);
                    ((SendFragment) fragment).setErrorCode(errorCode);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + fragment.getTag());
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static void uploadFaceInform(Bitmap bitmap, String uname, String uinform)
    {
        OkHttpClient client = new OkHttpClient();
        String Base64Photo = PhotoManager.ChangeBitmapToBase64(bitmap);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("image", Base64Photo);
            obj.put("image_type", "BASE64");
            obj.put("group_id", "group1");
            obj.put("user_id", "User" + System.currentTimeMillis());
            obj.put("user_info", uname + "////" + uinform);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"), "" + obj.toString());
        Log.i("BaiduManager", "正在上传人脸信息");

        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token=24.29062b34a7fc64f68465706de4b9a2a8.2592000.1575358784.282335-17603369")
                .post(requestBody)
                .build();

        try
        {
            Log.i("发送请求", "" + requestBody);
            response = client.newCall(request).execute();
            String res = response.body().string();
            System.out.println(res);
        } catch (IOException e)
        {
            e.printStackTrace();
        }


    }
}
