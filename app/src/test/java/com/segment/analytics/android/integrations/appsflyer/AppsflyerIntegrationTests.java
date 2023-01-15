package com.segment.analytics.android.integrations.appsflyer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.MockedStatic;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.segment.analytics.Analytics;
import com.segment.analytics.integrations.Logger;
import static  org.mockito.Mockito.*;

import java.lang.reflect.Field;


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(AndroidJUnit4.class)
public class AppsflyerIntegrationTests {
    @Test
    public void testAppsflyerIntegration_ctor_happyFlow() throws Exception {
        Context context = mock(Context.class);
        Logger logger = new Logger("test", Analytics.LogLevel.INFO);
        AppsFlyerLib appsflyer = mock(AppsFlyerLib.class);
        String appsflyerDevKey = "appsflyerDevKey";
        boolean isDebug = logger.logLevel != Analytics.LogLevel.NONE;
        AppsflyerIntegration appsflyerIntegration = new AppsflyerIntegration(context,logger,appsflyer,appsflyerDevKey);
        Assert.assertTrue(appsflyerIntegration.isDebug == isDebug);
        Assert.assertTrue(appsflyerIntegration.appsFlyerDevKey == appsflyerDevKey);
        Assert.assertTrue(appsflyerIntegration.appsflyer == appsflyer);
        Assert.assertTrue(appsflyerIntegration.logger == logger);
        Field field = AppsflyerIntegration.class.getDeclaredField("context");
        field.setAccessible(true);
        Context contextInappsflyerIntegration = (Context) field.get(appsflyerIntegration);
        Assert.assertTrue(contextInappsflyerIntegration == context);
//        checking the static clause
        Assert.assertTrue(appsflyerIntegration.MAPPER.get("revenue")== AFInAppEventParameterName.REVENUE);
        Assert.assertTrue(appsflyerIntegration.MAPPER.get("currency")== AFInAppEventParameterName.CURRENCY);
    }

//    @Test
//    public void testAppsflyerIntegration_ctor_nullFlow() throws Exception {
//        Context context = null;
//        Logger logger = null;
//        AppsFlyerLib appsflyer = null;
//        String appsflyerDevKey = null;
////        the line below is a problem that needs to be sorted.
////        boolean isDebug = logger.logLevel != Analytics.LogLevel.NONE;
//        AppsflyerIntegration appsflyerIntegration = new AppsflyerIntegration(context,logger,appsflyer,appsflyerDevKey);
////        Assert.assertTrue(appsflyerIntegration.isDebug == isDebug);
//        Assert.assertTrue(appsflyerIntegration.appsFlyerDevKey == appsflyerDevKey);
//        Assert.assertTrue(appsflyerIntegration.appsflyer == appsflyer);
//        Assert.assertTrue(appsflyerIntegration.logger == logger);
//        Field field = AppsflyerIntegration.class.getDeclaredField("context");
//        field.setAccessible(true);
//        Context contextInappsflyerIntegration = (Context) field.get(appsflyerIntegration);
//        Assert.assertTrue(contextInappsflyerIntegration == context);
//    }
    //  the issebug line in the ctor is a problem needs to be sorted.

    @Test
    public void testAppsflyerIntegration_setManualMode_happyFlow() throws Exception {
        Assert.assertTrue(AppsflyerIntegration.manualMode==false);
        AppsflyerIntegration.setManualMode(true);
        Assert.assertTrue(AppsflyerIntegration.manualMode==true);
        AppsflyerIntegration.setManualMode(false);
        Assert.assertTrue(AppsflyerIntegration.manualMode==false);
    }

    @Test
    public void testAppsflyerIntegration_startAppsFlyer_happyFlow() throws Exception {
        MockedStatic<AppsFlyerLib> staticAppsFlyerLib = mockStatic(AppsFlyerLib.class);
        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
        staticAppsFlyerLib.when(AppsFlyerLib::getInstance).thenReturn(appsFlyerLib);
        Context context = mock(Context.class);
        AppsflyerIntegration.startAppsFlyer(context);
        verify(appsFlyerLib).start(context);
    }

    @Test
    public void testAppsflyerIntegration_startAppsFlyer_nilFlow() throws Exception {
        MockedStatic<AppsFlyerLib> staticAppsFlyerLib = mockStatic(AppsFlyerLib.class);
        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
        staticAppsFlyerLib.when(AppsFlyerLib::getInstance).thenReturn(appsFlyerLib);
        AppsflyerIntegration.startAppsFlyer(null);
        verify(appsFlyerLib,never()).start(any());
    }


//    @Test
//    public void testAppsflyerIntegration_getUnderlyingInstance_happyFlow() throws Exception {
//        Assert.assertTrue(AppsflyerIntegration);
//    }

}