package com.pehrs.controlbooth.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pehrs.controlbooth.model.GroupDTO;
import com.pehrs.controlbooth.model.UserDTO;
import com.pehrs.controlbooth.service.GroupService;
import com.pehrs.controlbooth.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogApiImplTest {

  @Mock UserService userService;
  @Mock GroupService groupService;
  @InjectMocks
  CatalogApiImpl api;

  // ── Users ─────────────────────────────────────────────────────────────────

  @Test
  void upsertUser_delegatesToUserService() {
    UserDTO user = UserDTO.builder().name("alice").displayName("Alice").build();

    api.upsertUser(user);

    verify(userService).upsert(user);
  }

  @Test
  void findUser_returnsValueFromUserService() {
    UserDTO user = UserDTO.builder().name("alice").displayName("Alice").build();
    when(userService.findUser("alice")).thenReturn(Optional.of(user));

    Optional<UserDTO> result = api.findUser("alice");

    assertThat(result).contains(user);
    verify(userService).findUser("alice");
  }

  @Test
  void findUser_returnsEmptyWhenNotFound() {
    when(userService.findUser("ghost")).thenReturn(Optional.empty());

    assertThat(api.findUser("ghost")).isEmpty();
  }

  @Test
  void updateUser_delegatesToUserService() {
    UserDTO user = UserDTO.builder().name("alice").displayName("Alice").build();

    api.updateUser(42L, user);

    verify(userService).update(42L, user);
  }

  // ── Groups ────────────────────────────────────────────────────────────────

  @Test
  void upsertGroup_delegatesToGroupService() {
    GroupDTO group = GroupDTO.builder().name("devs").displayName("Devs").build();

    api.upsertGroup(group);

    verify(groupService).upsert(group);
  }

  @Test
  void findGroup_returnsValueFromGroupService() {
    GroupDTO group = GroupDTO.builder().name("devs").displayName("Devs").build();
    when(groupService.getByName("devs")).thenReturn(Optional.of(group));

    Optional<GroupDTO> result = api.findGroup("devs");

    assertThat(result).contains(group);
    verify(groupService).getByName("devs");
  }

  @Test
  void findGroup_returnsEmptyWhenNotFound() {
    when(groupService.getByName("ghost")).thenReturn(Optional.empty());

    assertThat(api.findGroup("ghost")).isEmpty();
  }
}
