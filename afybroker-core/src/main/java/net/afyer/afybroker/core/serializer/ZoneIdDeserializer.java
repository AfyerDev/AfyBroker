package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.ZoneId;

class ZoneIdDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return ZoneId.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String id = in.readString();
        ZoneId value = ZoneId.of(id);
        in.addRef(value);
        return value;
    }
}
