package com.ffanaticism.intruder.serviceprovider.service;

import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.serviceprovider.model.StoredUser;

import java.util.List;

public interface UserService {
    void save(User user);

    List<StoredUser> getAll();
}
