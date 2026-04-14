package net.afyer.afybroker.core.observability;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class PrometheusObservability implements Observability {

    private final String role;
    private final String localType;
    private final CollectorRegistry registry;
    private final HTTPServer server;
    private final Counter lifecycleCounter;
    private final Counter connectionCounter;
    private final Gauge activeConnections;
    private final Counter rpcCounter;
    private final Histogram rpcDuration;
    private final Counter playerCounter;
    private final Gauge onlinePlayers;

    public PrometheusObservability(Role role, String localType, PrometheusObservabilityOptions options) throws IOException {
        this.role = role.name().toLowerCase();
        this.localType = localType;
        this.registry = new CollectorRegistry();
        this.lifecycleCounter = Counter.build()
                .name("afybroker_lifecycle_events_total")
                .help("AfyBroker lifecycle event count.")
                .labelNames("role", "state", "local_type")
                .register(registry);
        this.connectionCounter = Counter.build()
                .name("afybroker_connection_events_total")
                .help("AfyBroker connection event count.")
                .labelNames("role", "state", "local_type")
                .register(registry);
        this.activeConnections = Gauge.build()
                .name("afybroker_active_connections")
                .help("Current active AfyBroker connections.")
                .labelNames("role", "local_type")
                .register(registry);
        this.rpcCounter = Counter.build()
                .name("afybroker_rpc_requests_total")
                .help("AfyBroker RPC request count.")
                .labelNames("role", "phase", "request_type", "service", "method", "result", "local_type")
                .register(registry);
        this.rpcDuration = Histogram.build()
                .name("afybroker_rpc_duration_seconds")
                .help("AfyBroker RPC request duration.")
                .labelNames("role", "phase", "request_type", "service", "method", "result", "local_type")
                .register(registry);
        this.playerCounter = Counter.build()
                .name("afybroker_player_events_total")
                .help("AfyBroker player event count.")
                .labelNames("role", "event", "local_type")
                .register(registry);
        this.onlinePlayers = Gauge.build()
                .name("afybroker_online_players")
                .help("Current online players observed by AfyBroker.")
                .labelNames("role", "local_type")
                .register(registry);
        this.server = new HTTPServer(new InetSocketAddress(options.getHost(), options.getPort()), registry, options.isDaemon());
    }

    @Override
    public void onLifecycle(LifecycleState state) {
        lifecycleCounter.labels(role, state.name().toLowerCase(), localType).inc();
    }

    @Override
    public void onConnection(ConnectionState state) {
        connectionCounter.labels(role, state.name().toLowerCase(), localType).inc();
        if (state == ConnectionState.CONNECTED) {
            activeConnections.labels(role, localType).inc();
        } else if (state == ConnectionState.CLOSED) {
            activeConnections.labels(role, localType).dec();
        }
    }

    @Override
    public void onRpc(RpcObservation observation) {
        String phase = observation.getPhase().name().toLowerCase();
        String requestType = ObservabilitySupport.labelValue(observation.getRequestType());
        String service = ObservabilitySupport.labelValue(observation.getServiceInterface());
        String method = ObservabilitySupport.labelValue(observation.getMethodName());
        String result = observation.isSuccess() ? "success" : "failure";
        rpcCounter.labels(role, phase, requestType, service, method, result, localType).inc();
        rpcDuration.labels(role, phase, requestType, service, method, result, localType)
                .observe(observation.getDurationNanos() / 1_000_000_000D);
    }

    @Override
    public void onPlayer(PlayerObservation observation) {
        if (observation.getEventType() != null) {
            String event = observation.getEventType().name().toLowerCase();
            playerCounter.labels(role, event, localType).inc();
        }
        onlinePlayers.labels(role, localType).set(observation.getOnlinePlayers());
    }

    @Override
    public void close() {
        server.close();
    }
}
