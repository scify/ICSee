ICSee
=======

Abstract
--------
It is an application for small portable devices (smart phones, tablets) that will help people with severe visual  impairments see more clearly.
How? The user simply directs the camera of his/her smartphone or tablet to the point he/she wants and… that was it! The application processes the image on the screen according to the user’s needs (conversion to negative or greyscale, increase the contrast, etc.) and provides in real time an image that is is easier for the user to see!
Who is it for?
It is intended to be used by people with severe visual impairments.
The Application provides an audio tutorial when starting.

Implementation
--------------
The ICSee project involves a software solution for Android hand-held devices.
We can support filtering on a camera-streamed image (real-time), applying a wide variety of filters on it.

Compilation - Requirements
-----------
The minimum SDK version is 16 (Android 4.1).
A device with a camera is required. For the auto-focus feature, the camera should support auto-focus.
This is a Gradle-based project. The required Open-CV library is included in the Android project (see `app/build.gradle` file).

Properties files setup
-----------

First make sure that a `SENTRY_DSN` variable exists in `local.properties`. If you use Sentry, you can put the Sentry DSN value there:

Example:

```text
SENTRY_DSN=https://test@sentry.test.org/1
```

Also, make sure to add a `secrets.properties` file in the root project directory. Initially, this file should look like this:

```text
SHAPES_DATALAKE_KEY=
```

Deployment
----------
This Application is deployed as a normal Android app.

Main Technologies
-----------------
<a href="http://opencv.org/"><img src="http://upload.wikimedia.org/wikipedia/commons/thumb/3/32/OpenCV_Logo_with_text_svg_version.svg/750px-OpenCV_Logo_with_text_svg_version.svg.png" alt="OpenCV" width="100px"></a>

[1]: http://www.scify.gr/site/en/projects/in-progress/icsee

LICENSE
-----------------

Copyright 2022

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
<a href="http://www.scify.gr/site/en/" title="SciFY website" rel="home" target="_blank"><img width="150px" src="http://www.scify.gr/site/images/scify/scify_logo_108.png" alt="SciFY logo" title="SciFY logo"></a>
</td>
<td>
<a href="http://www.latsis-foundation.org/" title="Ίδρυμα Λάτση" rel="home" target="_blank"><img width="150px" src="https://www.neolaia.gr/wp-content/uploads/2019/05/idryma_latsi-1.jpg" alt="Ίδρυμα Λάτση logo" title="Ίδρυμα Λάτση logo"></a>
</td>
<td>
<a href="https://shapes2020.eu/" title="SHAPES EU Project" rel="home" target="_blank"><img width="150px" src="https://shapes2020.eu/wp-content/uploads/2020/03/SHAPES_Logo_Scaled_190-removebg-preview.png" alt="SHAPES EU project logo" title="SHAPES EU project logo"></a>
</td>
</tr>
</table>
