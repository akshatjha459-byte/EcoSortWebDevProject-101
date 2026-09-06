package com.ecosort.auth.repository;

import com.ecosort.auth.model.User;

public interface UserRepository {
    User findById(String id);
    User findByEmail(String email);
    User save(User user);
}
