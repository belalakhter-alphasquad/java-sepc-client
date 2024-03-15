package sepc.sample.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import com.betbrain.sepc.connector.sportsmodel.Entity;
import java.io.*;
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
    public List<String> lrange(String listKey, long start, long stop) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(listKey, start, stop);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    public void ltrim(String listKey, long start, long stop) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.ltrim(listKey, start, stop);
        } catch (Exception e) {
          
        }
    }
    public List<String> popBulk(String listKey, int bulkSize) {
        String luaScript = "local keys = redis.call('LRANGE', KEYS[1], 0, ARGV[1]) " +
                            "redis.call('LTRIM', KEYS[1], ARGV[1]+1, -1) " +
                            "return keys";
        try (Jedis jedis = jedisPool.getResource()) {
            @SuppressWarnings("unchecked")
            List<String> keys = (List<String>) jedis.eval(luaScript, Collections.singletonList(listKey), Collections.singletonList(String.valueOf(bulkSize - 1)));
            return keys;
        } catch (Exception e) {
            return Collections.emptyList();
        }
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
