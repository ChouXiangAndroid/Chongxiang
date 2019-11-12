package com.wangze.chouxiang;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest
{
    @Test
    public void useAppContext() throws Exception
    {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.wangze.chouxiang", appContext.getPackageName());


    }
}
