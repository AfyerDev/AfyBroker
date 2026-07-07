package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

final class UrlDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return URL.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String raw = in.readString();
        try {
            URL value = new URL(raw);
            in.addRef(value);
            return value;
        } catch (MalformedURLException e) {
            throw new IOException("Invalid URL: " + raw, e);
        }
    }
}
