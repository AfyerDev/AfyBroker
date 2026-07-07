package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.Year;

final class YearSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        Year value = (Year) obj;
        int ref = out.writeObjectBegin(Year.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(1);
            out.writeString("year");
            out.writeObjectBegin(Year.class.getName());
        }
        out.writeInt(value.getValue());
        out.writeObjectEnd();
    }
}
