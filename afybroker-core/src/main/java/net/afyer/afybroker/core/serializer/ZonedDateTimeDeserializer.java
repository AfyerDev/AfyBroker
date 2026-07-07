package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

final class ZonedDateTimeDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return ZonedDateTime.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        long epochDay = in.readLong();
        long nanoOfDay = in.readLong();
        String zoneId = in.readString();
        int offsetSeconds = in.readInt();
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.ofEpochDay(epochDay),
                LocalTime.ofNanoOfDay(nanoOfDay));
        ZonedDateTime value = ZonedDateTime.ofLocal(dateTime, ZoneId.of(zoneId),
                ZoneOffset.ofTotalSeconds(offsetSeconds));
        in.addRef(value);
        return value;
    }
}
