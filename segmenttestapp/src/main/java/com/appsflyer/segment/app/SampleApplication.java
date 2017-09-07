package com.appsflyer.segment.app;

import android.app.Application;
import android.util.Log;

import com.appsflyer.AppsFlyerLib;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.android.integrations.appsflyer.AppsflyerIntegration;


public class SampleApplication extends Application {


    static final String SEGMENT_WRITE_KEY = "GRN6QWnSb8tbDETvKXwLQDEVomHmHuDO";
    static final String TAG = "SEG_AF";

    @Override public void onCreate() {
        super.onCreate();

            AppsFlyerLib afLib = AppsFlyerLib.getInstance();
            AppsFlyerLib.getInstance().enableUninstallTracking("120680937670");

        initSegmentAnalytics();

        Analytics analytics = Analytics.with(this);

        analytics.onIntegrationReady("Segment.io", new Analytics.Callback() {
            @Override public void onReady(Object instance) {
                Log.d(TAG, "Segment integration ready.");
            }
        });

       // Analytics.with(this.getApplicationContext()).track("Purchase Event", new Properties().putValue("someValue",20).putRevenue(200));

    }

    private void initSegmentAnalytics() {
        Analytics.Builder builder = new Analytics.Builder(this, SEGMENT_WRITE_KEY)
                .use(AppsflyerIntegration.FACTORY)
                .logLevel(Analytics.LogLevel.VERBOSE)
                .trackApplicationLifecycleEvents() // Enable this to record certain application events automatically!
                .recordScreenViews(); // Enable this to record screen views automatically!


        // Set the initialized instance as a globally accessible instance.
        Analytics.setSingletonInstance(builder.build());
    }
}
