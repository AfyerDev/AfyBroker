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
package com.alipay.remoting.rpc;

import com.alipay.remoting.*;
import com.alipay.remoting.config.BoltClientOption;
import com.alipay.remoting.config.BoltGenericOption;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.log.BoltLoggerFactory;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.alipay.remoting.rpc.protocol.UserProcessorRegisterHelper;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client for Rpc.
 *
 * @author jiangping
 * @version $Id: RpcClient.java, v 0.1 2015-9-23 PM4:03:28 tao Exp $
 */
public class RpcClient extends AbstractBoltClient {

    private static final Logger logger = BoltLoggerFactory
            .getLogger("RpcRemoting");

    private final RpcTaskScanner taskScanner;
    private final ConcurrentHashMap<String, UserProcessor<?>> userProcessors;
    private final ConnectionEventHandler connectionEventHandler;
    private final ConnectionEventListener connectionEventListener;

    private ConnectionManager connectionManager;
    private Reconnector reconnectManager;
    private RemotingAddressParser addressParser;
    private DefaultConnectionMonitor connectionMonitor;
    private ConnectionMonitorStrategy monitorStrategy;

    // used in RpcClientAdapter (bolt-tr-adapter)
    protected RpcRemoting rpcRemoting;

    public RpcClient() {
        this.taskScanner = new RpcTaskScanner();
        this.userProcessors = new ConcurrentHashMap<String, UserProcessor<?>>();
        this.connectionEventHandler = new RpcConnectionEventHandler(this);
        this.connectionEventListener = new ConnectionEventListener();
    }

    /**
     * Shutdown.
     * <p>
     * Notice:<br>
     * <li>Rpc client can not be used any more after shutdown.
     * <li>If you need, you should destroy it, and instantiate another one.
     */
    @Override
    public void shutdown() {
        super.shutdown();

        this.connectionManager.shutdown();
        logger.warn("Close all connections from client side!");
        this.taskScanner.shutdown();
        logger.warn("Rpc client shutdown!");
        if (reconnectManager != null) {
            reconnectManager.shutdown();
        }
        if (connectionMonitor != null) {
            connectionMonitor.shutdown();
        }
        for (UserProcessor<?> userProcessor : userProcessors.values()) {
            if (userProcessor.isStarted()) {
                userProcessor.shutdown();
            }
        }
    }

    @Override
    public void startup() throws LifeCycleException {
        super.startup();

        for (UserProcessor<?> userProcessor : userProcessors.values()) {
            if (!userProcessor.isStarted()) {
                userProcessor.startup();
            }
        }

        if (this.addressParser == null) {
            this.addressParser = new RpcAddressParser();
        }

        ConnectionSelectStrategy connectionSelectStrategy = option(BoltGenericOption.CONNECTION_SELECT_STRATEGY);
        if (connectionSelectStrategy == null) {
            connectionSelectStrategy = new RandomSelectStrategy(this);
        }
        if (this.connectionManager == null) {
            DefaultClientConnectionManager defaultConnectionManager = new DefaultClientConnectionManager(
                    connectionSelectStrategy, new RpcConnectionFactory(userProcessors, this),
                    connectionEventHandler, connectionEventListener);
            defaultConnectionManager.setAddressParser(this.addressParser);
            defaultConnectionManager.startup();
            this.connectionManager = defaultConnectionManager;
        }
        this.rpcRemoting = new RpcClientRemoting(new RpcCommandFactory(), this.addressParser,
                this.connectionManager);
        this.taskScanner.add(this.connectionManager);
        this.taskScanner.startup();

        if (option(BoltClientOption.CONN_MONITOR_SWITCH)) {
            if (monitorStrategy == null) {
                connectionMonitor = new DefaultConnectionMonitor(new ScheduledDisconnectStrategy(),
                        this.connectionManager);
            } else {
                connectionMonitor = new DefaultConnectionMonitor(monitorStrategy,
                        this.connectionManager);
            }
            connectionMonitor.startup();
            logger.warn("Switch on connection monitor");
        }
        if (option(BoltClientOption.CONN_RECONNECT_SWITCH)) {
            reconnectManager = new ReconnectManager(this.connectionManager);
            reconnectManager.startup();

            connectionEventHandler.setReconnector(reconnectManager);
            logger.warn("Switch on reconnect manager");
        }
    }

