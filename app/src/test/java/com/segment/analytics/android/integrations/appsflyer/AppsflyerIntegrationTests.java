package com.segment.analytics.android.integrations.appsflyer;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.internal.platform.app.ActivityInvoker;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.segment.analytics.Analytics;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.Logger;
import static  org.mockito.Mockito.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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

    @Test
    public void testAppsflyerIntegration_FACTORYCreate_happyFlow() throws Exception {
        MockedStatic<AppsFlyerLib> staticAppsFlyerLib = mockStatic(AppsFlyerLib.class);
        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
        staticAppsFlyerLib.when(AppsFlyerLib::getInstance).thenReturn(appsFlyerLib);
        Analytics analytics = mock(Analytics.class);
        ValueMap settings = new ValueMap();
        settings.put("appsFlyerDevKey" , "devKey");
        settings.put("trackAttributionData" , true);
        Logger logger = new Logger("test", Analytics.LogLevel.INFO);
        Mockito.when(analytics.logger("AppsFlyer")).thenReturn(logger);
        Application app = mock(Application.class);
        Mockito.when(analytics.getApplication()).thenReturn(app);
        AppsflyerIntegration.deepLinkListener = mock(AppsflyerIntegration.ExternalDeepLinkListener.class);

        Integration<AppsFlyerLib> integration= (Integration<AppsFlyerLib>) AppsflyerIntegration.FACTORY.create(settings,analytics);
        verify(appsFlyerLib).setDebugLog(logger.logLevel!=Analytics.LogLevel.NONE);
        ArgumentCaptor<AppsflyerIntegration.ConversionListener> captorListener = ArgumentCaptor.forClass(AppsflyerIntegration.ConversionListener.class);
        ArgumentCaptor<String> captorDevKey = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Context> captorContext = ArgumentCaptor.forClass(Context.class);
        verify(appsFlyerLib).init(captorDevKey.capture(), captorListener.capture() , captorContext.capture());
        Assert.assertTrue(captorListener.getValue()!=null);
        Assert.assertTrue(captorListener.getValue() instanceof AppsflyerIntegration.ConversionListener);
        Assert.assertTrue(captorDevKey.getValue() == settings.getString("appsFlyerDevKey"));
        Assert.assertTrue(captorContext.getValue() == app.getApplicationContext());
        verify(appsFlyerLib).subscribeForDeepLink(AppsflyerIntegration.deepLinkListener);
    }

//    @Test
//    public void testAppsflyerIntegration_FACTORYCreate_nilFlow() throws Exception {
//        MockedStatic<AppsFlyerLib> staticAppsFlyerLib = mockStatic(AppsFlyerLib.class);
//        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
//        staticAppsFlyerLib.when(AppsFlyerLib::getInstance).thenReturn(appsFlyerLib);
//        Analytics analytics = mock(Analytics.class);
//        ValueMap settings = new ValueMap();
//        settings.put("appsFlyerDevKey_wrong" , "devKey");
//        settings.put("trackAttributionData_wrong" , true);
//        Logger logger = new Logger("test", Analytics.LogLevel.INFO);
//        Mockito.when(analytics.logger("AppsFlyer")).thenReturn(logger);
//        Application app = mock(Application.class);
//        Mockito.when(analytics.getApplication()).thenReturn(app);
//        AppsflyerIntegration.deepLinkListener = mock(AppsflyerIntegration.ExternalDeepLinkListener.class);
//
//        Integration<AppsFlyerLib> integration =
//                (Integration<AppsFlyerLib>) AppsflyerIntegration.FACTORY.create(settings,analytics);
//        verify(appsFlyerLib).setDebugLog(logger.logLevel!=Analytics.LogLevel.NONE);
//        ArgumentCaptor<AppsflyerIntegration.ConversionListener> captorListener = ArgumentCaptor.forClass(AppsflyerIntegration.ConversionListener.class);
//        ArgumentCaptor<String> captorDevKey = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<Context> captorContext = ArgumentCaptor.forClass(Context.class);
//        verify(appsFlyerLib).init(captorDevKey.capture(), captorListener.capture() , captorContext.capture());
//        Assert.assertTrue(captorListener.getValue()!=null);
//        Assert.assertTrue(captorListener.getValue() instanceof AppsflyerIntegration.ConversionListener);
//        Assert.assertTrue(captorDevKey.getValue() == settings.getString("appsFlyerDevKey"));
//        Assert.assertTrue(captorContext.getValue() == app.getApplicationContext());
//        verify(appsFlyerLib).subscribeForDeepLink(AppsflyerIntegration.deepLinkListener);

    @Test
    public void testAppsflyerIntegration_FACTORYKEY_happyFlow() throws Exception {
            Assert.assertTrue(AppsflyerIntegration.FACTORY.key().equals("AppsFlyer"));
    }

    @Test
    public void testAppsflyerIntegration_onActivityCreated_nilFlow() throws Exception {
        MockedStatic<AppsFlyerLib> staticAppsFlyerLib = mockStatic(AppsFlyerLib.class);
        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
        staticAppsFlyerLib.when(AppsFlyerLib::getInstance).thenReturn(appsFlyerLib);
        AppsflyerIntegration.manualMode=false;
        AppsflyerIntegration appsflyerIntegration = mock(AppsflyerIntegration.class);
        appsflyerIntegration.onActivityCreated(mock(Activity.class), mock(Bundle.class));
        verify(appsFlyerLib).start(any());
    }

}