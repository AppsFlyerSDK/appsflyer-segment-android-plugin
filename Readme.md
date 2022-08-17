<img src="https://www.appsflyer.com/wp-content/uploads/2016/11/logo-1.svg"  width="200">


# AppsFlyer - Segment Integration

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.appsflyer/segment-android-integration/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.appsflyer/segment-android-integration)

----------
In order for us to provide optimal support, we would kindly ask you to submit any issues to support@appsflyer.com

*When submitting an issue please specify your AppsFlyer sign-up (account) email , your app ID , production steps, logs, code snippets and any additional relevant information.*


# Overview

AppsFlyer SDK provides app installation and event tracking functionality. We have developed an SDK that is highly robust (7+ billion SDK installations to date), secure, lightweight and very simple to embed.



You can track installs, updates and sessions and also track additional in-app events beyond app installs (including in-app purchases, game levels, etc.) to evaluate ROI and user engagement levels.

---

Built with AppsFlyer Android SDK `v6.8.0`

## Table of content

- [Introduction](#whatIsSegment)
- [Getting Started](#quickStart)
- [Manual mode](#manual)
-  [SDK Initialization](#sdk_init)
-  [Register In-App Events](#adding_events)
-  [Get Conversion Data](#conversion_data)
- [Unified Deep Linking](#deep_linking)
- [Sample App](#sample_app)


### <a id="whatIsSegment">
# Introduction

Segment makes it easy to send your data to AppsFlyer. Once you have tracked your data through Segment's open source libraries, the data is translated and routed to AppsFlyer in the appropriate format. AppsFlyer helps marketers to pinpoint targeting, optimize ad spend and boost ROI.


The AppsFlyer integration code is open-source on GitHub if you want to [check it out](https://github.com/segment-integrations/integration-appsflyer).

Check out the Segment AppsFlyer docs [here](https://segment.com/docs/destinations/appsflyer/).






### <a id="gettingStarted">
# Getting Started


#### DashBoard Setup

To enable AppsFlyer in the Segment dashboard, follow these steps:

1.  Enter your unique AppsFlyer Dev Key, which is accessible from your AppsFlyer account, in Segment’s destination settings.
2.  After you build and release to the app store, your data is translated and sent to AppsFlyer automatically.



The Segment AppsFlyer integration is entirely handled through Segment's servers, so you don’t need to bundle AppsFlyer's iOS or Android SDKs. Your Segment SDK will be enough.

AppsFlyer supports the `identify` and `track` methods.

### <a id="manual">
# Manual mode
Starting version 6.8.0, we support a manual mode to seperate the initialization of the AppsFlyer SDK and the start of the SDK. In this case, the AppsFlyer SDK won't start automatically, giving the developper more freedom when to start the AppsFlyer SDK. Please note that in manual mode, the developper is required to implement the API startAppsFlyer(Context context) in order to start the SDK.
### Example:

```java
AppsflyerIntegration.setManualMode(true);
```

And to start the AppsFlyer SDK, use `void startAppsFlyer(Context context)` API.

### Example:

```java
    protected void onCreate(Bundle savedInstanceState) {
         AppsflyerIntegration.startAppsFlyer(this);
    }
 ```


### <a id="quickStart">

# Setting up the SDK

#### 2.1) Adding the Plugin to your Project

Add the AppsFlyer Segment Integration dependency to your app `build.gradle` file.
```java
compile 'com.appsflyer:segment-android-integration:6.5.2'
compile 'com.android.installreferrer:installreferrer:2.1'
```

#### 2.2)  Setting the Required Permissions

The AndroidManifest.xml should include the following permissions:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

In v6.8.0 of the AppsFlyer SDK, we added the normal permission com.google.android.gms.permission.AD_ID to the SDK's AndroidManifest, 
to allow the SDK to collect the Android Advertising ID on apps targeting API 33.
If your app is targeting children, you may need to revoke this permission to comply with Google's Data policy.
You can read more about it [here](https://support.appsflyer.com/hc/en-us/articles/7569900844689).

### <a id="sdk_init"> 2.2)  Init AppsFlyer

```java

static final String SEGMENT_WRITE_KEY = "<YOUR_KEY>";

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_main);

Analytics.Builder builder = new Analytics.Builder(this , SEGMENT_WRITE_KEY)
.use(AppsflyerIntegration.FACTORY)

...
(optional)

.logLevel(Analytics.LogLevel.VERBOSE)
.recordScreenViews()
.trackAttributionInformation() // Install Attributed event
.trackApplicationLifecycleEvents() // Application Opened , Application Updated , Application Installed events
.build();

Analytics.setSingletonInstance(builder.build());

}
```

Adding `.trackAttributionInformation()` will send the `Install Attributed` event to AppsFlyer.
Adding `.trackApplicationLifecycleEvents()` will send   `Application Opened`  , `Application Updated`  and `Application Installed` events to AppsFlyer.




## <a id="adding_events"> In-app events

When you call `track`, Segment translates it automatically and sends the event to AppsFlyer.

Segment includes all the event properties as callback parameters on the AppsFlyer event, and automatically translates `properties.revenue` to the appropriate AppsFlyer purchase event properties based on Segment's spec’d properties.

Finally, Segment automatically uses AppsFlyer’s transactionId-based de-duplication when sending an an `orderId`.


Purchase Event Example:
```java
Map<String, Object> eventValue = new HashMap<String, Object>();
eventValue.put("productId","com.test.id");
eventValue.put("revenue","1.00");
eventValue.put("currency","USD");

Analytics analytics = Analytics.with(this);
Properties properties = new Properties();
properties.putAll(eventValue);

analytics.track("purchase", properties);
```

Note: AppsFlyer will map `revenue -> af_revenue` and `currency -> af_currency`.

Check out the Segment docs on track [here](https://segment.com/docs/spec/track/).


### <a id="identify">

## Identify


When you `identify` a user, that user’s information is passed to AppsFlyer with `customer user Id` as AppsFlyer’s External User ID. Segment’s special traits recognized as AppsFlyer’s standard user profile fields (in parentheses) are:

`customerUserId` (`Customer User Id`) <br>
`currencyCode` (`Currency Code`)

All other traits will be sent to AppsFlyer as custom attributes.

```java
Analytics analytics = Analytics.with(this);

analytics.identify("a user's id", new Traits()
.putName("a user's name")
.putEmail("maxim@appsflyer.com"),
null);
```

Check out the Segment docs on indentify [here](https://segment.com/docs/spec/identify/).

### <a id="conversion_data">

##  Get Conversion Data

For Conversion data your should call the method below.

```java
         AppsflyerIntegration.conversionListener  = new AppsflyerIntegration.ExternalAppsFlyerConversionListener() {
                    @Override
                    public void onConversionDataSuccess(Map<String, Object> map) {
                        // Process Deferred Deep Linking here
                        for (String attrName : map.keySet()) {
                            Log.d(TAG, "attribute: " + attrName + " = " + map.get(attrName));
                        }
                    }

                    @Override
                    public void onConversionDataFail(String s) {

                    }

                    @Override
                    public void onAppOpenAttribution(Map<String, String> map) {
                     // Process Direct Deep Linking here
                        for (String attrName : map.keySet()) {
                            Log.d(TAG, "attribute: " + attrName + " = " + map.get(attrName));
                        }
                    }

                    @Override
                    public void onAttributionFailure(String s) {

                    }
                };
```

In order for Conversion Data to be sent to Segment, make sure you have enabled "Track Attribution Data" in AppsFlyer destination settings:

<img width="741" alt="Xnip2019-05-11_19-19-31" src="https://user-images.githubusercontent.com/18286267/57572409-8fb19200-7422-11e9-832f-fdd343af3137.png">

### <a id="deep_linking">

##  Unified deep linking
In order to implement unified deep linking, call the method below :

```java
        AppsflyerIntegration.deepLinkListener = new AppsflyerIntegration.ExternalDeepLinkListener() {
            @Override
            public void onDeepLinking(@NonNull DeepLinkResult deepLinkResult) {
                //TODO handle deep link logic
            }
        };
```
For more information about unified deep linking, check [here](https://dev.appsflyer.com/docs/android-unified-deep-linking)

### <a id="sample_app">

##  Sample App
<p>AppsFlyer has created a sample Android application that integrates AppsFlyer via Segment. Check it out at the <a href="https://github.com/AppsFlyerSDK/appsflyer-segment-android-plugin/tree/master/segmenttestapp" target="_blank">Github repo</a>.</p>
