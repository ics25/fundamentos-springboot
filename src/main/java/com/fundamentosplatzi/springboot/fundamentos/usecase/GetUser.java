package com.fundamentosplatzi.springboot.fundamentos.usecase;

import com.fundamentosplatzi.springboot.fundamentos.entity.User;

import java.util.List;

public interface GetUser {
    List<User> getAll();
}
