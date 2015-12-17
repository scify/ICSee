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
    <th width="50px">Α/ Α</th>
    <th>Περιγραφή δράσης</th>
    <th>Τεκμηρίωση</th>
  </tr>
  <tr>
    <td>1</td>
    <td>ICSee Android Application: Εφαρμογή έτοιμη για χρήση σε smartphones / tablets με
        λειτουργικό σύστημα Android. Στα χαρακτηριστικά θα περιλαμβάνονται, πέραν των ήδη
        ανεπτυγμένων χαρακτηριστικών:</td>
    <td><ul><li>Λήψη στιγμιοτύπου υψηλής ανάλυσης για καλύτερη προβολή</li>
                <li>Δυνατότητα προσαρμογής της μεγέθυνσης της εικόνας από το χρήστη.</li><li>Δημιουργία εκπαιδευτικής διαδικασίας (tutorial) για να μπορεί ο χρήστης να μάθει να
                το χρησιμοποιεί εύκολα.</li></ul></td>
                <td>Το ICSee υπάρχει διαθέσιμο για δωρεάν κατέβασμα στο Google Play σε αυτόν τον σύνδεσμο: https://goo.gl/WnNyzn</td>
  </tr>
  <tr>
    <td>2</td>
    <td colspan="1">Ο κώδικας της εφαρμογής διαθέσιμος με πολύ ανοικτές άδειες χρήσης σε δημόσιο

                    αποθετήριο, προκειμένου όλοι να μπορούν να την εξελίξουν / εξειδικεύσουν.</td>
                    <td>Ο κώδικας της εφαρμογής είναι διαθέσιμος σε αυτόν τον σύνδεσμο: https://github.com/scify/ICSee</td>
  </tr>
  <tr>
    <td>3</td>
    <td>Διασφάλιση ότι η εφαρμογή θα λειτουργεί σε κάποιες δημοφιλείς συσκευές οι οποίες έχουν

        τα επιθυμητά χαρακτηριστικά της ομάδας-στόχου (π.χ. οθόνη τουλάχιστον Χ ιντσών) ή

        απαιτήσεων της εφαρμογής ώστε να λειτουργεί ικανοποιητικά (π.χ. κάμερα Υ Μpixels)</td>
  </tr>
  <tr>
    <td>4</td>
    <td>Σύντομο report με τα αποτελέσματα των tests με τελικούς χρήστες και με συνεισφορά

        επιστήμονα ειδικού σε προβλήματα όρασης.</td>
    <td></td>
  </tr>
  <tr>
    <td>5</td>
    <td colspan="1">Υποστηρικτικό υλικό.</td>
    <td></td>
  </tr>
  <tr>
    <td>6</td>
    <td>Προώθηση του κοινού project ICSee στα κανάλια επικοινωνίας της SciFY.</td>
    <td>Στο Newsletter της SciFY (http://eepurl.com/bAuv11)
        Στα Social media της SciFY
        <ul>
        <li>Google+ (https://goo.gl/cLQQVR)</li>
        <li>Facebook
        (https://www.facebook.com/SciFY.org)</li>
        <li>Twitter
        (https://twitter.com/scify_org)</li>
        </ul>
        Ειδική παρουσίαση στην ημέρα Λευκού Μπαστουνιού, όπως φαίνεται και στο προφίλ μας στο Facebook</td>
  </tr>
  <tr>
  <td>7</td>
  <td>Προώθηση σε κοινότητες και φορείς τυφλότητας.</td>
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
<table>
<tr>
<td>
<a href="http://www.scify.gr/site/en/"><img src="http://www.scify.gr/site/images/scify/scify_logo_108.png"></a>
</td>
<td>
<a href="http://www.latsis-foundation.org/" title="Ίδρυμα Λάτση" rel="home"><img src="http://www.latsis-foundation.org/img/iePngs/logoEll.png" alt="Ίδρυμα Λάτση" title="Ίδρυμα Λάτση"></a>
<td>
</tr>
</table>
=======
