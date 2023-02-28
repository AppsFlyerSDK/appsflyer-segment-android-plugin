package com.segment.analytics.android.integrations.appsflyer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import android.app.Application;
import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
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
import java.lang.reflect.Type;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class AppsflyerIntegrationTests {
    private TestHelper testHelper = new TestHelper();

    @Test
    public void testAppsflyerIntegration_ctor_happyFlow() throws Exception {
        Context context = mock(Context.class);
        Logger logger = new Logger("test", Analytics.LogLevel.INFO);
        AppsFlyerLib appsflyer = mock(AppsFlyerLib.class);
        String appsflyerDevKey = "appsflyerDevKey";
        boolean isDebug = logger.logLevel != Analytics.LogLevel.NONE;

        AppsflyerIntegration appsflyerIntegration = new AppsflyerIntegration(context,logger,appsflyer,appsflyerDevKey);
        Assert.assertEquals(appsflyerIntegration.isDebug , isDebug);
        Assert.assertEquals(appsflyerIntegration.appsFlyerDevKey, appsflyerDevKey);
        Assert.assertEquals(appsflyerIntegration.appsflyer, appsflyer);
        Assert.assertEquals(appsflyerIntegration.logger, logger);
        Context contextInappsflyerIntegration = (Context) TestHelper.getPrivateFieldForObject("context",AppsflyerIntegration.class,appsflyerIntegration);
        Assert.assertEquals(contextInappsflyerIntegration, context);
//        checking the static clause
        Assert.assertEquals(AppsflyerIntegration.MAPPER.get("revenue"), AFInAppEventParameterName.REVENUE);
        Assert.assertEquals(AppsflyerIntegration.MAPPER.get("currency"), AFInAppEventParameterName.CURRENCY);

        reset(context,appsflyer);
    }

    @Test
    public void testAppsflyerIntegration_setManualMode_happyFlow() {
        Assert.assertFalse(AppsflyerIntegration.manualMode);
        AppsflyerIntegration.setManualMode(true);
        Assert.assertTrue(AppsflyerIntegration.manualMode);
        AppsflyerIntegration.setManualMode(false);
        Assert.assertFalse(AppsflyerIntegration.manualMode);
    }

    @Test
    public void testAppsflyerIntegration_startAppsFlyer_happyFlow() {
        AppsFlyerLib appsFlyerLib = testHelper.mockAppsflyerLib();
        Context context = mock(Context.class);

        AppsflyerIntegration.startAppsFlyer(context);

        verify(appsFlyerLib).start(context);

        reset(appsFlyerLib,context);
        testHelper.closeMockAppsflyerLib();
    }

    @Test
    public void testAppsflyerIntegration_startAppsFlyer_nilFlow() {
        AppsFlyerLib appsFlyerLib = testHelper.mockAppsflyerLib();

        AppsflyerIntegration.startAppsFlyer(null);

        verify(appsFlyerLib,never()).start(any());

        reset(appsFlyerLib);
        testHelper.closeMockAppsflyerLib();
    }

    @Test
    public void testAppsflyerIntegration_FACTORYCreate_happyFlow() {
        AppsFlyerLib appsFlyerLib = testHelper.mockAppsflyerLib();
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
        Assert.assertNotNull(captorListener.getValue());
        Assert.assertEquals(captorDevKey.getValue(), settings.getString("appsFlyerDevKey"));
        Assert.assertEquals(captorContext.getValue(), app.getApplicationContext());
        verify(appsFlyerLib).subscribeForDeepLink(AppsflyerIntegration.deepLinkListener);

        reset(appsFlyerLib,analytics,app,AppsflyerIntegration.deepLinkListener);
        testHelper.closeMockAppsflyerLib();
    }

//need to check params values are null
//    @Test
//    public void testAppsflyerIntegration_FACTORYCreate_nilFlow() {
////        MockedStatic<AppsFlyerLib> staticAppsFlyerLib = mockStatic(AppsFlyerLib.class);
////        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
// //       staticAppsFlyerLib.when(AppsFlyerLib::getInstance).thenReturn(appsFlyerLib);
//        AppsFlyerLib appsFlyerLib = mockAppsflyerLib();
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
    public void testAppsflyerIntegration_FACTORYKEY_happyFlow() {
            Assert.assertEquals(AppsflyerIntegration.FACTORY.key(),"AppsFlyer");
    }

    @Test
    public void testAppsflyerIntegration_getUnderlyingInstance_happyFlow() {
        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
        Logger logger = new Logger("test", Analytics.LogLevel.INFO);
        AppsflyerIntegration appsflyerIntegration = new AppsflyerIntegration(null,logger,appsFlyerLib,null);

        Assert.assertEquals(appsflyerIntegration.getUnderlyingInstance(),appsFlyerLib);

        reset(appsFlyerLib);
    }

    @Test
    public void testAppsflyerIntegration_identify_happyFlow() throws Exception {
        AppsFlyerLib appsFlyerLib = mock(AppsFlyerLib.class);
        Logger logger = spy(new Logger("test", Analytics.LogLevel.INFO));
        AppsflyerIntegration appsflyerIntegration = new AppsflyerIntegration(null,logger,appsFlyerLib,null);
        IdentifyPayload identifyPayload = mock(IdentifyPayload.class);
        Traits traits = mock(Traits.class);
        when(identifyPayload.userId()).thenReturn("moris");
        when(identifyPayload.traits()).thenReturn(traits);
        when(traits.getString("currencyCode")).thenReturn("ILS");

        appsflyerIntegration.identify(identifyPayload);

        verify(logger, never()).verbose(any());
        String customerUserIdInappsflyerIntegration = (String) TestHelper.getPrivateFieldForObject("customerUserId",AppsflyerIntegration.class,appsflyerIntegration);
        Assert.assertEquals(customerUserIdInappsflyerIntegration, "moris");
        String currencyCodeInappsflyerIntegration = (String) TestHelper.getPrivateFieldForObject("currencyCode",AppsflyerIntegration.class,appsflyerIntegration);
        Assert.assertEquals(currencyCodeInappsflyerIntegration, "ILS");

        reset(appsFlyerLib,identifyPayload,traits);
    }

    @Test
    public void testAppsflyerIntegration_identify_nilflow() {
        Logger logger = spy(new Logger("test", Analytics.LogLevel.INFO));
        AppsflyerIntegration appsflyerIntegration = new AppsflyerIntegration(null,logger,null,null);
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
        AppsflyerIntegration appsflyerIntegration = new AppsflyerIntegration(null,logger,appsFlyerLib,null);
        Method updateEndUserAttributes = AppsflyerIntegration.class.getDeclaredMethod("updateEndUserAttributes");
        updateEndUserAttributes.setAccessible(true);
        TestHelper.setPrivateFieldForObject("customerUserId",AppsflyerIntegration.class,appsflyerIntegration,String.class,"Moris");
        TestHelper.setPrivateFieldForObject("currencyCode",AppsflyerIntegration.class,appsflyerIntegration,String.class,"ILS");
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
        AppsflyerIntegration appsflyerIntegration = new AppsflyerIntegration(null,logger,appsFlyerLib,null);
        TrackPayload trackPayload = mock(TrackPayload.class);
        String event = "event";
        Properties properties= mock(Properties.class);
        Map<String, Object> afProperties = mock(Map.class);
        MockedStatic<com.segment.analytics.internal.Utils> staticUtils = mockStatic(com.segment.analytics.internal.Utils.class);
        when(trackPayload.event()).thenReturn(event);
        when(trackPayload.properties()).thenReturn(properties);
        staticUtils.when(()->com.segment.analytics.internal.Utils.transform(any(),any())).thenReturn(afProperties);

        appsflyerIntegration.track(trackPayload);

        Context contextInAppsflyerIntegration = (Context) TestHelper.getPrivateFieldForObject("context",AppsflyerIntegration.class,appsflyerIntegration);
        verify(appsFlyerLib, times(1)).logEvent(contextInAppsflyerIntegration,event,afProperties);
        verify(logger, times(1)).verbose("appsflyer.logEvent(context, %s, %s)", event, properties);

        reset(appsFlyerLib,trackPayload,properties,afProperties);
        staticUtils.close();
    }
}