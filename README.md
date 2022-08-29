# HMS Core Scan Kit Sample Code (Android)

English | [中文](README_ZH.md)
## Contents

 * [Introduction](#Introduction)
 * [Environment Requirements](#Environment-Requirements)
 * [Sample Code](#Sample-Code)
 * [Result](#Result)
 * [License](#License)



## Introduction

This sample code shows how to quickly build barcode scanning functions into your app using the capabilities of the [HMS Core Scan Kit](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/service-introduction-0000001050041994). Currently, Scan Kit supports the following barcode formats:

- 1D barcode formats: [EAN-8](https://en.wikipedia.org/wiki/EAN-8), [EAN-13](https://en.wikipedia.org/wiki/International_Article_Number), [UPC-A](https://en.wikipedia.org/wiki/Universal_Product_Code), [UPC-E](https://en.wikipedia.org/wiki/Universal_Product_Code#UPC-E), [Codabar](https://en.wikipedia.org/wiki/Codabar), [Code 39](https://en.wikipedia.org/wiki/Code_39), [Code 93](https://en.wikipedia.org/wiki/Code_93), [Code 128](https://en.wikipedia.org/wiki/Code_128), and [ITF-14](https://en.wikipedia.org/wiki/ITF-14)
- 2D barcode formats: [QR Code](https://en.wikipedia.org/wiki/QR_code), [Data Matrix](https://en.wikipedia.org/wiki/Data_Matrix), [PDF417](https://en.wikipedia.org/wiki/PDF417), and [Aztec](https://en.wikipedia.org/wiki/Aztec_Code)



## Environment Requirements

Android Studio 3.0 or later and JDK 1.8.211 or later.

A Huawei device that runs EMUI 3.0 or later, compatible with HMS Core (APK) 4.0.0.200 or a generic device running Android 4.4 or later.



## Sample Code

Barcode scanning can be achieved in different ways, showcased by the demo.

**Default view**

The [default view](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/android-default-view-0000001050043961) mode will start a dedicated `Activity` provided by Scan Kit that will display a predefined UI, control the camera and pass back the scanning result via the `onActivityResult()` of the original activity (`com.example.scankitdemo.MainActivity` in the demo) .

**Customized view**

In [customized view](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/android-customized-view-0000001050042012) mode, you can define a custom UI, with the help of a `com.huawei.hms.hmsscankit.RemoteView`, and then obtain the scanning result through an asynchronous callback. As with the *default view* mode, you do not need to worry about developing the scanning process or controlling the camera. See `com.example.scankitdemo.DefinedActivity`.

**Bitmap**

Use this mode when you wish to have full control over the input to the scanning process and the moment when you wish to get the results back. In `com.example.scankitdemo.CommonActivity` , the demo will pass a `Bitmap` to `com.huawei.hms.hmsscankit.ScanUtil.decodeWithBitmap()` ([definition](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/scan-scanutil4-0000001050167699#section14774629143713)).

**Multi-processor**

Recognize multiple barcodes at the same time. When working together with the [HMS ML Kit](https://developer.huawei.com/consumer/en/hms/huawei-mlkit/), Scan Kit can detecting both barcodes and human faces. Frame data is transmitted and decoded through [the multi-processor API](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/scan-analyzer4-0000001050167905). See `com.example.scankitdemo.CommonActivity` and read more about [how it works](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/android-synchronous-mode-0000001050043967).

**Generating barcodes** 

Generate your own barcodes by calling `com.huawei.hms.hmsscankit.ScanUtil.buildBitmap()` ([definition](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/scan-scanutil4-0000001050167699#section56266161243)) in `com.example.scankitdemo.GenerateCodeActivity`.



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

