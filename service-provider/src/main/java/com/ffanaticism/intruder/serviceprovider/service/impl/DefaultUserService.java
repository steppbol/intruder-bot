package com.ffanaticism.intruder.serviceprovider.service.impl;

import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.serviceprovider.model.StoredUser;
import com.ffanaticism.intruder.serviceprovider.model.StoredUserCharacteristic;
import com.ffanaticism.intruder.serviceprovider.repository.UserRepository;
import com.ffanaticism.intruder.serviceprovider.service.UserService;
import com.ffanaticism.intruder.serviceprovider.util.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public DefaultUserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public void save(User user) {
        var storedUser = userMapper.toStoredUser(user);
        var characteristics = storedUser.getCharacteristics().stream()
                .filter(StoredUserCharacteristic::isUnique).toList();

        StoredUser updatedUser = userRepository.findByCharacteristics(characteristics.stream()
                .map(e -> String.valueOf(e.isUnique())
                        .concat("-")
                        .concat(e.getName())
                        .concat("-")
                        .concat(e.getValue()))
                .collect(Collectors.toList()));

        if (updatedUser != null) {
            userMapper.updatePatchEntity(storedUser, updatedUser);
        } else {
            updatedUser = storedUser;
        }

        userRepository.save(updatedUser);
    }

    @Override
    public List<StoredUser> getAll() {
        return userRepository.findAll();
    }
}
