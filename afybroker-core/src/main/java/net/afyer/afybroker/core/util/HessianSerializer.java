package net.afyer.afybroker.core.util;


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
public class HessianSerializer {

    private static final SerializerFactory serializerFactory = new SerializerFactory(HessianSerializer.class.getClassLoader());
    private static final ThreadLocal<ByteArrayOutputStream> localOutputByteArray = ThreadLocal.withInitial(ByteArrayOutputStream::new);

    private HessianSerializer() {
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArray = localOutputByteArray.get();
        byteArray.reset();
        Hessian2Output output = new Hessian2Output(byteArray);
        output.setSerializerFactory(serializerFactory);
        output.writeObject(obj);
        output.close();
        return byteArray.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] data) throws IOException {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(data));
        input.setSerializerFactory(serializerFactory);
        Object resultObject;
        resultObject = input.readObject();
        input.close();
        return (T) resultObject;
    }


}
