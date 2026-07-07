package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.Period;

final class PeriodSerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        Period value = (Period) obj;
        int ref = out.writeObjectBegin(Period.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(3);
            out.writeString("years");
            out.writeString("months");
            out.writeString("days");
            out.writeObjectBegin(Period.class.getName());
        }
        out.writeInt(value.getYears());
        out.writeInt(value.getMonths());
        out.writeInt(value.getDays());
        out.writeObjectEnd();
    }
}
