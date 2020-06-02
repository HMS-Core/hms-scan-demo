## HUAWEI Scan Kit Sample Code


## Table of Contents

 * [Introduction](#introduction)
 * [Supported Environments ](#Supported-Environments )
 * [Sample Code](#Sample-Code)
 * [License](#license)
 
 
## Introduction
    The sample code shows how to use the HMS Scan Kit's code scanning capabilities to help developers quickly build code scanning capabilities within applications.

## Supported Environments
    Compile using Android Studio and JDK1.8
	Prepared a device running EMUI 3.0 or later£¬ adapted HMS APK 4.0.0.200 or later£¬
	
## Sample Code
    Scan kit demo provides four modes to adapt to the code scanning ability in different scenarios.

    1)Default View
    The app directly calls the scanning activity of HUAWEI Scan Kit, and obtains the scanning result through the asynchronous callback API. Barcodes can be scanned using the device camera, or through an imported image.
    Code location£ºexample/scankitdemo/MainActivity.java

    2)Customized View
    The app directly creates a RemoteView, and obtains the scanning result through the asynchronous callback API. Barcodes can be scanned using the device camera, or through an imported image.
    Code location£ºexample/scankitdemo/DefinedActivity.java

    3)Bitmap API
    The app directly passes a bitmap through the bitmap API, and obtains the scanning result through the API. In your app, you can call the camera API or import a local image to obtain the bitmap, and then call the bitmap API of HUAWEI Scan Kit to decode the bitmap.
    Code location£ºexample/scankitdemo/CommonActivity.java example/scankitdemo/CommonHandler.java

    4)MultiProcessor API
    The app passes frame data through the MultiProcessor API for decoding, and detects barcodes along with multiple objects such as faces, using the same technology as HUAWEI ML Kit.
    Code location£ºexample/scankitdemo/CommonActivity.java example/scankitdemo/CommonHandler.java
	
	5)Generate Code API
	The app allow you to generate barcode.
    Code location£ºexample/scankitdemo/GenerateCodeActivity.java

##  License
    HMS Scan Kit sample is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

