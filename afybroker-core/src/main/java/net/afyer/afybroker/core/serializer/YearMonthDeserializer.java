package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.YearMonth;

final class YearMonthDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return YearMonth.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        int year = in.readInt();
        int month = in.readInt();
        YearMonth value = YearMonth.of(year, month);
        in.addRef(value);
        return value;
    }
}
