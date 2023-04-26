/*******************************************************************************
 *
 * Copyright 2013 SciFY NPO <info@scify.org>.
 *
 * This product is part of the NewSum Free Software.
 * For more information about NewSum visit
 *
 * 	http://www.scify.gr/site/en/our-projects/completed-projects/newsum-menu-en
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * If this code or its output is used, extended, re-engineered, integrated, 
 * or embedded to any extent in another software or hardware, there MUST be 
 * an explicit attribution to this work in the resulting source code, 
 * the packaging (where such packaging exists), or user interface 
 * (where such an interface exists). 
 * The attribution must be of the form "Powered by NewSum, SciFY" 
 *
 * @contributor NaSOS (nasos.loukas@gmail.com)
 *******************************************************************************/
package gr.scify.icsee;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.os.strictmode.Violation;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import io.sentry.SentryLevel;
import io.sentry.android.core.SentryAndroid;


public class ICSeeApplication extends Application {

    public static RequestQueue queue;
    public static String TAG = ICSeeApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        // Uncomment this code to receive strict mode warnings
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                            .detectActivityLeaks()
//                            .detectAll()
//                    .detectLeakedClosableObjects()
//                    .penaltyListener(this.getMainExecutor(), (Violation v) -> {
//                        v.fillInStackTrace();
//                        v.printStackTrace();
//                    })
//                    .build());
//        }
        SentryAndroid.init(this, options -> {
            options.setDsn(BuildConfig.SENTRY_DSN);
            // Add a callback that will be used before the event is sent to Sentry.
            // With this callback, you can modify the event or, when returning null, also discard the event.
            options.setBeforeSend((event, hint) -> {
                if (SentryLevel.DEBUG.equals(event.getLevel()))
                    return null;
                else
                    return event;
            });
        });
        queue = Volley.newRequestQueue(this);
    }
}
