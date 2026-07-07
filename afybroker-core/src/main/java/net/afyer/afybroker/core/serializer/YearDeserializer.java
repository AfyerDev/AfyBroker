package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.Year;

final class YearDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return Year.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        int year = in.readInt();
        Year value = Year.of(year);
        in.addRef(value);
        return value;
    }
}
