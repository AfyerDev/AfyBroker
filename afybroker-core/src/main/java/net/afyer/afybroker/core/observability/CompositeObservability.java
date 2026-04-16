package net.afyer.afybroker.core.observability;

import com.alipay.remoting.ConnectionEventType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CompositeObservability implements Observability {

    private final List<Observability> delegates;

    public CompositeObservability(List<Observability> delegates) {
        this.delegates = Collections.unmodifiableList(new ArrayList<>(delegates));
    }

    public static Observability of(Observability... delegates) {
        List<Observability> items = new ArrayList<>();
        for (Observability delegate : delegates) {
            if (delegate == null || delegate == Observability.NOOP) {
                continue;
            }
            items.add(delegate);
        }
        if (items.isEmpty()) {
            return Observability.NOOP;
        }
        if (items.size() == 1) {
            return items.get(0);
        }
        return new CompositeObservability(items);
    }

    @Override
    public void onLifecycle(LifecycleState state) {
        for (Observability delegate : delegates) {
            delegate.onLifecycle(state);
        }
    }

    @Override
    public void onConnection(ConnectionEventType state) {
        for (Observability delegate : delegates) {
            delegate.onConnection(state);
        }
    }

    @Override
    public void onRpc(RpcObservation observation) {
        for (Observability delegate : delegates) {
            delegate.onRpc(observation);
        }
    }

    @Override
    public void onPlayer(PlayerObservation observation) {
        for (Observability delegate : delegates) {
            delegate.onPlayer(observation);
        }
    }

    @Override
    public void close() {
        for (Observability delegate : delegates) {
            delegate.close();
        }
    }
}
