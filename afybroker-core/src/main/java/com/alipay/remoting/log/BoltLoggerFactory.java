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
package com.alipay.remoting.log;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

/**
 * Customized logger factory
 *
 * This can use middleware-log in sofa-common-tools to detect specific log implementation and initialize with the given log template.
 *
 * @author tsui
 * @version $Id: BoltLoggerFactory.java, v 0.1 2017-09-05 16:06 tsui Exp $
 */
public class BoltLoggerFactory {
    public static Logger getLogger(Class<?> clazz) {
        if (clazz == null) {
            return getLogger("");
        }
        return getLogger(clazz.getCanonicalName());
    }

    public static Logger getLogger(String name) {
        if (name == null || name.isEmpty()) {
            return LoggerFactory.getLogger("");
        }
        return LoggerFactory.getLogger(name);
    }
}