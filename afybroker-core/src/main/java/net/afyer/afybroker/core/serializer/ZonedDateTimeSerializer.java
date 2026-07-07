package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.ZonedDateTime;

final class ZonedDateTimeSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        ZonedDateTime value = (ZonedDateTime) obj;
        int ref = out.writeObjectBegin(ZonedDateTime.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(4);
            out.writeString("epochDay");
            out.writeString("nanoOfDay");
            out.writeString("zoneId");
            out.writeString("offsetSeconds");
            out.writeObjectBegin(ZonedDateTime.class.getName());
        }
        out.writeLong(value.toLocalDate().toEpochDay());
        out.writeLong(value.toLocalTime().toNanoOfDay());
        out.writeString(value.getZone().getId());
        out.writeInt(value.getOffset().getTotalSeconds());
        out.writeObjectEnd();
    }
}
