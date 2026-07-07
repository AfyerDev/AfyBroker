package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.MonthDay;

final class MonthDaySerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        MonthDay value = (MonthDay) obj;
        int ref = out.writeObjectBegin(MonthDay.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(2);
            out.writeString("month");
            out.writeString("day");
            out.writeObjectBegin(MonthDay.class.getName());
        }
        out.writeInt(value.getMonthValue());
        out.writeInt(value.getDayOfMonth());
        out.writeObjectEnd();
    }
}
