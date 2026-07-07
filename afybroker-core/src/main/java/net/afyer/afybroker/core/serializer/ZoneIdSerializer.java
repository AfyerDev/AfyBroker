package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.ZoneId;

class ZoneIdSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        ZoneId value = (ZoneId) obj;
        int ref = out.writeObjectBegin(ZoneId.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(1);
            out.writeString("id");
            out.writeObjectBegin(ZoneId.class.getName());
        }
        out.writeString(value.getId());
        out.writeObjectEnd();
    }
}
