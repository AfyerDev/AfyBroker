package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.LocalDate;

final class LocalDateDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return LocalDate.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        long epochDay = in.readLong();
        LocalDate value = LocalDate.ofEpochDay(epochDay);
        in.addRef(value);
        return value;
    }
}
