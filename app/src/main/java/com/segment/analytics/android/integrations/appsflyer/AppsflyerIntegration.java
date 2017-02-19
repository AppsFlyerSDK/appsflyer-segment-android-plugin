package com.segment.analytics.android.integrations.appsflyer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.Logger;
import com.segment.analytics.integrations.TrackPayload;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.segment.analytics.internal.Utils.transform;

/**
 * Created by shacharaharon on 12/04/2016.
 */
public class AppsflyerIntegration extends Integration<AppsFlyerLib> {

    static final Map<String, String> MAPPER;

    private static final String APPSFLYER_KEY = "AppsFlyer";
    private static final String SEGMENT_REVENUE = "revenue";

    /**
     * Responsible to map revenue -> af_revenue
     */
    static {
        Map<String, String> mapper = new LinkedHashMap<>();
        mapper.put(SEGMENT_REVENUE, AFInAppEventParameterName.REVENUE);
        MAPPER = Collections.unmodifiableMap(mapper);
    }



    final Logger logger;
    final AppsFlyerLib appsflyer;
    final String appsFlyerDevKey;
    final boolean isDebug;
    private Context context;
    private String customerUserId;
    private String currencyCode;

    public static ConversionListenerDisplay cld;
    public static final Factory FACTORY = new Integration.Factory() {
        @Override
        public Integration<AppsFlyerLib> create(ValueMap settings, Analytics analytics) {
            Logger logger = analytics.logger(APPSFLYER_KEY);
            AppsFlyerLib afLib = AppsFlyerLib.getInstance();

            String devKey = settings.getString("appsFlyerDevKey");
            boolean trackAttributionData = settings.getBoolean("trackAttributionData", false);
            Application application = analytics.getApplication();
            afLib.startTracking(application, devKey);
            logger.verbose("AppsFlyer.getInstance().startTracking(%s, %s)", application, devKey.substring(0, 1) + "*****" + devKey.substring(devKey.length() - 2));
            if (trackAttributionData) {
                AppsFlyerConversionListener listener = new ConversionListener(analytics);
                afLib.registerConversionListener(application, listener);
            }
            return new AppsflyerIntegration(application, logger, afLib, devKey);
        }

        @Override
        public String key() {
            return APPSFLYER_KEY;
        }

    };

    public AppsflyerIntegration(Context context, Logger logger, AppsFlyerLib afLib, String devKey) {
        this.context = context;
        this.logger = logger;
        this.appsflyer = afLib;
        this.appsFlyerDevKey = devKey;
        this.isDebug = (logger.logLevel != Analytics.LogLevel.NONE);
    }

    // TODO: for some reason it never called
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);

        updateEndUserAttributes();
        appsflyer.startTracking(activity.getApplication(), appsFlyerDevKey);
        logger.verbose("AppsFlyer.getInstance().startTracking(%s, %s)",
                activity.getApplication(),
                appsFlyerDevKey.substring(0, 1) + "*****" + appsFlyerDevKey.substring(appsFlyerDevKey.length() - 2));
    }



    @Override
    public AppsFlyerLib getUnderlyingInstance() {
        return appsflyer;
    }

    @Override
    public void identify(IdentifyPayload identify) {
        super.identify(identify);

        customerUserId = identify.userId();
        currencyCode = identify.traits().getString("currencyCode");

        if (appsflyer != null) {
            updateEndUserAttributes();
        } else {
            logger.verbose("couldn't update 'Identify' attributes");
        }
    }

    private void updateEndUserAttributes() {

        appsflyer.setCustomerUserId(customerUserId);
        logger.verbose("appsflyer.setCustomerUserId(%s)", customerUserId);
        appsflyer.setCurrencyCode(currencyCode);
        logger.verbose("appsflyer.setCurrencyCode(%s)", currencyCode);
        appsflyer.setDebugLog(isDebug);
        logger.verbose("appsflyer.setDebugLog(%s)", isDebug);
    }

    @Override
    public void track(TrackPayload track) {
        String event = track.event();
        Properties properties = track.properties();

        Map<String, Object> afProperties = transform(properties, MAPPER);

        appsflyer.trackEvent(context, event, afProperties);
        logger.verbose("appsflyer.trackEvent(context, %s, %s)", event, properties);
    }


    public interface ConversionListenerDisplay {
        void display(Map<String, String> attributionData);
    }

    static class ConversionListener implements AppsFlyerConversionListener {
        final Analytics analytics;

        public ConversionListener(Analytics analytics) {
            this.analytics = analytics;
        }

        @Override
        public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
            trackInstallAttributed(conversionData);
            if (cld != null) {
                cld.display(conversionData);
            }
        }

        @Override
        public void onInstallConversionFailure(String errorMessage) {

        }

        @Override
        public void onAppOpenAttribution(Map<String, String> attributionData) {
            trackInstallAttributed(attributionData);
            if (cld != null) {
                cld.display(attributionData);
            }
        }

        @Override
        public void onAttributionFailure(String errorMessage) {

        }

        void trackInstallAttributed(Map<String, String> attributionData) {
            // See https://segment.com/docs/spec/mobile/#install-attributed.
            Map<String, Object> campaign = new ValueMap() //
                    .putValue("source", attributionData.get("media_source"))
                    .putValue("name", attributionData.get("campaign"))
                    // .putValue("content", attributionData.get("?"))
                    // .putValue("adCreative", attributionData.get("?"))
                    .putValue("adGroup", attributionData.get("adgroup"));


            Properties properties = new Properties() //
                    .putValue("provider", "AppsFlyer")
                    .putValue("campaign", campaign);
            properties.putAll(attributionData);

            // Remove properties set in campaign.
            properties.remove("media_source");
            properties.remove("campaign");
            properties.remove("adgroup");

            analytics.track("Install Attributed", properties);
        }
    }

}
