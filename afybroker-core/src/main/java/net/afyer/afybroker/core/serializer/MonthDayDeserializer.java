package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.MonthDay;

final class MonthDayDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return MonthDay.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        int month = in.readInt();
        int day = in.readInt();
        MonthDay value = MonthDay.of(month, day);
        in.addRef(value);
        return value;
    }
}
