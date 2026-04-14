package com.pehrs.controlbooth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pehrs.controlbooth.domain.catalog.Group;
import com.pehrs.controlbooth.events.BeforeDeleteGroup;
import com.pehrs.controlbooth.model.GroupDTO;
import com.pehrs.controlbooth.repos.GroupRepository;
import com.pehrs.controlbooth.repos.UserRepository;
import com.pehrs.controlbooth.util.NotFoundException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

  @Mock GroupRepository groupRepository;
  @Mock UserRepository userRepository;
  @Mock ApplicationEventPublisher publisher;
  @InjectMocks GroupService groupService;

  // ── upsert ────────────────────────────────────────────────────────────────

  @Test
  void upsert_createsGroup_whenNameNotFound() {
    GroupDTO dto = GroupDTO.builder().name("devs").displayName("Devs").build();
    when(groupRepository.findByName("devs")).thenReturn(Optional.empty());
    when(groupRepository.findAllByNames(anySet())).thenReturn(Set.of());
    when(userRepository.findAllByNames(anySet())).thenReturn(Set.of());
    Group saved = groupWithId(1L, "devs");
    when(groupRepository.save(any(Group.class))).thenReturn(saved);

    groupService.upsert(dto);

    verify(groupRepository).save(any(Group.class));
  }

  @Test
  void upsert_updatesGroup_whenNameFound() {
    Group existing = groupWithId(5L, "devs");
    when(groupRepository.findByName("devs")).thenReturn(Optional.of(existing));
    // update() now loads from DB (after the fix)
    when(groupRepository.findById(5L)).thenReturn(Optional.of(existing));
    when(groupRepository.findAllByNames(anySet())).thenReturn(Set.of());
    when(userRepository.findAllByNames(anySet())).thenReturn(Set.of());
    when(groupRepository.save(any(Group.class))).thenReturn(existing);

    GroupDTO dto = GroupDTO.builder().name("devs").displayName("Devs Updated").build();
    groupService.upsert(dto);

    // must load the existing entity, not create a new one with a pre-set id
    verify(groupRepository).findById(5L);
    verify(groupRepository).save(existing);
  }

  // ── get ───────────────────────────────────────────────────────────────────

  @Test
  void get_throwsNotFoundException_whenIdMissing() {
    when(groupRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> groupService.get(99L))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void get_returnsDto_whenFound() {
    Group group = groupWithId(1L, "devs");
    when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

    GroupDTO result = groupService.get(1L);

    assertThat(result.getName()).isEqualTo("devs");
    assertThat(result.getId()).isEqualTo(1L);
  }

  // ── getByName ─────────────────────────────────────────────────────────────

  @Test
  void getByName_returnsEmpty_whenNotFound() {
    when(groupRepository.findByName("ghost")).thenReturn(Optional.empty());

    assertThat(groupService.getByName("ghost")).isEmpty();
  }

  @Test
  void getByName_returnsDto_whenFound() {
    Group group = groupWithId(1L, "devs");
    when(groupRepository.findByName("devs")).thenReturn(Optional.of(group));

    Optional<GroupDTO> result = groupService.getByName("devs");

    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("devs");
  }

  // ── delete ────────────────────────────────────────────────────────────────

  @Test
  void delete_throwsNotFoundException_whenIdMissing() {
    when(groupRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> groupService.delete(99L))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void delete_publishesBeforeDeleteGroupEvent_thenRemovesGroup() {
    Group group = groupWithId(7L, "devs");
    when(groupRepository.findById(7L)).thenReturn(Optional.of(group));

    groupService.delete(7L);

    ArgumentCaptor<BeforeDeleteGroup> eventCaptor = ArgumentCaptor.forClass(BeforeDeleteGroup.class);
    verify(publisher).publishEvent(eventCaptor.capture());
    assertThat(eventCaptor.getValue().getId()).isEqualTo(7L);

    verify(groupRepository).delete(group);
  }

  // ── create validation ─────────────────────────────────────────────────────

  @Test
  void create_throwsNotFoundException_whenChildGroupDoesNotExist() {
    GroupDTO dto = GroupDTO.builder()
        .name("devs").displayName("Devs")
        .children(Set.of("missing-child"))
        .build();
    when(groupRepository.findAllByNames(anySet())).thenReturn(Set.of());

    assertThatThrownBy(() -> groupService.create(dto))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void create_throwsNotFoundException_whenMemberUserDoesNotExist() {
    GroupDTO dto = GroupDTO.builder()
        .name("devs").displayName("Devs")
        .users(Set.of("missing-user"))
        .build();
    when(groupRepository.findAllByNames(anySet())).thenReturn(Set.of());
    when(userRepository.findAllByNames(anySet())).thenReturn(Set.of());

    assertThatThrownBy(() -> groupService.create(dto))
        .isInstanceOf(NotFoundException.class);
  }

  // ── helpers ───────────────────────────────────────────────────────────────

  private static Group groupWithId(Long id, String name) {
    Group group = new Group();
    group.setId(id);
    group.setName(name);
    group.setDisplayName(name);
    return group;
  }
}
