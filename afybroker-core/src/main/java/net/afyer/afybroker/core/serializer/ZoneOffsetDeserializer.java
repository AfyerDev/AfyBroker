package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.ZoneOffset;

final class ZoneOffsetDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return ZoneOffset.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        int totalSeconds = in.readInt();
        ZoneOffset value = ZoneOffset.ofTotalSeconds(totalSeconds);
        in.addRef(value);
        return value;
    }
}
