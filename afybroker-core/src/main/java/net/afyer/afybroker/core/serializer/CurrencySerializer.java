package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.util.Currency;

final class CurrencySerializer implements Serializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        Currency value = (Currency) obj;
        int ref = out.writeObjectBegin(Currency.class.getName());
        if (ref == -1) {
            out.writeClassFieldLength(1);
            out.writeString("code");
            out.writeObjectBegin(Currency.class.getName());
        }
        out.writeString(value.getCurrencyCode());
        out.writeObjectEnd();
    }
}
