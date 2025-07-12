package net.afyer.afybroker.client;

import com.alipay.remoting.LifeCycleException;
import com.alipay.remoting.exception.RemotingException;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.server.BrokerServer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Nipuru
 * @since 2025/07/12 12:11
 */
@Disabled
public class BrokerServiceTest {

    private static BrokerServer brokerServer;
    private static final int BROKER_PORT = 11200;
    private static final String BROKER_HOST = "localhost";

    /**
     * 日志服务接口
     */
    public interface LogService {
        void info(String message);
        void error(String message, String exception);
        String getLastLog();
    }

    /**
     * 日志服务实现
     */
    public static class LogServiceImpl implements LogService {
        private String lastLog = "";

        @Override
        public void info(String message) {
            String log = "[INFO] " + message;
            System.out.println(log);
            lastLog = log;
        }

        @Override
        public void error(String message, String exception) {
            String log = "[ERROR] " + message + " - " + exception;
            System.out.println(log);
            lastLog = log;
        }

        @Override
        public String getLastLog() {
            return lastLog;
        }
    }

    @BeforeAll
    static void setUp() throws IOException {
        // 启动 BrokerServer
        brokerServer = BrokerServer.builder()
                .port(BROKER_PORT)
                .build();

        brokerServer.startup();

        System.out.println("BrokerServer started on port: " + BROKER_PORT);

        // 等待服务器完全启动
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterAll
    static void tearDown() {
        if (brokerServer != null) {
            brokerServer.shutdown();
            System.out.println("BrokerServer shutdown");
        }
    }

    @Test
    void testLogServiceRpc() throws Exception {
        CountDownLatch latch = new CountDownLatch(2);
        
        // 启动服务提供者线程
        Thread providerThread = new Thread(() -> {
            try {
                startLogServiceProvider(latch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // 启动服务消费者线程
        Thread consumerThread = new Thread(() -> {
            try {
                startLogServiceConsumer(latch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        providerThread.start();
        consumerThread.start();
        
        // 等待两个客户端都完成
        latch.await(30, TimeUnit.SECONDS);
        
        providerThread.join();
        consumerThread.join();
        
        System.out.println("测试完成！");
    }

    /**
     * 启动日志服务提供者
     */
    private void startLogServiceProvider(CountDownLatch latch) throws Exception {
        System.out.println("启动日志服务提供者...");
        
        // 创建服务提供者客户端
        BrokerClient providerClient = BrokerClient.newBuilder()
                .host(BROKER_HOST)
                .port(BROKER_PORT)
                .name("log-service-provider")
                .type(BrokerClientType.UNKNOWN)
                .addTag("log-provider")
                .registerService(LogService.class, new LogServiceImpl(), "log-provider")
                .build();
        
        try {
            // 启动客户端
            providerClient.startup();
            providerClient.ping();
            
            System.out.println("日志服务提供者启动成功，等待服务调用...");
            
            // 保持服务运行15秒
            Thread.sleep(15000);
            
        } catch (LifeCycleException | RemotingException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            providerClient.shutdown();
            latch.countDown();
            System.out.println("日志服务提供者已关闭");
        }
    }

    /**
     * 启动日志服务消费者
     */
    private void startLogServiceConsumer(CountDownLatch latch) throws Exception {
        System.out.println("启动日志服务消费者...");
        
        // 等待服务提供者启动
        Thread.sleep(3000);
        
        // 创建服务消费者客户端
        BrokerClient consumerClient = BrokerClient.newBuilder()
                .host(BROKER_HOST)
                .port(BROKER_PORT)
                .name("log-service-consumer")
                .type(BrokerClientType.UNKNOWN)
                .addTag("log-consumer")
                .build();
        
        try {
            // 启动客户端
            consumerClient.startup();
            consumerClient.ping();
            
            System.out.println("日志服务消费者启动成功，开始调用服务...");
            
            // 获取远程日志服务代理
            LogService logService = consumerClient.getService(LogService.class);
            
            // 调用远程服务
            System.out.println("调用 info 方法...");
            logService.info("这是一条测试信息");
            
            Thread.sleep(1000);
            
            System.out.println("调用 error 方法...");
            logService.error("这是一个错误信息", "NullPointerException");
            
            Thread.sleep(1000);
            
            System.out.println("调用 getLastLog 方法...");
            String lastLog = logService.getLastLog();
            System.out.println("获取到的最后一条日志: " + lastLog);
            
            System.out.println("服务调用完成！");
            
        } catch (LifeCycleException | RemotingException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            consumerClient.shutdown();
            latch.countDown();
            System.out.println("日志服务消费者已关闭");
        }
    }
}
