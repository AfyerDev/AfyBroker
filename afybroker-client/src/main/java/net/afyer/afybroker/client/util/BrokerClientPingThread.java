package net.afyer.afybroker.client.util;

import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.exception.ClientRegistrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BrokerClientPingThread {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerClientPingThread.class);

    private final BrokerClient brokerClient;
    private volatile boolean stopped;
    private final Thread thread;

    public BrokerClientPingThread(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
        Thread thread = new Thread(this::run, "afybroker-ping");
        thread.setDaemon(false);
        this.thread = thread;
    }

    public void startup() {
        thread.start();
    }

    public boolean isStarted() {
        return thread.isAlive();
    }

    public void shutdown() {
        stopped = true;
        thread.interrupt();
    }

    private void run() {
        boolean warned = false;
        try {
            while (!stopped && !Thread.currentThread().isInterrupted()) {
                try {
                    brokerClient.ping();
                    return;
                } catch (ClientRegistrationException e) {
                    if (!warned && !stopped) {
                        warned = true;
                        LOGGER.warn("Broker registration failed; will retry: {}", e.getMessage());
                    }
                } catch (InterruptedException e) {
                    throw e;
                } catch (Exception e) {
                    if (!stopped) {
                        LOGGER.error("Ping to the broker server failed!", e);
                    }
                }
                if (stopped) return;
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (!stopped) {
                LOGGER.error("Ping to the broker server interrupted!", e);
            }
        }
    }
}
