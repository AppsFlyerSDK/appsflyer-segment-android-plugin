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
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.Logger;
import com.segment.analytics.integrations.TrackPayload;

import static  org.mockito.Mockito.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

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

        reset(context,appsflyer);
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
        reset(appsFlyerLib,context);
        staticAppsFlyerLib.close();
    }

    @Test
    public void testAppsflyerIntegration_startAppsFlyer_nilFlow() throws Exception {
        MockedStatic<AppsFlyerLib> staticAppsFlyerLib = mockStatic(AppsFlyerLib.class);
        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
        staticAppsFlyerLib.when(AppsFlyerLib::getInstance).thenReturn(appsFlyerLib);
        AppsflyerIntegration.startAppsFlyer(null);
        verify(appsFlyerLib,never()).start(any());
        reset(appsFlyerLib);
        staticAppsFlyerLib.close();
    }

//    @MockitoSession(MockitoSessions.SINGLE_USE)
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
        reset(appsFlyerLib,analytics,app,AppsflyerIntegration.deepLinkListener);
        staticAppsFlyerLib.close();
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

//    @Test
//    public void testAppsflyerIntegration_onActivityCreated_nilFlow() throws Exception {
//        MockedStatic<AppsFlyerLib> staticAppsFlyerLib = mockStatic(AppsFlyerLib.class);
//        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
//        staticAppsFlyerLib.when(AppsFlyerLib::getInstance).thenReturn(appsFlyerLib);
//        AppsflyerIntegration.manualMode=false;
//        AppsflyerIntegration appsflyerIntegration = mock(AppsflyerIntegration.class);
//        appsflyerIntegration.onActivityCreated(mock(Activity.class), mock(Bundle.class));
//        verify(appsFlyerLib).start(any());
//    }

    @Test
    public void testAppsflyerIntegration_getUnderlyingInstance_happyFlow() throws Exception {
        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
        Logger logger = new Logger("test", Analytics.LogLevel.INFO);
        AppsflyerIntegration appsflyerIntegration = new AppsflyerIntegration(null,logger,appsFlyerLib,null);
        Assert.assertTrue(appsflyerIntegration.getUnderlyingInstance().equals(appsFlyerLib));
        reset(appsFlyerLib);
    }

    @Test
    public void testAppsflyerIntegration_identify_happyFlow() throws Exception {
        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
        Logger logger = spy(new Logger("test", Analytics.LogLevel.INFO));
        AppsflyerIntegration appsflyerIntegration = spy(new AppsflyerIntegration(null,logger,appsFlyerLib,null));
        IdentifyPayload identifyPayload = mock(IdentifyPayload.class);
        Traits traits = mock(Traits.class);
        when(identifyPayload.userId()).thenReturn("moris");
        when(identifyPayload.traits()).thenReturn(traits);
        when(traits.getString("currencyCode")).thenReturn("ILS");

        appsflyerIntegration.identify(identifyPayload);

        verify(logger, never()).verbose(any());

        Field customerUserIdField = AppsflyerIntegration.class.getDeclaredField("customerUserId");
        customerUserIdField.setAccessible(true);
        String customerUserIdInappsflyerIntegration = (String) customerUserIdField.get(appsflyerIntegration);
        Assert.assertTrue(customerUserIdInappsflyerIntegration.equals("moris"));

        Field currencyCodeField = AppsflyerIntegration.class.getDeclaredField("currencyCode");
        currencyCodeField.setAccessible(true);
        String currencyCodeInappsflyerIntegration = (String) currencyCodeField.get(appsflyerIntegration);
        Assert.assertTrue(currencyCodeInappsflyerIntegration.equals("ILS"));
        reset(appsFlyerLib,identifyPayload,traits);
    }

    @Test
    public void testAppsflyerIntegration_identify_nilflow() throws Exception {
        Logger logger = spy(new Logger("test", Analytics.LogLevel.INFO));
        AppsflyerIntegration appsflyerIntegration = spy(new AppsflyerIntegration(null,logger,null,null));
        IdentifyPayload identifyPayload = mock(IdentifyPayload.class);
        Traits traits = mock(Traits.class);
        when(identifyPayload.traits()).thenReturn(traits);

        appsflyerIntegration.identify(identifyPayload);

        verify(logger, times(1)).verbose("couldn't update 'Identify' attributes");
        reset(identifyPayload,traits);
    }

    @Test
    public void testAppsflyerIntegration_updateEndUserAttributes_happyflow() throws Exception {
        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
        Logger logger = spy(new Logger("test", Analytics.LogLevel.INFO));
        AppsflyerIntegration appsflyerIntegration = spy(new AppsflyerIntegration(null,logger,appsFlyerLib,null));
        Method updateEndUserAttributes = AppsflyerIntegration.class.getDeclaredMethod("updateEndUserAttributes");
        updateEndUserAttributes.setAccessible(true);

        Field customerUserIdField = AppsflyerIntegration.class.getDeclaredField("customerUserId");
        customerUserIdField.setAccessible(true);
        customerUserIdField.set(appsflyerIntegration,"Moris");
        Field currencyCodeField = AppsflyerIntegration.class.getDeclaredField("currencyCode");
        currencyCodeField.setAccessible(true);
        currencyCodeField.set(appsflyerIntegration, "ILS");

        updateEndUserAttributes.invoke(appsflyerIntegration);

        verify(logger, times(1)).verbose("appsflyer.setCustomerUserId(%s)", "Moris");
        verify(logger, times(1)).verbose("appsflyer.setCurrencyCode(%s)", "ILS");
        verify(logger, times(1)).verbose("appsflyer.setDebugLog(%s)", true);
        reset(appsFlyerLib);
    }

    @Test
    public void testAppsflyerIntegration_track_happyflow() throws Exception {
        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
        Logger logger = spy(new Logger("test", Analytics.LogLevel.INFO));
        AppsflyerIntegration appsflyerIntegration = spy(new AppsflyerIntegration(null,logger,appsFlyerLib,null));
        TrackPayload trackPayload = mock(TrackPayload.class);
        String event = "event";
        Properties properties= mock(Properties.class);
        Map<String, Object> afProperties = mock(Map.class);
        MockedStatic<com.segment.analytics.internal.Utils> staticUtils = mockStatic(com.segment.analytics.internal.Utils.class);

        when(trackPayload.event()).thenReturn(event);
        when(trackPayload.properties()).thenReturn(properties);
        staticUtils.when(()->com.segment.analytics.internal.Utils.transform(any(),any())).thenReturn(afProperties);

        appsflyerIntegration.track(trackPayload);

        Field contextField = AppsflyerIntegration.class.getDeclaredField("context");
        contextField.setAccessible(true);
        Context contextInAppsflyerIntegration = (Context) contextField.get(appsflyerIntegration);

        verify(appsFlyerLib, times(1)).logEvent(contextInAppsflyerIntegration,event,afProperties);
        verify(logger, times(1)).verbose("appsflyer.logEvent(context, %s, %s)", event, properties);
        reset(appsFlyerLib,trackPayload,properties,afProperties);
        staticUtils.close();
    }
}