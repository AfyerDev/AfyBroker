package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.LocalDateTime;

final class LocalDateTimeSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        LocalDateTime value = (LocalDateTime) obj;
        int ref = out.writeObjectBegin(LocalDateTime.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(2);
            out.writeString("epochDay");
            out.writeString("nanoOfDay");
            out.writeObjectBegin(LocalDateTime.class.getName());
        }
        out.writeLong(value.toLocalDate().toEpochDay());
        out.writeLong(value.toLocalTime().toNanoOfDay());
        out.writeObjectEnd();
    }
}
