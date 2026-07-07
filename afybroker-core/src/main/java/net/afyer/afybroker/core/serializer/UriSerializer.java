package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.net.URI;

final class UriSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        URI value = (URI) obj;
        int ref = out.writeObjectBegin(URI.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(1);
            out.writeString("value");
            out.writeObjectBegin(URI.class.getName());
        }
        out.writeString(value.toString());
        out.writeObjectEnd();
    }
}
