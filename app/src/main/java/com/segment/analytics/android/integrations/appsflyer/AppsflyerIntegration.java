package com.segment.analytics.android.integrations.appsflyer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.appsflyer.AppsFlyerLib;
import com.appsflyer.AppsFlyerProperties;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.Logger;
import com.segment.analytics.integrations.TrackPayload;

/**
 * Created by shacharaharon on 12/04/2016.
 */
public class AppsflyerIntegration extends Integration<AppsFlyerLib> {

    private static final String APPSFLYER_KEY = "AppsFlyer";

    final Logger logger;
    final AppsFlyerLib appsflyer;
    final String devKey;
//    final String[] userEmails;
    final int emailEncryption;
    final boolean isDebug;
    private Context context;

    private String endUserEmail, customerUserId, currencyCode;

    public static final Factory FACTORY = new Integration.Factory() {
        @Override
        public Integration<?> create(ValueMap settings, Analytics analytics) {
            Logger logger = analytics.logger(APPSFLYER_KEY);
            AppsFlyerLib afLib = AppsFlyerLib.getInstance();

            String devKey = settings.getString("devKey");
            int userEmailsCryptType = settings.getInt("userEmailsCryptType", 0);
            String currencyCode = settings.getString("currency");

            return new AppsflyerIntegration(logger, afLib, devKey, userEmailsCryptType, currencyCode, enableLog, customerUserId);
        }

        @Override
        public String key() {
            return APPSFLYER_KEY;
        }

    };

    public AppsflyerIntegration(Logger logger, AppsFlyerLib afLib, String devKey, int emailEncryption,  String currencyCode, boolean enableLog, String customerUserId) {
        this.logger = logger;
        this.appsflyer = afLib;
        this.devKey = devKey;
        this.emailEncryption = emailEncryption;
        this.currencyCode = currencyCode;
        this.isDebug = (logger.logLevel != Analytics.LogLevel.NONE);
        this.customerUserId = customerUserId;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        context = activity.getApplicationContext();
        updateEndUserAttributes();

        appsflyer.startTracking(activity.getApplication(), devKey);
        logger.verbose("AppsFlyer.getInstance().startTracking(%s, %s)",activity.getApplication(), devKey.substring(0,1)+"*****"+devKey.substring(devKey.length()-2) );
    }

    @Override
    public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);
        context = activity.getApplicationContext();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        super.onActivityPaused(activity);
        context = activity.getApplicationContext();
    }


    @Override
    public AppsFlyerLib getUnderlyingInstance() {
        return appsflyer;
    }


    @Override
    public void identify(IdentifyPayload identify) {
        super.identify(identify);

        Traits traits = identify.traits();
        endUserEmail = traits.email();
        customerUserId = identify.userId();

        if(appsflyer != null) {
            updateEndUserAttributes();
        } else {
            logger.verbose("couldn't update attributes");
        }
    }


    private void updateEndUserAttributes() {
        AppsFlyerProperties.EmailsCryptType cryptType;
        switch (emailEncryption) {
            case 1:
                cryptType = AppsFlyerProperties.EmailsCryptType.SHA1;
                break;
            case 2:
                cryptType = AppsFlyerProperties.EmailsCryptType.MD5;
                break;
            default:
                cryptType = AppsFlyerProperties.EmailsCryptType.NONE;
        }
        appsflyer.setUserEmails(cryptType,endUserEmail);
        logger.verbose("appsflyer.setUserEmails(%s)",endUserEmail);
        appsflyer.setCurrencyCode(currencyCode);
        logger.verbose("appsflyer.setCurrencyCode(%s)",currencyCode);
        appsflyer.setCustomerUserId(customerUserId);
        logger.verbose("appsflyer.setCustomerUserId(%s)",customerUserId);
        appsflyer.setDebugLog(isDebug);
        logger.verbose("appsflyer.setDebugLog(%s)",isDebug);
    }


    @Override
    public void track(TrackPayload track) {
        String event = track.event();
        Properties properties = track.properties();
        appsflyer.trackEvent(context, event, properties);
        logger.verbose("appsflyer.trackEvent(null, %s, %s)",context, event, properties);
    }


}

