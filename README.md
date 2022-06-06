HMS Site Kit for Android
===============================
![Apache-2.0](https://img.shields.io/badge/license-Apache-blue)

English | [中文](https://github.com/HMS-Core/hms-sitekit-demo/blob/master/README_ZH.md)

## Table of Contents

 * [Introduction](#Introduction)
 * [Getting Started](#getting-started)
 * [Supported Environments](#supported-environments)
 * [Support](#Support)
 * [Result](#Result)
 * [License](#License)

Introduction
------------

With HUAWEI Site Kit, your app can provide users with convenient and secure access to diverse, place-related services.

HUAWEI Site Kit provides the following core capabilities you need to quickly build apps with which your users can explore the world around them:

- Place search: Returns a place list based on keywords entered by the user.
- Nearby place search: Searches for nearby places based on the current location of the user's device.
- Place details: Searches for details about a place.
- Search suggestion: Returns a list of place suggestions.
- Widget:A search component of the built-in place search suggestion feature.
- Autocomplete:Return an autocomplete place and a list of suggested places.

You also can use HMS Toolkit to quickly integrate the kit and run the demo project, as well as debug the app using a remote device for free. For details, please visit https://developer.huawei.com/consumer/en/doc/development/Tools-Guides/getting-started-0000001077381096

Getting Started
---------------
We provide an sample to demonstrate the use of Site SDK for Android. 

This sample uses the Gradle build system.

First download the demo by cloning this repository or downloading an archived snapshot.

In Android Studio, use the "Open an existing Android Studio project", and select the directory of "site-sample".

You can use the "gradlew build" command to build the project directly.

You should create an app in AppGallery Connect, and obtain the file of agconnect-services.json and add to the project. You should also generate a signing certificate fingerprint  and add the certificate file to the project, and add configuration to build.gradle. See the [Configuring App Information in AppGallery Connect](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/android-sdk-config-agc-0000001050158579) guide to configure app in AppGallery Connect. In addition, change the value of the apiKey variable ("your api key") of the getApiKey function in Utils.java to the value of apiKey obtained from agconnect-services.json.



For more development guidance, please refer to the links below:

- [Development Guide](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/android-sdk-introduction-0000001050158571)
- [API References](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/package-summary-0000001064775040)

Supported Environments
-------

Android SDK Version >= 19 and JDK version >= 1.8 is recommended.

Support
-------

If you have any questions or comments during use, welcome to make suggestions or exchange here: https://github.com/HMS-Core/hms-sitekit-demo/issues

## Result

<img src="nearby-search.jpg" width = 30% height = 30%>

## Question or issues
If you want to evaluate more about HMS Core,
[r/HMSCore on Reddit](https://www.reddit.com/r/HuaweiDevelopers/
) is for you to keep up with latest news about HMS Core, and to exchange insights with other developers.

If you have questions about how to use HMS samples, try the following options:
- [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services) is the best place for any programming questions. Be sure to tag your question with 
`huawei-mobile-services`.
- [Huawei Developer Forum](https://forums.developer.huawei.com/forumPortal/en/home?fid=0101187876626530001) HMS Core Module is great for general questions, or seeking recommendations and opinions.

If you run into a bug in our samples, please submit an [issue](https://github.com/HMS-Core/hms-sitekit-demo/issues) to the Repository. Even better you can submit a [Pull Request](https://github.com/HMS-Core/hms-sitekit-demo/pulls) with a fix.

License
-------
Apache License version 2.0

You can get a licensed copy at: https://github.com/HMS-Core/hms-sitekit-demo/blob/master/LICENSE

