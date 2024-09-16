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

/**
 * Reconnect manager interface.
 *
 * @author chengyi (mark.lx@antfin.com) 2018-11-05 17:43
 */
public interface Reconnector extends LifeCycle {

    /**
     * Do reconnecting in async mode.
     *
     * @param url target url
     */
    void reconnect(Url url);

    /**
     * Disable reconnect to the target url.
     *
     * @param url target url
     */
    void disableReconnect(Url url);

    /**
     * Enable reconnect to the target url.
     *
     * @param url target url
     */
    void enableReconnect(Url url);
}
