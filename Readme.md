[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.appsflyer/segment-android-integration/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.appsflyer/segment-android-integration) 

<h2 class="CalloutBox-title"><span class="wysiwyg-font-size-large">What is Segment?</span></h2>
<p>Segment is a customer data hub. You send your data from any device or platform. It is translated and forwarded to your apps in a language they understand.</p>
<p>Segment makes it easy to send your data to <a href="http://appsflyer.com/?utm_source=segmentio&amp;utm_medium=docs&amp;utm_campaign=partners" target="_blank">AppsFlyer</a> (and lots of other integrations). 
<p><strong><span class="wysiwyg-font-size-large">AppsFlyer</span></strong></p>
<div>
<p>AppsFlyer lets you understand, engage, monetize and maximize the lifetime value of your app users. <a class="ArrowLink ArrowLink--caps" href="http://AppsFlyer.com/?utm_source=segmentio&amp;utm_medium=docs&amp;utm_campaign=partners" target="_blank">Visit Website</a></p>
</div>
<p>The AppsFlyer integration code is open-source on GitHub if you want to <a href="https://github.com/segmentio/integration-AppsFlyer" target="_blank">check it out</a>.</p>
<h2 id="getting-started" class="Permalink"><span class="wysiwyg-font-size-large">Getting Started</span></h2>
<p>Once the Segment library is integrated, toggle AppsFlyer on in your Segment integrations, and add your App Group Identifier which you can find in the AppsFlyer Dashboard under App Settings &gt; Developer Console.</p>
<p>The Segment AppsFlyer integration is 100% handled through our servers, so you don’t need to bundle their iOS or Android SDKs. Your Segment SDK will be enough.</p>
<p>AppsFlyer supports the <code>identify</code>, <code>track</code>, and <code>group</code> methods.</p>
<h2 id="identify" class="Permalink"><span class="wysiwyg-font-size-large">Identify</span></h2>
<p>When you <code>identify</code> a user, that user’s information is passed to AppsFlyer with <code>customer user Id</code> as AppsFlyer’s External User ID. Segment’s special traits recognized as AppsFlyer’s standard user profile fields (in parentheses) are:</p>
<ul>
<li><code> appsFlyerDevKey </code> (<code> appsflyer's devKey </code>)</li>
<li><code> customerUserId </code> (<code>Customer User Id</code>)</li>
<li><code> currencyCode </code> (<code>Currency Code</code>)</li>
</ul>
<p>All other traits will be sent to AppsFlyer as custom attributes.</p>
<h2 id="track" class="Permalink"><span class="wysiwyg-font-size-large">Track</span></h2>
<p>When you <code>track</code> an event, the event is sent to AppsFlyer as a custom event.</p>
<h3 id="completed-order" class="Permalink"><span class="wysiwyg-font-size-large">Completed Order</span></h3>
<p>When you <code>track</code> an event with the name <code>Completed Order</code> using the <a href="https://segment.com/docs/spec/ecommerce" target="_blank">e-commerce tracking API</a>, the products you’ve listed are sent to AppsFlyer as purchases.</p>

<h2 id="android" class="Permalink">Android</h2>
<h3 id="integrating" class="Permalink">Integrating</h3>
<p>1. Add the AppsFlyer Segment Integration dependency to your app <code>build.gradle</code>:</p>
<pre><code>compile 'com.appsflyer:segment-android-integration:1.+'
</code></pre>
<p>It is recommended to use the latest version on <a href="http://search.maven.org/#search%7Cga%7C1%7Ca%3A%AppsFlyer-segment-integration%22" target="_blank">Maven</a> since it contains the most up-to-date features and bug fixes.</p>
<p><strong>NOTE</strong>: Our Group ID is <code>com.appsFlyer</code> and not <code>com.segment.analytics.android.integrations</code>.</p>
<p>2. Next, declare AppsFlyer’s integration in your <code>Analytics</code> instance:</p>
<pre><code>Analytics analytics = new Analytics.Builder(context, "YOUR_WRITE_KEY_HERE")
  .use(AppsFlyer.FACTORY)
  ...
  .build();
</code></pre>

<h3 id="in-app-messages" class="Permalink">In-App Messages</h3>
<p>No further action is required to integrate in-app messages, which are registered for and requested by default by our AppsFlyer Segment integration.</p>
<h3 id="sample-app" class="Permalink">Sample App</h3>
<p>AppsFlyer has created a sample Android application that integrates AppsFlyer via Segment. Check it out at the <a href="https://github.com/AppsFlyerSDK/AppsFlyer-Segment-Integration.git" target="_blank">Github repo</a>.</p>
