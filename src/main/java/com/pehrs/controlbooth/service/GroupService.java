package com.pehrs.controlbooth.service;


import com.pehrs.controlbooth.domain.catalog.CatalogEntity;
import com.pehrs.controlbooth.domain.catalog.Group;
import com.pehrs.controlbooth.domain.catalog.User;
import com.pehrs.controlbooth.events.BeforeDeleteGroup;
import com.pehrs.controlbooth.model.GroupDTO;
import com.pehrs.controlbooth.repos.GroupRepository;
import com.pehrs.controlbooth.repos.UserRepository;
import com.pehrs.controlbooth.util.CustomCollectors;
import com.pehrs.controlbooth.util.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class GroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final ApplicationEventPublisher publisher;

  public GroupService(final GroupRepository groupRepository,
      final UserRepository userRepository,
      final ApplicationEventPublisher publisher) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.publisher = publisher;
  }

  public List<GroupDTO> findAll() {
    final List<Group> groups = groupRepository.findAll(Sort.by("id"));
    return groups.stream()
        .map(group -> mapToDTO(group, GroupDTO.builder().build()))
        .toList();
  }

  public Optional<GroupDTO> getByName(String name) {
    return groupRepository.findByName(name)
        .map(group -> mapToDTO(group, GroupDTO.builder().build()));
  }

  public List<GroupDTO> getChildren(Long parentId) {
    return groupRepository.getReferenceById(parentId)
        .getChildren().stream()
        .map(group -> mapToDTO(group, GroupDTO.builder().build()))
        .toList();
  }

  public GroupDTO get(final Long id) {
    return groupRepository.findById(id)
        .map(group -> mapToDTO(group, GroupDTO.builder().build()))
        .orElseThrow(NotFoundException::new);
  }

  public Long create(final GroupDTO groupDTO) {
    final Group group = new Group();
    mapToEntity(groupDTO, group);
    return groupRepository.save(group).getId();
  }

  public void upsert(GroupDTO group) {
    getByName(group.getName())
        .ifPresentOrElse((groupDTO -> {
              update(groupDTO.getId(), group);
            }),
            () -> {
              create(group);
            });
  }


  public void update(final Long id, final GroupDTO groupDTO) {
//    final Group group = groupRepository.findById(id)
//        .orElseThrow(NotFoundException::new);
    Group group = new Group();
    mapToEntity(groupDTO, group);
    group.setId(id);
    groupRepository.save(group);
  }

  public void delete(final Long id) {
    final Group group = groupRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    publisher.publishEvent(new BeforeDeleteGroup(id));
    groupRepository.delete(group);
  }

  private GroupDTO mapToDTO(final Group group, final GroupDTO groupDTO) {
    CatalogEntityService.mapToDTO(group, groupDTO);

    if(group.getParent() != null) {
      groupDTO.setParentName(group.getParent().getName());
    }
    groupDTO.setUsers(
        group.getUsers().stream().map(CatalogEntity::getName).collect(Collectors.toSet())
    );
    groupDTO.setChildren(
        group.getChildren().stream().map(CatalogEntity::getName).collect(Collectors.toSet())
    );
    return groupDTO;
  }

  private Group mapToEntity(final GroupDTO groupDTO, final Group group) {
    CatalogEntityService.mapToEntity(groupDTO, group);

    group.setEmail(groupDTO.getEmail());
    group.setPicture(groupDTO.getPicture());
    if (groupDTO.getParentName() != null) {
      groupRepository.findByName(groupDTO.getParentName())
              .ifPresent((parent -> {
                group.setParent(parent);
              }));
    }

    final Set<Group> children = groupRepository.findAllByNames(
        groupDTO.getChildren() == null ? Set.of() : groupDTO.getChildren()
    );
    if (children.size() != (groupDTO.getChildren() == null ? 0 : groupDTO.getChildren().size())) {
      throw new NotFoundException("one of groups not found");
    }
    group.setChildren(children);

    final Set<User> users = userRepository.findAllByNames(
        groupDTO.getUsers() == null ? Set.of() : groupDTO.getUsers()
    );
    if (users.size() != (groupDTO.getUsers() == null ? 0 : groupDTO.getUsers().size())) {
      throw new NotFoundException("one of users not found");
    }
    group.setUsers(users);
    return group;
  }

  public Map<Long, String> getGroupValues() {
    return groupRepository.findAll(Sort.by("id"))
        .stream()
        .collect(CustomCollectors.toSortedMap(Group::getId, Group::getName));
  }

}