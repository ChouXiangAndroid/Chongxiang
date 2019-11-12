package com.wangze.chouxiang.wangze;


public class User
{
    private int age, beauty;
    private String emotion;
    private String name;

    @Override
    public String toString()
    {
        return "User{" +
                "age=" + age +
                ", beauty=" + beauty +
                ", emotion='" + emotion + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public int getBeauty()
    {
        return beauty;
    }

    public void setBeauty(int beauty)
    {
        this.beauty = beauty;
    }

    public String getEmotion()
    {
        return emotion;
    }

    public void setEmotion(String emotion)
    {
        this.emotion = emotion;
    }

}