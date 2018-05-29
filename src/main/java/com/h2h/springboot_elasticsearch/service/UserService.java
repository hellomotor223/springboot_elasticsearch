package com.h2h.springboot_elasticsearch.service;

import com.h2h.springboot_elasticsearch.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public List<Map<String,Object>> getUsers() {
        return userDao.getUsers();
    }
}
