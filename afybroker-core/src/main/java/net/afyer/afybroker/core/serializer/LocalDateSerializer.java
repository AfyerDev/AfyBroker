package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.LocalDate;

final class LocalDateSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        LocalDate value = (LocalDate) obj;
        int ref = out.writeObjectBegin(LocalDate.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(1);
            out.writeString("epochDay");
            out.writeObjectBegin(LocalDate.class.getName());
        }
        out.writeLong(value.toEpochDay());
        out.writeObjectEnd();
    }
}
