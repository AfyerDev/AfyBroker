package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.YearMonth;

final class YearMonthSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        YearMonth value = (YearMonth) obj;
        int ref = out.writeObjectBegin(YearMonth.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(2);
            out.writeString("year");
            out.writeString("month");
            out.writeObjectBegin(YearMonth.class.getName());
        }
        out.writeInt(value.getYear());
        out.writeInt(value.getMonthValue());
        out.writeObjectEnd();
    }
}
