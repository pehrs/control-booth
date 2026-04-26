package com.pehrs.cb.core.service;

import com.pehrs.cb.port.in.CatalogApi;
import com.pehrs.cb.core.domain.Group;
import com.pehrs.cb.core.domain.User;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
class CatalogApiImpl implements CatalogApi {

  private final UserServiceImpl userService;
  private final GroupServiceImpl groupService;

  @Override
  public void upsertUser(User user) {
    userService.upsert(user);
  }

  @Override
  public Optional<User> findUser(String username) {
    return userService.findUser(username);
  }

  @Override
  public void updateUser(Long id, User user) {
    userService.update(id, user);
  }

  @Override
  public void upsertGroup(Group group) {
    groupService.upsert(group);
  }

  @Override
  public Optional<Group> findGroup(String name) {
    return groupService.getByName(name);
  }
}
