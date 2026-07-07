package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.util.Currency;

final class CurrencyDeserializer extends AbstractDeserializer {
    @Override
    public Class<?> getType() {
        return Currency.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String code = in.readString();
        Currency value = Currency.getInstance(code);
        in.addRef(value);
        return value;
    }
}
