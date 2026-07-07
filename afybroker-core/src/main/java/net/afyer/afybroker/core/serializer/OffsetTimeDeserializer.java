package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

final class OffsetTimeDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return OffsetTime.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        long nanoOfDay = in.readLong();
        int offsetSeconds = in.readInt();
        OffsetTime value = OffsetTime.of(LocalTime.ofNanoOfDay(nanoOfDay),
                ZoneOffset.ofTotalSeconds(offsetSeconds));
        in.addRef(value);
        return value;
    }
}
