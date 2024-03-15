package sepc.sample.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import com.betbrain.sepc.connector.sportsmodel.Entity;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RedisClient {

    private final JedisPool jedisPool;

    public RedisClient(String host, int port) {

        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxTotal(1000);

        poolConfig.setMaxIdle(250);

        poolConfig.setMinIdle(100);

        this.jedisPool = new JedisPool(poolConfig, host, port, 6000);
    }

    public void setObject(String key, Object obj) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            byte[] bytes = bos.toByteArray();
            jedis.set(key.getBytes(), bytes);

        }
    }

    public Object getObject(String key) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes == null) {
                return null;
            }
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();

            return obj;
        }
    }

    public Entity blpopEntity(String listKey, int timeoutSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> response = jedis.blpop(timeoutSeconds, listKey);
            if (response == null || response.isEmpty()) {
                return null;
            } else {
                String key = response.get(1);
                return (Entity) getObject(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void bulkInsertEntities(List<Entity> entities) {
        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline pipeline = jedis.pipelined();

            for (Entity entity : entities) {

                String key = "entity:" + entity.getId();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(entity);
                byte[] bytes = bos.toByteArray();

                pipeline.set(key.getBytes(), bytes);

                String listName = entity.getDisplayName().toLowerCase();
                pipeline.rpush(listName, key);
            }

            pipeline.sync();
        } catch (IOException e) {

        }
    }

    public List<Entity> batchPopEntities(String listKey, int count) {
        List<Entity> entities = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            List<Response<byte[]>> responses = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                responses.add(pipeline.lpop(listKey.getBytes()));
            }

            pipeline.sync();

            for (Response<byte[]> response : responses) {
                byte[] bytes = response.get();
                if (bytes != null) {
                    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    entities.add((Entity) ois.readObject());
                }
            }
        } catch (Exception e) {

        }
        return entities;
    }

    public Set<String> keys(String pattern) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(pattern);
        } catch (Exception e) {

            return Collections.emptySet();
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

    public void close() {
        jedisPool.close();
    }
}
