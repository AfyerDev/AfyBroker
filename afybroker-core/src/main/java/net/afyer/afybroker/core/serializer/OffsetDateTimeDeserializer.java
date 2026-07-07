package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

final class OffsetDateTimeDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return OffsetDateTime.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        long epochDay = in.readLong();
        long nanoOfDay = in.readLong();
        int offsetSeconds = in.readInt();
        OffsetDateTime value = OffsetDateTime.of(LocalDate.ofEpochDay(epochDay),
                LocalTime.ofNanoOfDay(nanoOfDay), ZoneOffset.ofTotalSeconds(offsetSeconds));
        in.addRef(value);
        return value;
    }
}
