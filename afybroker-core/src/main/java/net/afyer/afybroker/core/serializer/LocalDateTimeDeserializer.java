package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

final class LocalDateTimeDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return LocalDateTime.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        long epochDay = in.readLong();
        long nanoOfDay = in.readLong();
        LocalDateTime value = LocalDateTime.of(LocalDate.ofEpochDay(epochDay), LocalTime.ofNanoOfDay(nanoOfDay));
        in.addRef(value);
        return value;
    }
}
