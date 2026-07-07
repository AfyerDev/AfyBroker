package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.Instant;

final class InstantDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return Instant.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        long seconds = in.readLong();
        int nanos = in.readInt();
        Instant value = Instant.ofEpochSecond(seconds, nanos);
        in.addRef(value);
        return value;
    }
}
