package net.afyer.afybroker.core.util;


import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.Serializer;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Nipuru
 * @since 2025/07/12 10:36
 */
public class HessianSerializer implements Serializer {

    private final ThreadLocal<ByteArrayOutputStream> localOutputByteArray = ThreadLocal.withInitial(ByteArrayOutputStream::new);
    private final SerializerFactory serializerFactory;

    public HessianSerializer(ClassLoader classLoader) {
        serializerFactory = new SerializerFactory(classLoader);
    }

    public byte[] serialize(Object obj) throws CodecException {
        ByteArrayOutputStream byteArray = localOutputByteArray.get();
        byteArray.reset();
        Hessian2Output output = new Hessian2Output(byteArray);
        output.setSerializerFactory(serializerFactory);
        try {
            output.writeObject(obj);
            output.close();
        } catch (IOException e) {
            throw new CodecException("IOException occurred when Hessian serializer encode!", e);
        }
        return byteArray.toByteArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, String classOfT) throws CodecException {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(data));
        input.setSerializerFactory(serializerFactory);
        Object resultObject;
        try {
            resultObject = input.readObject();
            input.close();
        } catch (IOException e) {
            throw new CodecException("IOException occurred when Hessian serializer decode!", e);
        }
        return (T) resultObject;
    }

}
