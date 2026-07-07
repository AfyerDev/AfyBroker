package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.Duration;

final class DurationSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        Duration value = (Duration) obj;
        int ref = out.writeObjectBegin(Duration.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(2);
            out.writeString("seconds");
            out.writeString("nanos");
            out.writeObjectBegin(Duration.class.getName());
        }
        out.writeLong(value.getSeconds());
        out.writeInt(value.getNano());
        out.writeObjectEnd();
    }
}
