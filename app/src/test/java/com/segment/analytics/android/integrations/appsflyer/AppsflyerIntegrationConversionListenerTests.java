package com.segment.analytics.android.integrations.appsflyer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.ValueMap;
import static  org.mockito.Mockito.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class AppsflyerIntegrationConversionListenerTests {

    @Test
    public void testAppsflyerIntegration_ConversionListener_ctor_happyFlow() {
        Analytics analytics = mock(Analytics.class);
        AppsflyerIntegration.ConversionListener conversionListener = new AppsflyerIntegration.ConversionListener(analytics);

        Assert.assertEquals(conversionListener.analytics, analytics);

        reset(analytics);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_ctor_nullFlow() {
        Analytics analytics = null;
        AppsflyerIntegration.ConversionListener conversionListener = new AppsflyerIntegration.ConversionListener(analytics);

        Assert.assertEquals(conversionListener.analytics, analytics);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_onConversionDataSuccess_happyFlow() {
        //I want just to check the conversionListener gets the map.
        AppsflyerIntegration.conversionListener = mock(AppsflyerIntegration.ExternalAppsFlyerConversionListener.class);
        Analytics analytics = mock(Analytics.class);
        Map<String, Object> conversionData =  new ValueMap();
        Application app = mock(Application.class);
        Context context = mock(Context.class);
        SharedPreferences sharedPreferences = mock(SharedPreferences.class);
        when(analytics.getApplication()).thenReturn(app);
        when(app.getApplicationContext()).thenReturn(context);
        when(context.getSharedPreferences("appsflyer-segment-data",0)).thenReturn(sharedPreferences);
        when(sharedPreferences.getBoolean("AF_onConversion_Data",false)).thenReturn(true);
        AppsflyerIntegration.ConversionListener conversionListener = new AppsflyerIntegration.ConversionListener(analytics);

        conversionListener.onConversionDataSuccess(conversionData);

        verify(AppsflyerIntegration.conversionListener).onConversionDataSuccess(conversionData);

        reset(AppsflyerIntegration.conversionListener,analytics,app,context,sharedPreferences);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_onAttributionFailure_happyFlow() {
        AppsflyerIntegration.conversionListener = mock(AppsflyerIntegration.ExternalAppsFlyerConversionListener.class);
        Analytics analytics = Mockito.mock(Analytics.class);
        AppsflyerIntegration.ConversionListener conversionListener = new AppsflyerIntegration.ConversionListener(analytics);
        String errorMsg = "error - test";

        conversionListener.onAttributionFailure(errorMsg);

        verify(AppsflyerIntegration.conversionListener,times(1)).onAttributionFailure(errorMsg);

        reset(analytics,AppsflyerIntegration.conversionListener);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_onAttributionFailure_nullFlow() {
        AppsflyerIntegration.conversionListener = mock(AppsflyerIntegration.ExternalAppsFlyerConversionListener.class);
        Analytics analytics = Mockito.mock(Analytics.class);
        AppsflyerIntegration.ConversionListener conversionListener = new AppsflyerIntegration.ConversionListener(analytics);
        String errorMsg = null;
        conversionListener.onAttributionFailure(errorMsg);
        verify(AppsflyerIntegration.conversionListener,times(1)).onAttributionFailure(null);

        reset(analytics,AppsflyerIntegration.conversionListener);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_trackInstallAttributed_happyFlow() {
        Analytics analytics =mock(Analytics.class);
        Map<String, Object> attributionData = new HashMap<>();
        attributionData.put("media_source", "media_source_moris");
        attributionData.put("campaign", "campaign_moris");
        attributionData.put("adgroup", "adgroup_moris");

        Map<String, Object> campaign = new ValueMap()
                .putValue("source", attributionData.get("media_source"))
                .putValue("name", attributionData.get("campaign"))
                .putValue("ad_group", attributionData.get("adgroup"));
        Properties properties = new Properties().putValue("provider", "AppsFlyer");
        properties.putAll(attributionData);
        properties.remove("media_source");
        properties.remove("adgroup");
        properties.putValue("campaign", campaign);
        AppsflyerIntegration.ConversionListener conversionListener = new AppsflyerIntegration.ConversionListener(analytics);

        conversionListener.trackInstallAttributed(attributionData);

        verify(analytics,times(1)).track("Install Attributed", properties);

        reset(analytics);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_trackInstallAttributed_negativeFlow() {
        Analytics analytics =mock(Analytics.class);
        Map<String, Object> attributionData = new HashMap<String, Object>();
        Map<String, Object> campaign = new ValueMap() //
                .putValue("source", "")
                .putValue("name", "")
                .putValue("ad_group", "");
        Properties properties = new Properties().putValue("provider", "AppsFlyer");
        properties.putAll(attributionData);
        properties.remove("media_source");
        properties.remove("adgroup");
        properties.putValue("campaign", campaign);
        AppsflyerIntegration.ConversionListener conversionListener = new AppsflyerIntegration.ConversionListener(analytics);
        conversionListener.trackInstallAttributed(attributionData);

        verify(analytics,times(1)).track("Install Attributed", properties);

        reset(analytics);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_getFlag_happyFlow() throws Exception {
        String key="key";
        Analytics analytics = mock(Analytics.class);
        Application app = mock(Application.class);
        Context context = mock(Context.class);
        SharedPreferences sharedPreferences = mock(SharedPreferences.class);
        when(analytics.getApplication()).thenReturn(app);
        when(app.getApplicationContext()).thenReturn(context);
        when(context.getSharedPreferences("appsflyer-segment-data",0)).thenReturn(sharedPreferences);
        when(sharedPreferences.getBoolean(key,false)).thenReturn(true);
        AppsflyerIntegration.ConversionListener conversionListener = new AppsflyerIntegration.ConversionListener(analytics);

        boolean resBoolean = (Boolean) TestHelper.getPrivateMethodForObjectReadyToInvoke("getFlag",String.class).invoke(conversionListener,key);

        Assert.assertTrue(resBoolean);

        reset(analytics,app,context,sharedPreferences);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_setFlag_happyFlow() throws Exception {
        String key="key";
        boolean value=true;
        Analytics analytics = mock(Analytics.class);
        Application app = mock(Application.class);
        Context context = mock(Context.class);
        SharedPreferences sharedPreferences = mock(SharedPreferences.class);
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(analytics.getApplication()).thenReturn(app);
        when(app.getApplicationContext()).thenReturn(context);
        when(context.getSharedPreferences("appsflyer-segment-data",0)).thenReturn(sharedPreferences);
        when(sharedPreferences.edit()).thenReturn(editor);
        AppsflyerIntegration.ConversionListener conversionListener = new AppsflyerIntegration.ConversionListener(analytics);

        TestHelper.getPrivateMethodForObjectReadyToInvoke("setFlag",String.class,boolean.class).invoke(conversionListener,key,value);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD){
            verify(editor,times(1)).apply();
        }
        else{
            verify(editor,times(1)).commit();
        }

        reset(analytics,app,context,sharedPreferences);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_getContext_happyFlow() throws Exception {
        Analytics analytics = mock(Analytics.class);
        Application app = mock(Application.class);
        Context context = mock(Context.class);
        when(analytics.getApplication()).thenReturn(app);
        when(app.getApplicationContext()).thenReturn(context);
        AppsflyerIntegration.ConversionListener conversionListener = new AppsflyerIntegration.ConversionListener(analytics);

        Context resContext = (Context) TestHelper.getPrivateMethodForObjectReadyToInvoke("getContext").invoke(conversionListener);

        Assert.assertEquals(resContext, context);

        reset(analytics,app,context);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_getContext_nullFlow() throws Exception{
        Analytics analytics = mock(Analytics.class);
        Application app = mock(Application.class);
        Context context = null;
        when(analytics.getApplication()).thenReturn(app);
        when(app.getApplicationContext()).thenReturn(context);
        AppsflyerIntegration.ConversionListener conversionListener = new AppsflyerIntegration.ConversionListener(analytics);

        Context resContext = (Context) TestHelper.getPrivateMethodForObjectReadyToInvoke("getContext").invoke(conversionListener);

        Assert.assertEquals(resContext, context);

        reset(analytics,app);
    }
}