    @Override
    public void oneway(final String address, final Object request) throws RemotingException,
            InterruptedException {
        ensureStarted();
        this.rpcRemoting.oneway(address, request, null);
    }

    @Override
    public void oneway(final String address, final Object request, final InvokeContext invokeContext)
            throws RemotingException,
            InterruptedException {
        ensureStarted();
        this.rpcRemoting.oneway(address, request, invokeContext);
    }

    @Override
    public void oneway(final Url url, final Object request) throws RemotingException,
            InterruptedException {
        ensureStarted();
        this.rpcRemoting.oneway(url, request, null);
    }

    @Override
    public void oneway(final Url url, final Object request, final InvokeContext invokeContext)
            throws RemotingException,
            InterruptedException {
        ensureStarted();
        this.rpcRemoting.oneway(url, request, invokeContext);
    }

    @Override
    public void oneway(final Connection conn, final Object request) throws RemotingException {
        ensureStarted();
        this.rpcRemoting.oneway(conn, request, null);
    }

    @Override
    public void oneway(final Connection conn, final Object request,
                       final InvokeContext invokeContext) throws RemotingException {
        ensureStarted();
        this.rpcRemoting.oneway(conn, request, invokeContext);
    }

    @Override
    public Object invokeSync(final String address, final Object request, final int timeoutMillis)
            throws RemotingException,
            InterruptedException {
        ensureStarted();
        return this.rpcRemoting.invokeSync(address, request, null, timeoutMillis);
    }

    @Override
    public Object invokeSync(final String address, final Object request,
                             final InvokeContext invokeContext, final int timeoutMillis)
            throws RemotingException,
            InterruptedException {
        ensureStarted();
        return this.rpcRemoting.invokeSync(address, request, invokeContext, timeoutMillis);
    }

    @Override
    public Object invokeSync(final Url url, final Object request, final int timeoutMillis)
            throws RemotingException,
            InterruptedException {
        ensureStarted();
        return this.invokeSync(url, request, null, timeoutMillis);
    }

    @Override
    public Object invokeSync(final Url url, final Object request,
                             final InvokeContext invokeContext, final int timeoutMillis)
            throws RemotingException,
            InterruptedException {
        ensureStarted();
        return this.rpcRemoting.invokeSync(url, request, invokeContext, timeoutMillis);
    }

    @Override
    public Object invokeSync(final Connection conn, final Object request, final int timeoutMillis)
            throws RemotingException,
            InterruptedException {
        ensureStarted();
        return this.rpcRemoting.invokeSync(conn, request, null, timeoutMillis);
    }

    @Override
    public Object invokeSync(final Connection conn, final Object request,
                             final InvokeContext invokeContext, final int timeoutMillis)
            throws RemotingException,
            InterruptedException {
        ensureStarted();
        return this.rpcRemoting.invokeSync(conn, request, invokeContext, timeoutMillis);
    }

    @Override
    public RpcResponseFuture invokeWithFuture(final String address, final Object request,
                                              final int timeoutMillis) throws RemotingException,
            InterruptedException {
        ensureStarted();
        return this.rpcRemoting.invokeWithFuture(address, request, null, timeoutMillis);
    }

    @Override
    public RpcResponseFuture invokeWithFuture(final String address, final Object request,
                                              final InvokeContext invokeContext,
                                              final int timeoutMillis) throws RemotingException,
            InterruptedException {
        ensureStarted();
        return this.rpcRemoting.invokeWithFuture(address, request, invokeContext, timeoutMillis);
    }

    @Override
    public RpcResponseFuture invokeWithFuture(final Url url, final Object request,
                                              final int timeoutMillis) throws RemotingException,
            InterruptedException {
        ensureStarted();
        return this.rpcRemoting.invokeWithFuture(url, request, null, timeoutMillis);
    }

