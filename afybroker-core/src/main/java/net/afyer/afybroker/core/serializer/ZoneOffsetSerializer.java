package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.ZoneOffset;

final class ZoneOffsetSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        ZoneOffset value = (ZoneOffset) obj;
        int ref = out.writeObjectBegin(ZoneOffset.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(1);
            out.writeString("totalSeconds");
            out.writeObjectBegin(ZoneOffset.class.getName());
        }
        out.writeInt(value.getTotalSeconds());
        out.writeObjectEnd();
    }
}
