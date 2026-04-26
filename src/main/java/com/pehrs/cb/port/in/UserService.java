package com.pehrs.cb.port.in;

import com.pehrs.cb.core.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findUser(String username);

    User get(final Long id);

    Optional<User> getByName(String name);

    void upsert(User user);

    Long create(final User userDTO);

    void update(final Long id, final User userDTO);

    void delete(final Long id);
}
