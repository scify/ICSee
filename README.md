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

Πίνακας παραδοτέων
------------------

<table>
  <tr>
    <th>Α/Α</th>
    <th>Περιγραφή δράσης</th>
    <th>Τεκμηρίωση</th>
  </tr>
  <tr>
    <td>Α</td>
    <td>Έκδοση 1.0 της εφαρμογής ICSee, με επιπλέον τρία φίλτρα επεξεργασίας εικόνας</td>
    <td>Το ICSee υπάρχει διαθέσιμο για δωρεάν κατέβασμα στο Google Play σε αυτόν τον σύνδεσμο: https://goo.gl/WnNyzn</td>
  </tr>
  <tr>
    <td>Β</td>
    <td>Υλικό για διασφάλιση της δυνατότητας διάχυσης του ICSee</td>
  </tr>
  <tr>
    <td>Β1</td>
    <td>Προσφορά του κώδικα της εφαρμογής ως ανοικτού κώδικα.</td>
    <td>Το έργο βρισκεται στο GitHub σε αυτόν τον σύνδεσμο:
        https://github.com/ellak-monades-aristeias/ICSee-v1</td>
  </tr>
</table>

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
<a href="http://www.scify.gr/site/en/"><img src="http://www.scify.gr/site/images/scify/scify_logo_108.png"></a>
<a href="https://ellak.gr/" title="Ελεύθερο Λογισμικό / Λογισμικό ανοιχτού κώδικα" rel="home"><img style="height: 90px; margin-left: 20px;"  src="https://ellak.gr/wp-content/uploads/2015/09/el-lak.png" alt="Ελεύθερο Λογισμικό / Λογισμικό ανοιχτού κώδικα" title="Ελεύθερο Λογισμικό / Λογισμικό ανοιχτού κώδικα"></a>
