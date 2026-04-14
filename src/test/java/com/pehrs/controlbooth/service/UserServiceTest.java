package com.pehrs.controlbooth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pehrs.controlbooth.domain.catalog.Group;
import com.pehrs.controlbooth.domain.catalog.User;
import com.pehrs.controlbooth.events.BeforeDeleteGroup;
import com.pehrs.controlbooth.model.UserDTO;
import com.pehrs.controlbooth.repos.GroupRepository;
import com.pehrs.controlbooth.repos.UserRepository;
import com.pehrs.controlbooth.util.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock UserRepository userRepository;
  @Mock GroupRepository groupRepository;
  @InjectMocks UserService userService;

  // ── upsert ────────────────────────────────────────────────────────────────

  @Test
  void upsert_createsUser_whenNameNotFound() {
    UserDTO dto = UserDTO.builder().name("alice").displayName("Alice").email("alice@example.com").build();
    when(userRepository.findByName("alice")).thenReturn(Optional.empty());
    when(groupRepository.findAllByNames(anySet())).thenReturn(Set.of());
    User saved = userWithId(1L, "alice");
    when(userRepository.save(any(User.class))).thenReturn(saved);

    userService.upsert(dto);

    verify(userRepository).save(any(User.class));
  }

  @Test
  void upsert_updatesUser_whenNameFound() {
    User existing = userWithId(42L, "alice");
    when(userRepository.findByName("alice")).thenReturn(Optional.of(existing));
    when(userRepository.findById(42L)).thenReturn(Optional.of(existing));
    when(groupRepository.findAllByNames(anySet())).thenReturn(Set.of());
    when(userRepository.save(any(User.class))).thenReturn(existing);

    UserDTO dto = UserDTO.builder().name("alice").displayName("Alice Updated").email("alice@example.com").build();
    userService.upsert(dto);

    // update path must go through findById, not create a new entity
    verify(userRepository).findById(42L);
    verify(userRepository).save(existing);
  }

  // ── get ───────────────────────────────────────────────────────────────────

  @Test
  void get_throwsNotFoundException_whenIdMissing() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.get(99L))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void get_returnsDto_whenFound() {
    User user = userWithId(1L, "alice");
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    UserDTO result = userService.get(1L);

    assertThat(result.getName()).isEqualTo("alice");
    assertThat(result.getId()).isEqualTo(1L);
  }

  // ── findUser ──────────────────────────────────────────────────────────────

  @Test
  void findUser_returnsDto_whenFound() {
    User user = userWithId(1L, "alice");
    when(userRepository.findUserAndAnnotationsByUserId("alice")).thenReturn(Optional.of(user));

    Optional<UserDTO> result = userService.findUser("alice");

    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("alice");
  }

  @Test
  void findUser_returnsEmpty_whenNotFound() {
    when(userRepository.findUserAndAnnotationsByUserId("ghost")).thenReturn(Optional.empty());

    assertThat(userService.findUser("ghost")).isEmpty();
  }

  // ── create ────────────────────────────────────────────────────────────────

  @Test
  void create_throwsNotFoundException_whenGroupDoesNotExist() {
    UserDTO dto = UserDTO.builder()
        .name("alice").displayName("Alice")
        .groups(Set.of("missing-group"))
        .build();
    // repository returns fewer groups than requested → at least one doesn't exist
    when(groupRepository.findAllByNames(anySet())).thenReturn(Set.of());

    assertThatThrownBy(() -> userService.create(dto))
        .isInstanceOf(NotFoundException.class);
  }

  // ── delete ────────────────────────────────────────────────────────────────

  @Test
  void delete_throwsNotFoundException_whenIdMissing() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.delete(99L))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void delete_removesUser_whenFound() {
    User user = userWithId(1L, "alice");
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    userService.delete(1L);

    verify(userRepository).delete(user);
  }

  // ── event: BeforeDeleteGroup ──────────────────────────────────────────────

  @Test
  void on_beforeDeleteGroup_removesGroupFromAffectedUsers() {
    Group group = new Group();
    group.setId(10L);
    group.setName("devs");

    User user = userWithId(1L, "alice");
    user.getGroups().add(group);

    when(userRepository.findAllByGroupsId(10L)).thenReturn(List.of(user));

    userService.on(new BeforeDeleteGroup(10L));

    assertThat(user.getGroups()).isEmpty();
  }

  // ── helpers ───────────────────────────────────────────────────────────────

  private static User userWithId(Long id, String name) {
    User user = new User();
    user.setId(id);
    user.setName(name);
    user.setDisplayName(name);
    return user;
  }
}
