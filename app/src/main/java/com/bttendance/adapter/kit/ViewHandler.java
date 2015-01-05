/* 
 * Copyright (c) 2013 Mobs & Geeks
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bttendance.adapter.kit;

import android.view.View;
import android.widget.ListAdapter;

/**
 * Interface that provides a mechanism to customize Views. You can setup one {@link com.bttendance.adapter.kit.ViewHandler}
 * per {@link android.view.View}.
 *
 * @param <T> The model you will be using for customizing the associated {@link android.view.View}.
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
public interface ViewHandler<T> {

    /**
     * Allows you to make changes to an associated View by supplying the adapter, the View's
     * parent, an instance of the data associated with the position and the position itself.
     *
     * @param adapter  The adapter to which this {@link com.bttendance.adapter.kit.ViewHandler} is assigned to.
     * @param parent   Parent for the View that is being handled. Usually the custom layout
     *                 instance for individual views and a {@link android.widget.ListView} or a {@link android.widget.GridView}
     *                 for the layout.
     * @param view     The {@link android.view.View} that has to be handled.
     * @param instance The instance that is associated with the position.
     * @param position Position of the item within the adapter's data set.
     */
    void handleView(ListAdapter adapter, View parent, View view, T instance, int position);
}
