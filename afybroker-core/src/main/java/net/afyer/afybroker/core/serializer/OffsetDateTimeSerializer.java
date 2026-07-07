package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.OffsetDateTime;

final class OffsetDateTimeSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        OffsetDateTime value = (OffsetDateTime) obj;
        int ref = out.writeObjectBegin(OffsetDateTime.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(3);
            out.writeString("epochDay");
            out.writeString("nanoOfDay");
            out.writeString("offsetSeconds");
            out.writeObjectBegin(OffsetDateTime.class.getName());
        }
        out.writeLong(value.toLocalDate().toEpochDay());
        out.writeLong(value.toLocalTime().toNanoOfDay());
        out.writeInt(value.getOffset().getTotalSeconds());
        out.writeObjectEnd();
    }
}
