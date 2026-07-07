package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.Period;

final class PeriodDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return Period.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        int years = in.readInt();
        int months = in.readInt();
        int days = in.readInt();
        Period value = Period.of(years, months, days);
        in.addRef(value);
        return value;
    }
}
