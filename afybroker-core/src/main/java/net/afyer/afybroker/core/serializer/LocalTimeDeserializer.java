package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.LocalTime;

final class LocalTimeDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return LocalTime.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        long nanoOfDay = in.readLong();
        LocalTime value = LocalTime.ofNanoOfDay(nanoOfDay);
        in.addRef(value);
        return value;
    }
}
