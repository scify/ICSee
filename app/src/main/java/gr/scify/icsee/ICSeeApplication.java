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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class ICSeeApplication extends Application {

    public static RequestQueue queue;
    public static String TAG = ICSeeApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(getApplicationContext());
    }
}
