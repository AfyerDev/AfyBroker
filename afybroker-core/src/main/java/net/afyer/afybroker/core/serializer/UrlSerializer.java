package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.net.URL;

final class UrlSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        URL value = (URL) obj;
        int ref = out.writeObjectBegin(URL.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(1);
            out.writeString("value");
            out.writeObjectBegin(URL.class.getName());
        }
        out.writeString(value.toExternalForm());
        out.writeObjectEnd();
    }
}
