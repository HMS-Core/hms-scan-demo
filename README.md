# HMS Scankit Demo

English | [中文](https://github.com/HMS-Core/hms-scan-demo/blob/master/README_ZH.md)

## Table of Contents

 * [Introduction](#introduction)
 * [Supported Environments ](#supported-environments )
 * [Getting Started](#getting-started)
 * [Sample Code](#sample-code)
 * [Result](#result)
 * [License](#license)
 
## Introduction
The sample code shows how to use the HMS Scan Kit's code scanning capabilities to help developers quickly build code scanning capabilities within applications.

## Getting Started
1. Create an app and configure the app information in AppGallery Connect.
See details:[HUAWEI Scan Development Preparation](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/android-config-agc-0000001050043955)

2. Adding the AppGallery Connect Configuration File of Your App

3. Configuring the Maven Repository Address for the HMS Core SDK

## Supported Environments
Compile using Android Studio and JDK1.8
Prepared a device running EMUI 3.0 or later, adapted HMS APK 4.0.0.200 or later
	
## Sample Code
Scan kit demo provides four modes to adapt to the code scanning ability in different scenarios.

* Default View

The app directly calls the scanning activity of HUAWEI Scan Kit, and obtains the scanning result through the asynchronous callback API. Barcodes can be scanned using the device camera, or through an imported image.

Code location:[example/scankitdemo/MainActivity.java](https://github.com/HMS-Core/hms-scan-demo/blob/master/app/src/main/java/com/example/scankitdemo/MainActivity.java)

* Customized View

The app directly creates a RemoteView, and obtains the scanning result through the asynchronous callback API. Barcodes can be scanned using the device camera, or through an imported image.

Code location:[example/scankitdemo/DefinedActivity.java](https://github.com/HMS-Core/hms-scan-demo/blob/master/app/src/main/java/com/example/scankitdemo/DefinedActivity.java)

* Bitmap API

The app directly passes a bitmap through the bitmap API, and obtains the scanning result through the API. In your app, you can call the camera API or import a local image to obtain the bitmap, and then call the bitmap API of HUAWEI Scan Kit to decode the bitmap.

Code location:[example/scankitdemo/CommonActivity.java example/scankitdemo/CommonHandler.java](https://github.com/HMS-Core/hms-scan-demo/blob/master/app/src/main/java/com/example/scankitdemo/CommonHandler.java)

* MultiProcessor API

The app passes frame data through the MultiProcessor API for decoding, and detects barcodes along with multiple objects such as faces, using the same technology as HUAWEI ML Kit.

Code location:[example/scankitdemo/CommonActivity.java example/scankitdemo/CommonHandler.java](https://github.com/HMS-Core/hms-scan-demo/blob/master/app/src/main/java/com/example/scankitdemo/CommonHandler.java)
	
5)Generate Code API

The app allow you to generate barcode.

Code location:[example/scankitdemo/GenerateCodeActivity.java](https://github.com/HMS-Core/hms-scan-demo/blob/master/app/src/main/java/com/example/scankitdemo/GenerateCodeActivity.java)

## Result
<img src="Screenshot.jpg" width=250 title="ID Photo DIY" div align=center border=5>

## Question or issues
If you want to evaluate more about HMS Core, [r/HMSCore on Reddit](https://www.reddit.com/r/HuaweiDevelopers/) is for you to keep up with latest news about HMS Core, and to exchange insights with other developers.

If you have questions about how to use HMS samples, try the following options:
- [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services) is the best place for any programming questions. Be sure to tag your question with 
`huawei-mobile-services`.
- [Huawei Developer Forum](https://forums.developer.huawei.com/forumPortal/en/home?fid=0101187876626530001) HMS Core Module is great for general questions, or seeking recommendations and opinions.

If you run into a bug in our samples, please submit an [issue](https://github.com/HMS-Core/hms-scan-demo/issues) to the Repository. Even better you can submit a [Pull Request](https://github.com/HMS-Core/hms-scan-demo/pulls) with a fix.

##  License
HMS Scan Kit sample is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
