package serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.FileOutputStream;

public class KryoSerializer implements Serializer {
    /**
     * kryo非线程安全，需要利用threadLocalMap来保证线程安全
     */
    private final ThreadLocal<Kryo> threadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(Request.class);
        kryo.register(Response.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object object) {
        try (
                Output output = new Output(new ByteBufferOutputStream());
        ) {
            Kryo kryo = threadLocal.get();
            kryo.writeObject(output, object);
            threadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new RuntimeException("serialize error");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (
                Input input = new Input(bytes);
        ) {
            Kryo kryo = threadLocal.get();
            Object o = kryo.readObject(input, clazz);
            threadLocal.remove();
            //强转  Object -> T
            return clazz.cast(o);
        } catch (Exception e) {
            throw new RuntimeException("deserialize error");
        }
    }
}
