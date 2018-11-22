ICSee
=======

Abstract
--------
It is an application for small portable devices (smart phones, tablets) that will help people with severe visual  impairments see more clearly.
How? The user simply directs the camera of his/her smartphone or tablet to the point he/she wants and… that was it! The application processes the image on the screen according to the user’s needs (conversion to negative or greyscale, increase the contrast, etc.) and provides in real time an image that is is easier for the user to see!
Who is it for?
It is intended to be used by people with severe visual impairments.
The Application provides an audio tutorial when starting. Depending on the phone's language, the audio will be in Greek or in English.

Implementation
--------------
The ICSee project involves a software solution for Android hand-held devices.
We can support filtering on a camera-streamed image (real-time), applying a wide variety of filters on it.

Compilation - Requirements
-----------
The minimum SDK version is 14 (Android 4.0). The targeted SDK version is 19 (Android 4.4)
A device with a camera is required. For the auto-focus feature, the camera should support auto-focus.
This is a Gradle-based project. The required Open-CV library is included in the Android project.
It is required to have the <a href="http://docs.opencv.org/2.4.11/platforms/android/service/doc/index.html">OpenCV Manager</a> installed on the device. You can find it <a href="https://play.google.com/store/apps/details?id=org.opencv.engine&hl=en">here</a>
(The ICSee app will prompt you to the appropriate Play Store page if you haven't installed OpenCV Manager already.)

Deployment
----------
This Application is deployed as a normal Android app.

Main Technologies
-----------------
<a href="http://opencv.org/"><img src="http://upload.wikimedia.org/wikipedia/commons/thumb/3/32/OpenCV_Logo_with_text_svg_version.svg/750px-OpenCV_Logo_with_text_svg_version.svg.png" alt="OpenCV" width="100px"></a>

[1]: http://www.scify.gr/site/en/projects/in-progress/icsee

User/developer guidelines
-------------------------
Download the User Guidelines from <a href="http://icstudy.projects.development1.scify.org/www/files/ICSeeAudioInstructionsGREN.pdf">here.</a>

Download the Developer Guidelines from <a href="http://icstudy.projects.development1.scify.org/www/files/ICSee_developer_guidelines.pdf">here.</a>

Altering/ adding more languages
-------------------------------
A this version of the app, English and Greek sounds are supported. This is how the selection works:<br>
When the app starts, the current language code is produced, based on the Locale, or the SIM operator's country code. (If we are on a tablet, there may be no SIM card).
```java
lang = Locale.getDefault().getLanguage();
TelephonyManager tm = (TelephonyManager)startActivity.getSystemService(Context.TELEPHONY_SERVICE);
countryCode = tm.getSimCountryIso();
```
Each time we try to load a sound, we check for the lang or countryCode variable. If we are in a Greek environment we load the Greek sound. Otherwise, we default back to the English one.
Given that, in order to add sound files in French, you should modify the code for each function that loads a sound as follows:
```java
if(lang.equals("fr") || countryCode.equals("fr")) {
    soundId = R.raw.NAME_OF_FRENCH_FILE_HERE;
}
else if (lang.equals("el") || countryCode.equals("gr")) {
    soundId = R.raw.gr_take_picure;
} else {
    soundId = R.raw.en_take_picture;
}
```

## Installing OpenCV as an included module

1. First download the OpenCV Android SDK from [https://opencv.org/releases.html](https://opencv.org/releases.html)                

2. Unzip the SDK to a directory of your choice. **(The OpenCV-android-sdk directory 
path name must not contain any spaces).**

3. Open Android Studio and go to:
File->New->Import Module

4. Select the `OpenCV-android-sdk/sdk/java` directory. If selected correctly, you will 
see that the "Module Name" field gets updated.

    ![Installation screenshot](https://raw.githubusercontent.com/SciFY/ICSee/opencv_integrated/documentation_screens/ICSee-2.png)

    ![Installation screenshot](https://raw.githubusercontent.com/SciFY/ICSee/opencv_integrated/documentation_screens/ICSee-3.png)

5. In the next screen (ADT Import Preferences), make sure that you uncheck all options.

    ![Installation screenshot](https://raw.githubusercontent.com/SciFY/ICSee/opencv_integrated/documentation_screens/ICSee-4.png)

6. Then, you will need to make some changes in the `build.gradle` file of your project and the build.gradle
file of the imported OPenCV module.

    These changes will make sure that the project and the module have the same values for the
    sdk version and the build tools version.
    
    ```
    compileSdkVersion 28
    buildToolsVersion "28.0.3"
    
    defaultConfig {
        minSdkVersion 15
        targetSdkVersion 28
    }
    ```
    settings.

7. So, open the `build.gradle` file of your project, and copy 
the properties described above. Then paste them into the `openCVLibrary344/build.gradle` file (overwrite if already set).

8. For the changes to take effect, select **Sync now**.

9. Then, make sure that the Project View preference for your project is set to **Android**.

    ![Installation screenshot](https://raw.githubusercontent.com/SciFY/ICSee/opencv_integrated/documentation_screens/ICSee-5.png)

10. Right click on Android, go to open module settings and select dependencies.
    Go to the Dependencies tab, then press + , module dependencies and select the imported OpenCV library.
    ![Installation screenshot](https://raw.githubusercontent.com/SciFY/ICSee/opencv_integrated/documentation_screens/ICSee-7.png)
    
    ![Installation screenshot](https://raw.githubusercontent.com/SciFY/ICSee/opencv_integrated/documentation_screens/ICSee-8.png)

11. Then, Go to New ->Folder -> JNI folder (Android View)

    Select Change folder location
    
    and set `src/main/jniLibs/`.

12. Go to the `/path/to/OpenCV-android-sdk/sdk/native/libs` folder which you downloaded 
then copy the folders inside,paste that to Project view `app/src/main/jniLibs` location.

    ![Installation screenshot](https://raw.githubusercontent.com/SciFY/ICSee/opencv_integrated/documentation_screens/ICSee-11.png)

13. Then, (in android file view mode), right click on the app directory and select "Link c++ project with gradle"
    
    ![Installation screenshot](https://raw.githubusercontent.com/SciFY/ICSee/opencv_integrated/documentation_screens/ICSee-10.png)
    
    ![Installation screenshot](https://raw.githubusercontent.com/SciFY/ICSee/opencv_integrated/documentation_screens/ICSee-12.png)
    
14. Select "ndk-build"
and select `/path/to/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk` file from the downloaded Sdk path.

    ![Installation screenshot](https://raw.githubusercontent.com/SciFY/ICSee/opencv_integrated/documentation_screens/ICSee-13.png)
    
    ![Installation screenshot](https://raw.githubusercontent.com/SciFY/ICSee/opencv_integrated/documentation_screens/ICSee-14.png)

15. In order for this step to execute correctly, you need to have NDK integrated into Android Studio.
(it can be installed from the SDK manager).

To verify that OpenCV is loaded correctly, you can Log the following to your Activity class:

```
Log.d("verify",String.valueOf(OpenCVLoader.initDebug()));
```

More info about the process can be found here:

[Link 1](https://stackoverflow.com/a/40746665/4679732)

[Link 2](https://www.learn2crack.com/2016/03/setup-opencv-sdk-android-studio.html)

[Link 3](https://medium.com/@rdeep/android-opencv-integration-without-opencv-manager-c259ef14e73b)


--------------------------
LICENSE
-----------------

Copyright 2015

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Sponsors
--------
<table>
<tr>
<td>
<a href="http://www.scify.gr/site/en/"><img src="http://www.scify.gr/site/images/scify/scify_logo_108.png"></a>
</td>
<td>
<a href="http://www.latsis-foundation.org/" title="Ίδρυμα Λάτση" rel="home"><img src="http://www.latsis-foundation.org/img/iePngs/logoEll.png" alt="Ίδρυμα Λάτση" title="Ίδρυμα Λάτση"></a>
</td>
</tr>
</table>

----------