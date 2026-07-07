package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.Instant;

final class InstantSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        Instant value = (Instant) obj;
        int ref = out.writeObjectBegin(Instant.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(2);
            out.writeString("seconds");
            out.writeString("nanos");
            out.writeObjectBegin(Instant.class.getName());
        }
        out.writeLong(value.getEpochSecond());
        out.writeInt(value.getNano());
        out.writeObjectEnd();
    }
}
