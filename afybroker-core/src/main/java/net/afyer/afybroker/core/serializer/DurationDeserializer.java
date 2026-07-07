package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.Duration;

final class DurationDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return Duration.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        long seconds = in.readLong();
        int nanos = in.readInt();
        Duration value = Duration.ofSeconds(seconds, nanos);
        in.addRef(value);
        return value;
    }
}
