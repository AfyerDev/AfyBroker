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
package com.alipay.remoting;

import com.alipay.remoting.config.*;

/**
 * @author chengyi (mark.lx@antfin.com) 2018-11-07 15:22
 */
public abstract class AbstractBoltClient extends AbstractLifeCycle implements BoltClient {

    private final BoltOptions options;

    public AbstractBoltClient() {
        this.options = new BoltOptions();
        if (ConfigManager.conn_reconnect_switch()) {
            option(BoltClientOption.CONN_RECONNECT_SWITCH, true);
        } else {
            option(BoltClientOption.CONN_RECONNECT_SWITCH, false);
        }

        if (ConfigManager.conn_monitor_switch()) {
            option(BoltClientOption.CONN_MONITOR_SWITCH, true);
        } else {
            option(BoltClientOption.CONN_MONITOR_SWITCH, false);
        }
    }

    @Override
    public <T> T option(BoltOption<T> option) {
        return options.option(option);
    }

    @Override
    public <T> Configuration option(BoltOption<T> option, T value) {
        options.option(option, value);
        return this;
    }

}
