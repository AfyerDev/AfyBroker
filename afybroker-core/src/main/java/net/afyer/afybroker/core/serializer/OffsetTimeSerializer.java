package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.OffsetTime;

final class OffsetTimeSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        OffsetTime value = (OffsetTime) obj;
        int ref = out.writeObjectBegin(OffsetTime.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(2);
            out.writeString("nanoOfDay");
            out.writeString("offsetSeconds");
            out.writeObjectBegin(OffsetTime.class.getName());
        }
        out.writeLong(value.toLocalTime().toNanoOfDay());
        out.writeInt(value.getOffset().getTotalSeconds());
        out.writeObjectEnd();
    }
}
