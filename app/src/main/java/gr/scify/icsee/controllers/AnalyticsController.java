/*******************************************************************************
 *
 * Copyright 2022 SciFY NPO <info@scify.org>.
 *
 * This product is part of the ICSee Free Software.
 * For more information about ICSee visit
 *
 * 	https://www.scify.gr/site/en/impact-areas-en/assistive-technologies/icsee
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
 *
 *******************************************************************************/

package gr.scify.icsee.controllers;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import java.util.Locale;

import gr.scify.icsee.BuildConfig;
import gr.scify.icsee.interfaces.IAnalyticsLogger;

public class AnalyticsController {

	/**
	 * Singleton
	 */
	private static AnalyticsController instance = null;

	protected IAnalyticsLogger analyticsLogger;

	private AnalyticsController() {
		analyticsLogger = new GeneralAnalyticsLogger();
	}

	public static AnalyticsController getInstance() {
		if(instance == null)
			instance = new AnalyticsController();
		return instance;
	}

	/**
	 * Send a custom event
	 * @param eventName the name of the event
	 * @param eventValue the value of the event
	 * @param additionalPayload a set of key-value pairs that will be sent alongside the event, as a payload
	 */
	public void sendEvent(Context c, String eventName, String eventValue, Bundle additionalPayload){
		additionalPayload.putString("language", getCurrentLocale(c).getLanguage());
		additionalPayload.putString("device_name", android.os.Build.MODEL);
		additionalPayload.putString("device_manufacturer", android.os.Build.MANUFACTURER);
		additionalPayload.putString("device_version", Build.VERSION.RELEASE);
		additionalPayload.putString("device_api_level", String.valueOf(Build.VERSION.SDK_INT));
		additionalPayload.putString("app_version_name", BuildConfig.VERSION_NAME);
		additionalPayload.putString("app_version_code", String.valueOf(BuildConfig.VERSION_CODE));
		analyticsLogger.logEvent(c, eventName, eventValue, additionalPayload);
	}

	public static Locale getCurrentLocale(Context context){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
			return context.getResources().getConfiguration().getLocales().get(0);
		} else{
			//noinspection deprecation
			return context.getResources().getConfiguration().locale;
		}
	}
}
