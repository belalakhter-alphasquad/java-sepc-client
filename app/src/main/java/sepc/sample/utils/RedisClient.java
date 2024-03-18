package sepc.sample.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.betbrain.sepc.connector.sportsmodel.Entity;

import java.io.*;

import java.util.List;
import java.util.Collections;

public class RedisClient {

    private final JedisPool jedisPool;

    public RedisClient(String host, int port) {

        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxTotal(1000);

        poolConfig.setMaxIdle(250);

        poolConfig.setMinIdle(100);

        this.jedisPool = new JedisPool(poolConfig, host, port, 6000);
    }

    public void addListToRedis(String key, List<Entity> items) {
        try (Jedis jedis = jedisPool.getResource()) {
            try {
                jedis.set(key.getBytes(), serialize(items));
            } catch (IOException e) {

            }
        }
    }

    public void rpush(String listKey, String... values) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.rpush(listKey, values);
        }
    }

    public String lpop(String listKey) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpop(listKey);
        }
    }

    public List<Entity> getListFromRedis(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] data = jedis.get(key.getBytes());
            List<Entity> entities = Collections.emptyList();
            ;
            try {
                entities = deserialize(data);
                return entities;
            } catch (ClassNotFoundException e) {

            } catch (IOException e) {

            }
            return entities;
        }
    }

    private byte[] serialize(List<Entity> objs) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(objs);
            return bos.toByteArray();
        }
    }

    private List<Entity> deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bis)) {
            @SuppressWarnings("unchecked")
            List<Entity> list = (List<Entity>) ois.readObject();
            return list;
        }
    }

    public void deleteFromRedis(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key.getBytes());
        }
    }

    public void close() {
        jedisPool.close();
    }
}
