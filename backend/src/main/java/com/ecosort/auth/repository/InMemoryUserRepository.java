package com.ecosort.auth.repository;

import com.ecosort.auth.model.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> byId = new ConcurrentHashMap<>();
    private final Map<String, User> byEmail = new ConcurrentHashMap<>();

    @Override
    public User findById(String id) {
        return byId.get(id);
    }

    @Override
    public User findByEmail(String email) {
        if (email == null) {
            return null;
        }
        return byEmail.get(email.toLowerCase());
    }

    @Override
    public User save(User user) {
        byId.put(user.id(), user);
        byEmail.put(user.email().toLowerCase(), user);
        return user;
    }
}
