package sepc.sample.utils;

import redis.clients.jedis.Jedis;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RedisClient {

    private final Jedis jedis;

    public RedisClient(String host, int port) {
        this.jedis = new Jedis(host, port);
    }

    public void setObject(String key, Object obj) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        byte[] bytes = bos.toByteArray();
        jedis.set(key.getBytes(), bytes);
        oos.close();
        bos.close();
    }

    public Object getObject(String key) throws Exception {
        byte[] bytes = jedis.get(key.getBytes());
        if (bytes == null) {
            return null;
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();
        ois.close();
        bis.close();
        return obj;
    }

    public void close() {
        jedis.close();
    }
}
