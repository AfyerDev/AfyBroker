package net.afyer.afybroker.core.observability;

public interface Observability extends AutoCloseable {

    Observability NOOP = new NoopObservability();

    default void onLifecycle(LifecycleState state) {
    }

    default void onConnection(ConnectionState state) {
    }

    default void onRpc(RpcObservation observation) {
    }

    default void onPlayer(PlayerObservation observation) {
    }

    @Override
    default void close() {
    }
}
