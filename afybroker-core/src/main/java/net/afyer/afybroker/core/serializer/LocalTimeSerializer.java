package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.LocalTime;

final class LocalTimeSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        LocalTime value = (LocalTime) obj;
        int ref = out.writeObjectBegin(LocalTime.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(1);
            out.writeString("nanoOfDay");
            out.writeObjectBegin(LocalTime.class.getName());
        }
        out.writeLong(value.toNanoOfDay());
        out.writeObjectEnd();
    }
}
