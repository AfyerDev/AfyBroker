package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.net.URI;

final class UriDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return URI.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String raw = in.readString();
        URI value = URI.create(raw);
        in.addRef(value);
        return value;
    }
}
