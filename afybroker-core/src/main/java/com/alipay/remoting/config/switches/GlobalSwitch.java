/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.remoting.config.switches;

import java.util.BitSet;

/**
 * Global switches used in client or server
 * <p>
 * NOTICE:<br>
 * 1. system settings will take effect in all bolt client or server instances in one process<br>
 * 2. user settings will only take effect in the current instance of bolt client or server.
 * <p/>
 *
 * @author tsui
 * @version $Id: GlobalSwitch.java, v 0.1 2017-08-03 15:50 tsui Exp $
 */
@Deprecated
public class GlobalSwitch implements Switch {

    /** user settings */
    private BitSet userSettings = new BitSet();

    /**
     * Init with system default value
     *   if settings exist by system property then use system property at first;
     *   if no settings exist by system property then use default value in {@link com.alipay.remoting.config.Configs}
     * All these settings can be overwrite by user api settings.
     */
    public GlobalSwitch() {

    }

    // ~~~ public methods
    @Override
    public void turnOn(int index) {
        this.userSettings.set(index);
    }

    @Override
    public void turnOff(int index) {
        this.userSettings.clear(index);
    }

    @Override
    public boolean isOn(int index) {
        return this.userSettings.get(index);
    }
}