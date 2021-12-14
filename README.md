# HMS Core Scan Kit Sample Code (Android)

English | [中文](README_ZH.md)
## Contents

 * [Introduction](#Introduction)
 * [Preparations](#Preparations)
 * [Environment Requirements](#Environment-Requirements)
 * [Sample Code](#Sample-Code)
 * [Result](#Result)
 * [License](#License)


## Introduction
The sample code shows how to quickly build barcode scanning functions into an app using the capabilities in HMS Core Scan Kit.

You can use HMS Toolkit to quickly run the sample code. HMS Toolkit supports one-stop kit integration, and provides functions such as free app debugging on remote real devices. To learn more about HMS Toolkit, please refer to the [HMS Toolkit documentation](https://developer.huawei.com/consumer/en/doc/development/Tools-Guides/getting-started-0000001077381096?ha_source=hms1).

## Preparations
1. Create an app and configure its information in AppGallery Connect.
For details, please refer to [Configuring App Information in AppGallery Connect](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/android-config-agc-0000001050043955?ha_source=hms1).

2. Add the AppGallery Connect configuration file of your app.

3. Configure the Maven repository address for the HMS Core SDK.

## Environment Requirements
Android Studio 3.0 or later and JDK 1.8.211 or later
A device that runs EMUI 3.0 or later and is compatible with HMS Core (APK) 4.0.0.200 or later
	
## Sample Code
The demo shows the following modes for barcode scanning in different scenarios:

1. Default View

In this mode, your app will call activities provided by this kit and obtain the scanning result through an asynchronous callback API. Users will be able to scan barcodes directly with the camera or from local images imported to the app.

Find the sample code for this mode in **example/scankitdemo/MainActivity.java**.

2. Customized View

In this mode, your app will create a **RemoteView** object and obtain the scanning result through an asynchronous callback API. Users will be able to scan barcodes directly with the camera or from local images imported to the app.

Find the sample code for this mode in **example/scankitdemo/DefinedActivity.java**.

3. Bitmap

In this mode, your app will implement bitmap-based barcode scanning and obtain the scanning result through related APIs. Your app will be able to obtain bitmaps by starting the camera or importing local images, and decode the bitmaps by calling **decodeWithBitmap**.

Find the sample code for this mode in **example/scankitdemo/CommonActivity.java** and **example/scankitdemo/CommonHandler.java**.

4. MultiProcessor

When working together with ML Kit, Scan Kit provides the capability of detecting both barcodes and human faces. Frame data is transmitted and decoded through MultiProcessor APIs.

Find the sample code for this mode in **example/scankitdemo/CommonActivity.java** and **example/scankitdemo/CommonHandler.java**.
	
5. Generating barcodes

Call the **buildBitmap** API to generate barcodes.

Find the sample code for barcode generation in **example/scankitdemo/GenerateCodeActivity.java**.

## Result
<img src="Screenshot.jpg" width=250 title="ID Photo DIY" div align=center border=5>

## Technical Support
You can visit the [Reddit community](https://www.reddit.com/r/HuaweiDevelopers/) to obtain the latest information about HMS Core and communicate with other developers.

If you have any questions about the sample code, try the following:
- Visit [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services?tab=Votes), submit your questions, and tag them with `huawei-mobile-services`. Huawei experts will answer your questions.
- Visit the HMS Core section in the [HUAWEI Developer Forum](https://forums.developer.huawei.com/forumPortal/en/home?fid=0101187876626530001?ha_source=hms1) and communicate with other developers.

If you encounter any issues when using the sample code, submit your [issues](https://github.com/HMS-Core/hms-scan-demo/issues) or submit a [pull request](https://github.com/HMS-Core/hms-scan-demo/pulls).

##  License
The sample code is licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

