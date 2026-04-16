package net.afyer.afybroker.core.observability;

import com.alipay.remoting.ConnectionEventType;
import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Gauge;
import io.prometheus.metrics.core.metrics.Histogram;
import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;

import java.io.IOException;

public class PrometheusObservability implements Observability {

    private static final String LABEL_ROLE = "role";
    private static final String LABEL_LOCAL_TYPE = "local_type";
    private static final String LABEL_STATE = "state";
    private static final String LABEL_PHASE = "phase";
    private static final String LABEL_SERVICE = "service";
    private static final String LABEL_METHOD = "method";
    private static final String LABEL_RESULT = "result";
    private static final String LABEL_EVENT = "event";
    private static final String RESULT_SUCCESS = "success";
    private static final String RESULT_FAILURE = "failure";

    private final HTTPServer server;
    private final Counter connectionCounter;
    private final Gauge activeConnections;
    private final Counter rpcCounter;
    private final Histogram rpcDuration;
    private final Counter playerCounter;
    private final Gauge onlinePlayers;

    public PrometheusObservability(Role role, String localType, PrometheusObservabilityOptions options) throws IOException {
        Labels constLabels = Labels.of(LABEL_ROLE, role.name().toLowerCase()).add(LABEL_LOCAL_TYPE, localType);
        PrometheusRegistry registry = new PrometheusRegistry();
        this.connectionCounter = Counter.builder()
                .name("afybroker_connection_events_total")
                .help("AfyBroker connection event count.")
                .constLabels(constLabels)
                .labelNames(LABEL_STATE)
                .register(registry);
        this.activeConnections = Gauge.builder()
                .name("afybroker_active_connections")
                .help("Current active AfyBroker connections.")
                .constLabels(constLabels)
                .register(registry);
        this.rpcCounter = Counter.builder()
                .name("afybroker_rpc_requests_total")
                .help("AfyBroker RPC request count.")
                .constLabels(constLabels)
                .labelNames(LABEL_PHASE, LABEL_SERVICE, LABEL_METHOD, LABEL_RESULT)
                .register(registry);
        this.rpcDuration = Histogram.builder()
                .name("afybroker_rpc_duration_seconds")
                .help("AfyBroker RPC request duration.")
                .constLabels(constLabels)
                .labelNames(LABEL_PHASE, LABEL_SERVICE, LABEL_METHOD, LABEL_RESULT)
                .register(registry);
        this.playerCounter = Counter.builder()
                .name("afybroker_player_events_total")
                .help("AfyBroker player event count.")
                .constLabels(constLabels)
                .labelNames(LABEL_EVENT)
                .register(registry);
        this.onlinePlayers = Gauge.builder()
                .name("afybroker_online_players")
                .help("Current online players observed by AfyBroker.")
                .constLabels(constLabels)
                .register(registry);
        HTTPServer.Builder serverBuilder = HTTPServer.builder()
                .hostname(options.getHost())
                .port(options.getPort())
                .registry(registry);
        this.server = serverBuilder.buildAndStart();
    }

    @Override
    public void onConnection(ConnectionEventType state) {
        connectionCounter.labelValues(state.name().toLowerCase()).inc();
        if (state == ConnectionEventType.CONNECT) {
            activeConnections.inc();
        } else if (state == ConnectionEventType.CLOSE) {
            activeConnections.dec();
        }
    }

    @Override
    public void onRpc(RpcObservation observation) {
        String phase = observation.getPhase().name().toLowerCase();
        String service = ObservabilitySupport.labelValue(observation.getServiceInterface());
        String method = ObservabilitySupport.labelValue(observation.getMethodName());
        String result = observation.isSuccess() ? RESULT_SUCCESS : RESULT_FAILURE;
        rpcCounter.labelValues(phase, service, method, result).inc();
        rpcDuration.labelValues(phase, service, method, result)
                .observe(observation.getDurationNanos() / 1_000_000_000D);
    }

    @Override
    public void onPlayer(PlayerObservation observation) {
        if (observation.getEventType() != null) {
            String event = observation.getEventType().name().toLowerCase();
            playerCounter.labelValues(event).inc();
        }
        onlinePlayers.set(observation.getOnlinePlayers());
    }

    @Override
    public void close() {
        server.close();
    }
}
