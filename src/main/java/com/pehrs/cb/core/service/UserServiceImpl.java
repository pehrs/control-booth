package com.pehrs.cb.core.service;

import com.pehrs.cb.port.in.UserService;
import com.pehrs.cb.port.out.repos.domain.CatalogBaseEntity;
import com.pehrs.cb.port.out.repos.domain.GroupEntity;
import com.pehrs.cb.port.out.repos.domain.UserEntity;
import com.pehrs.cb.core.domain.events.BeforeDeleteGroup;
import com.pehrs.cb.core.domain.User;
import com.pehrs.cb.port.out.repos.GroupRepository;
import com.pehrs.cb.port.out.repos.UserRepository;
import com.pehrs.cb.util.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final GroupRepository groupRepository;

  public UserServiceImpl(UserRepository userRepository,
                         GroupRepository groupRepository) {
    this.userRepository = userRepository;
    this.groupRepository = groupRepository;
  }

  public List<User> findAll() {
    final List<UserEntity> users = userRepository.findAll(Sort.by("id"));
    return users.stream()
        .map(user -> mapToDomain(user, User.builder().build()))
        .toList();
  }

  public Optional<User> findUser(String username) {
    return userRepository.findUserAndAnnotationsByUserId(username)
        .map(user -> mapToDomain(user, User.builder().build()));
  }

  public User get(final Long id) {
    return userRepository.findById(id)
        .map(user -> mapToDomain(user, User.builder().build()))
        .orElseThrow(NotFoundException::new);
  }

  public Optional<User> getByName(String name) {
    return userRepository.findByName(name)
        .map(user -> mapToDomain(user, User.builder().build()));
  }

  public void upsert(User user) {
    getByName(user.getName())
        .ifPresentOrElse((userDTO -> {
              update(userDTO.getId(), user);
            }),
            () -> {
              create(user);
            });
  }

  public Long create(final User userDTO) {
    final UserEntity user = new UserEntity();
    mapToEntity(userDTO, user);
    return userRepository.save(user).getId();
  }

  public void update(final Long id, final User userDTO) {
    final UserEntity user = userRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    mapToEntity(userDTO, user);
    userRepository.save(user);
  }

  public void delete(final Long id) {
    final UserEntity user = userRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    userRepository.delete(user);
  }


  private User mapToDomain(final UserEntity entity, final User user) {
    // Catalog Entity
    CatalogEntityServiceImpl.mapToDomain(entity, user);

    // User Entity
    user.setEmail(entity.getEmail());
    user.setPicture(entity.getPicture());
//    userDTO.setPasswordHash(user.getPasswordHash());
    user.setGroups(entity.getGroups().stream()
        .map(CatalogBaseEntity::getName)
        .collect(Collectors.toSet()));
    return user;
  }

  private UserEntity mapToEntity(final User user, final UserEntity entity) {
    // Catalog Entity
    CatalogEntityServiceImpl.mapToEntity(user, entity);

    entity.setEmail(user.getEmail());
    entity.setPicture(user.getPicture());
    final Set<GroupEntity> groups = groupRepository.findAllByNames(
        user.getGroups() == null ? Set.of() : user.getGroups());
    if (groups.size() != (user.getGroups() == null ? 0 : user.getGroups().size())) {
      throw new NotFoundException("one of groups not found");
    }
    entity.setGroups(groups);
    return entity;
  }

  @EventListener(BeforeDeleteGroup.class)
  public void on(final BeforeDeleteGroup event) {
    // remove many-to-many relations at owning side
    userRepository.findAllByGroupsId(event.getId()).forEach(
        user -> user.getGroups().removeIf(group -> group.getId().equals(event.getId()))
    );
  }

}
