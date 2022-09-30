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

package gr.scify.icsee.interfaces;

import android.content.Context;
import android.os.Bundle;

public interface IAnalyticsLogger {
    void logEvent(Context c, String eventName, String eventValue, Bundle additionalPayload);
}
