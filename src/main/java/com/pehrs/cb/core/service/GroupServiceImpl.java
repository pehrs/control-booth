package com.pehrs.cb.core.service;


import com.pehrs.cb.port.in.GroupService;
import com.pehrs.cb.port.out.repos.domain.CatalogBaseEntity;
import com.pehrs.cb.port.out.repos.domain.GroupEntity;
import com.pehrs.cb.port.out.repos.domain.UserEntity;
import com.pehrs.cb.core.domain.events.BeforeDeleteGroup;
import com.pehrs.cb.core.domain.Group;
import com.pehrs.cb.port.out.repos.GroupRepository;
import com.pehrs.cb.port.out.repos.UserRepository;
import com.pehrs.cb.util.CustomCollectors;
import com.pehrs.cb.util.NotFoundException;
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
public class GroupServiceImpl implements GroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final ApplicationEventPublisher publisher;

  public GroupServiceImpl(final GroupRepository groupRepository,
                          final UserRepository userRepository,
                          final ApplicationEventPublisher publisher) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.publisher = publisher;
  }

  public List<Group> findAll() {
    final List<GroupEntity> groups = groupRepository.findAll(Sort.by("id"));
    return groups.stream()
        .map(group -> mapToDomain(group, Group.builder().build()))
        .toList();
  }

  public Optional<Group> getByName(String name) {
    return groupRepository.findByName(name)
        .map(group -> mapToDomain(group, Group.builder().build()));
  }

  public List<Group> getChildren(Long parentId) {
    return groupRepository.getReferenceById(parentId)
        .getChildren().stream()
        .map(group -> mapToDomain(group, Group.builder().build()))
        .toList();
  }

  public Group get(final Long id) {
    return groupRepository.findById(id)
        .map(group -> mapToDomain(group, Group.builder().build()))
        .orElseThrow(NotFoundException::new);
  }

  public Long create(final Group groupDTO) {
    final GroupEntity group = new GroupEntity();
    mapToEntity(groupDTO, group);
    return groupRepository.save(group).getId();
  }

  public void upsert(Group group) {
    getByName(group.getName())
        .ifPresentOrElse((groupDTO -> {
              update(groupDTO.getId(), group);
            }),
            () -> {
              create(group);
            });
  }


  public void update(final Long id, final Group groupDTO) {
    final GroupEntity group = groupRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    mapToEntity(groupDTO, group);
    groupRepository.save(group);
  }

  public void delete(final Long id) {
    final GroupEntity group = groupRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    publisher.publishEvent(new BeforeDeleteGroup(id));
    groupRepository.delete(group);
  }

  private Group mapToDomain(final GroupEntity entity, final Group group) {
    CatalogEntityServiceImpl.mapToDomain(entity, group);

    if(entity.getParent() != null) {
      group.setParentName(entity.getParent().getName());
    }
    group.setUsers(
        entity.getUsers().stream().map(CatalogBaseEntity::getName).collect(Collectors.toSet())
    );
    group.setChildren(
        entity.getChildren().stream().map(CatalogBaseEntity::getName).collect(Collectors.toSet())
    );
    return group;
  }

  private GroupEntity mapToEntity(final Group group, final GroupEntity entity) {
    CatalogEntityServiceImpl.mapToEntity(group, entity);

    entity.setEmail(group.getEmail());
    entity.setPicture(group.getPicture());
    if (group.getParentName() != null) {
      groupRepository.findByName(group.getParentName())
              .ifPresent((entity::setParent));
    }

    final Set<GroupEntity> children = groupRepository.findAllByNames(
        group.getChildren() == null ? Set.of() : group.getChildren()
    );
    if (children.size() != (group.getChildren() == null ? 0 : group.getChildren().size())) {
      throw new NotFoundException("one of groups not found");
    }
    entity.setChildren(children);

    final Set<UserEntity> users = userRepository.findAllByNames(
        group.getUsers() == null ? Set.of() : group.getUsers()
    );
    if (users.size() != (group.getUsers() == null ? 0 : group.getUsers().size())) {
      throw new NotFoundException("one of users not found");
    }
    entity.setUsers(users);
    return entity;
  }

  public Map<Long, String> getGroupValues() {
    return groupRepository.findAll(Sort.by("id"))
        .stream()
        .collect(CustomCollectors.toSortedMap(GroupEntity::getId, GroupEntity::getName));
  }

}