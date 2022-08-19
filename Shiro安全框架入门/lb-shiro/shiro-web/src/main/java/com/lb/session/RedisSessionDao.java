package com.lb.session;

import com.lb.util.JedisUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RedisSessionDao extends AbstractSessionDAO {

    @Resource
    private JedisUtil jedisUtil;

    private final String SHIRO_SESSION_PREFIX = "cheng-session:";

    private byte[] getKey(String key) {
        return (SHIRO_SESSION_PREFIX + key).getBytes();
    }

    /**
     * 保存session返回sessionId
     * @param session
     * @return
     */
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        //sessionId和session进行捆绑
        assignSessionId(session, sessionId);
        //保存session到redis
        saveSession(session);
        return sessionId;
    }

    /**
     * 通过sessionId获得session
     * @param sessionId
     * @return
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        System.out.println("read session");
        if (sessionId == null) {
            return null;
        }
        byte[] key = getKey(sessionId.toString());
        //通过key获取value
        byte[] value = jedisUtil.get(key);
        //value byte数组反序列化为Session
        return (Session) SerializationUtils.deserialize(value);
    }

    /**
     * 更新session
     * @param session
     * @throws UnknownSessionException
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session != null && session.getId() != null) {
            //保存session到redis
            saveSession(session);
        }
    }

    /**
     * 保存session到redis
     * @param session
     */
    private void saveSession(Session session) {
        //key
        byte[] key = getKey(session.getId().toString());
        //把session序列化为byte数组
        byte[] value = SerializationUtils.serialize(session);
        //把session放入redis中，并设置超时时间为600秒
        jedisUtil.set(key, value);
        jedisUtil.expire(key, 600);
    }

    /**
     * 删除session
     * @param session
     */
    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }

        byte[] key = getKey(session.getId().toString());
        jedisUtil.delete(key);
    }

    /**
     * 获取所有Session
     * @return
     */
    @Override
    public Collection<Session> getActiveSessions() {
        Set<byte[]> keys = jedisUtil.keys(SHIRO_SESSION_PREFIX);
        Set<Session> sessions = new HashSet<>();
        if (CollectionUtils.isEmpty(keys)) {
            return sessions;
        }
        for (byte[] key : keys) {
            Session session = (Session) SerializationUtils.deserialize(jedisUtil.get(key));
            sessions.add(session);
        }
        return sessions;
    }
}