    @Override
    public RpcResponseFuture invokeWithFuture(final Url url, final Object request,
                                              final InvokeContext invokeContext,
                                              final int timeoutMillis) throws RemotingException,
            InterruptedException {
        ensureStarted();
        return this.rpcRemoting.invokeWithFuture(url, request, invokeContext, timeoutMillis);
    }

    @Override
    public RpcResponseFuture invokeWithFuture(final Connection conn, final Object request,
                                              int timeoutMillis) throws RemotingException {
        ensureStarted();
        return this.rpcRemoting.invokeWithFuture(conn, request, null, timeoutMillis);
    }

    @Override
    public RpcResponseFuture invokeWithFuture(final Connection conn, final Object request,
                                              final InvokeContext invokeContext, int timeoutMillis)
            throws RemotingException {
        ensureStarted();
        return this.rpcRemoting.invokeWithFuture(conn, request, invokeContext, timeoutMillis);
    }

    @Override
    public void invokeWithCallback(final String address, final Object request,
                                   final InvokeCallback invokeCallback, final int timeoutMillis)
            throws RemotingException,
            InterruptedException {
        ensureStarted();
        this.rpcRemoting.invokeWithCallback(address, request, null, invokeCallback, timeoutMillis);
    }

    @Override
    public void invokeWithCallback(final String address, final Object request,
                                   final InvokeContext invokeContext,
                                   final InvokeCallback invokeCallback, final int timeoutMillis)
            throws RemotingException,
            InterruptedException {
        ensureStarted();
        this.rpcRemoting.invokeWithCallback(address, request, invokeContext, invokeCallback,
                timeoutMillis);
    }

    @Override
    public void invokeWithCallback(final Url url, final Object request,
                                   final InvokeCallback invokeCallback, final int timeoutMillis)
            throws RemotingException,
            InterruptedException {
        ensureStarted();
        this.rpcRemoting.invokeWithCallback(url, request, null, invokeCallback, timeoutMillis);
    }

    @Override
    public void invokeWithCallback(final Url url, final Object request,
                                   final InvokeContext invokeContext,
                                   final InvokeCallback invokeCallback, final int timeoutMillis)
            throws RemotingException,
            InterruptedException {
        ensureStarted();
        this.rpcRemoting.invokeWithCallback(url, request, invokeContext, invokeCallback,
                timeoutMillis);
    }

    @Override
    public void invokeWithCallback(final Connection conn, final Object request,
                                   final InvokeCallback invokeCallback, final int timeoutMillis)
            throws RemotingException {
        ensureStarted();
        this.rpcRemoting.invokeWithCallback(conn, request, null, invokeCallback, timeoutMillis);
    }

    @Override
    public void invokeWithCallback(final Connection conn, final Object request,
                                   final InvokeContext invokeContext,
                                   final InvokeCallback invokeCallback, final int timeoutMillis)
            throws RemotingException {
        ensureStarted();
        this.rpcRemoting.invokeWithCallback(conn, request, invokeContext, invokeCallback,
                timeoutMillis);
    }

    @Override
    public void addConnectionEventProcessor(ConnectionEventType type,
                                            ConnectionEventProcessor processor) {
        this.connectionEventListener.addConnectionEventProcessor(type, processor);
    }

    @Override
    public void registerUserProcessor(UserProcessor<?> processor) {
        UserProcessorRegisterHelper.registerUserProcessor(processor, this.userProcessors);
        // startup the processor if it registered after component startup
        if (isStarted() && !processor.isStarted()) {
            processor.startup();
        }
    }

    @Override
    public Connection createStandaloneConnection(String ip, int port, int connectTimeout)
            throws RemotingException {
        ensureStarted();
        return this.connectionManager.create(ip, port, connectTimeout);
    }

    @Override
    public Connection createStandaloneConnection(String address, int connectTimeout)
            throws RemotingException {
        ensureStarted();
        return this.connectionManager.create(address, connectTimeout);
    }

