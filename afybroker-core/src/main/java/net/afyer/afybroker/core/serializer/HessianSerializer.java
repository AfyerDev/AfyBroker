package net.afyer.afybroker.core.serializer;


import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.Serializer;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Function;

/**
 * Hessian 序列化器实现
 *
 * @author Nipuru
 * @since 2025/07/12 10:36
 */
public class HessianSerializer implements Serializer {
    private final ThreadLocal<ByteArrayOutputStream> localOutputByteArray = ThreadLocal.withInitial(ByteArrayOutputStream::new);
    private final SerializerFactory serializerFactory;
    private final Function<ByteArrayOutputStream, Hessian2Output> outputFactory;

    public HessianSerializer(ClassLoader classLoader) {
        this.serializerFactory = new SerializerFactory(classLoader);
        this.outputFactory = useTypeIntern()
                ? TypeInternHessian2Output::new
                : Hessian2Output::new;
    }

    public byte[] serialize(Object obj) throws CodecException {
        if (obj == null) {
            return new byte[0];
        }
        ByteArrayOutputStream byteArray = localOutputByteArray.get();
        byteArray.reset();
        Hessian2Output output = outputFactory.apply(byteArray);
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
        if (data == null || data.length == 0) {
            return null;
        }
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

    private static boolean useTypeIntern() {
        Class<?> clazz = HessianSerializer.class;
        String name1 = clazz.getName();
        String name2 = clazz.getName();
        return System.identityHashCode(name1) != System.identityHashCode(name2);
    }

    private static class TypeInternHessian2Output extends Hessian2Output {
        public TypeInternHessian2Output(ByteArrayOutputStream byteArray) {
            super(byteArray);
        }
        @Override
        public int writeObjectBegin(String type) throws IOException {
            // 修复某些自定义类 Class.getName() 返回对象不同的问题
            return super.writeObjectBegin(type.intern());
        }
    }

}
