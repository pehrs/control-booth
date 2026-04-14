package com.pehrs.controlbooth.catalog;

import com.pehrs.controlbooth.catalog.spi.CatalogApi;
import com.pehrs.controlbooth.model.GroupDTO;
import com.pehrs.controlbooth.model.UserDTO;
import com.pehrs.controlbooth.service.GroupService;
import com.pehrs.controlbooth.service.UserService;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
class CatalogApiImpl implements CatalogApi {

  private final UserService userService;
  private final GroupService groupService;

  @Override
  public void upsertUser(UserDTO user) {
    userService.upsert(user);
  }

  @Override
  public Optional<UserDTO> findUser(String username) {
    return userService.findUser(username);
  }

  @Override
  public void updateUser(Long id, UserDTO user) {
    userService.update(id, user);
  }

  @Override
  public void upsertGroup(GroupDTO group) {
    groupService.upsert(group);
  }

  @Override
  public Optional<GroupDTO> findGroup(String name) {
    return groupService.getByName(name);
  }
}