    @Override
    public void closeStandaloneConnection(Connection conn) {
        ensureStarted();
        if (null != conn) {
            conn.close();
        }
    }

    @Override
    public Connection getConnection(String address, int connectTimeout) throws RemotingException,
            InterruptedException {
        ensureStarted();
        Url url = this.addressParser.parse(address);
        return this.getConnection(url, connectTimeout);
    }

    @Override
    public Connection getConnection(Url url, int connectTimeout) throws RemotingException,
            InterruptedException {
        ensureStarted();
        url.setConnectTimeout(connectTimeout);
        return this.connectionManager.getAndCreateIfAbsent(url);
    }

    @Override
    public Map<String, List<Connection>> getAllManagedConnections() {
        ensureStarted();
        return this.connectionManager.getAll();
    }

    @Override
    public boolean checkConnection(String address) {
        return checkConnection(address, false);
    }

    @Override
    public boolean checkConnection(String address, boolean createIfAbsent) {
        return checkConnection(address, createIfAbsent, false);
    }

    @Override
    public boolean checkConnection(String address, boolean createIfAbsent, boolean createAsync) {
        ensureStarted();
        Url url = this.addressParser.parse(address);
        Connection conn = this.connectionManager.get(url.getUniqueKey());
        try {
            this.connectionManager.check(conn);
        } catch (Exception e) {
            logger.warn("check failed. address: {}, connection: {}", address, conn, e);
            if (createIfAbsent) {
                try {
                    if (createAsync) {
                        this.connectionManager.createConnectionInManagement(url);
                        return false;
                    } else {
                        Connection connection = this.connectionManager.getAndCreateIfAbsent(url);
                        try {
                            this.connectionManager.check(connection);
                            return true;
                        } catch (Exception ex0) {
                            return false;
                        }
                    }
                } catch (Exception ex) {
                    logger.warn("check failed and try create connection for {} also failed.",
                            address, e);
                    return false;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Close all connections of a address
     *
     * @param addr address
     */
    public void closeConnection(String addr) {
        ensureStarted();
        Url url = this.addressParser.parse(addr);
        if (option(BoltClientOption.CONN_RECONNECT_SWITCH) && reconnectManager != null) {
            reconnectManager.disableReconnect(url);
        }
        this.connectionManager.remove(url.getUniqueKey());
    }

    @Override
    public void closeConnection(Url url) {
        ensureStarted();
        if (option(BoltClientOption.CONN_RECONNECT_SWITCH) && reconnectManager != null) {
            reconnectManager.disableReconnect(url);
        }
        this.connectionManager.remove(url.getUniqueKey());
    }

    @Override
    public void enableConnHeartbeat(String address) {
        ensureStarted();
        Url url = this.addressParser.parse(address);
        this.enableConnHeartbeat(url);
    }

    @Override
    public void enableConnHeartbeat(Url url) {
        ensureStarted();
        if (null != url) {
            this.connectionManager.enableHeartbeat(this.connectionManager.get(url.getUniqueKey()));
        }
    }

    @Override
    public void disableConnHeartbeat(String address) {
        ensureStarted();
        Url url = this.addressParser.parse(address);
        this.disableConnHeartbeat(url);
    }

    @Override
    public void disableConnHeartbeat(Url url) {
        ensureStarted();
        if (null != url) {
            this.connectionManager.disableHeartbeat(this.connectionManager.get(url.getUniqueKey()));
        }
    }

    @Override
    public ConnectionManager getConnectionManager() {
        return this.connectionManager;
    }

    @Override
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public RemotingAddressParser getAddressParser() {
        return this.addressParser;
    }

    @Override
    public void setAddressParser(RemotingAddressParser addressParser) {
        this.addressParser = addressParser;
    }

    @Override
    public void setMonitorStrategy(ConnectionMonitorStrategy monitorStrategy) {
        this.monitorStrategy = monitorStrategy;
    }
}
