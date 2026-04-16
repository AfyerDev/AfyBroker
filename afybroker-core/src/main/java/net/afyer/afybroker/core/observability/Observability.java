package net.afyer.afybroker.core.observability;

import com.alipay.remoting.ConnectionEventType;

public interface Observability extends AutoCloseable {

    Observability NOOP = new NoopObservability();

    default void onLifecycle(LifecycleState state) {
    }

    default void onConnection(ConnectionEventType state) {
    }

    default void onRpc(RpcObservation observation) {
    }

    default void onPlayer(PlayerObservation observation) {
    }

    @Override
    default void close() {
    }
}
