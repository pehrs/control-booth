package com.pehrs.controlbooth.service;

import com.pehrs.controlbooth.domain.catalog.CatalogEntity;
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
import java.util.stream.Collectors;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class UserService {

  private final UserRepository userRepository;
  private final GroupRepository groupRepository;

  public UserService(UserRepository userRepository,
      GroupRepository groupRepository) {
    this.userRepository = userRepository;
    this.groupRepository = groupRepository;
  }

  public List<UserDTO> findAll() {
    final List<User> users = userRepository.findAll(Sort.by("id"));
    return users.stream()
        .map(user -> mapToDTO(user, UserDTO.builder().build()))
        .toList();
  }

  public Optional<UserDTO> findUser(String username) {
    return userRepository.findUserAndAnnotationsByUserId(username)
        .map(user -> mapToDTO(user, UserDTO.builder().build()));
  }

  public UserDTO get(final Long id) {
    return userRepository.findById(id)
        .map(user -> mapToDTO(user, UserDTO.builder().build()))
        .orElseThrow(NotFoundException::new);
  }

  public Optional<UserDTO> getByName(String name) {
    return userRepository.findByName(name)
        .map(user -> mapToDTO(user, UserDTO.builder().build()));
  }

  public void upsert(UserDTO user) {
    getByName(user.getName())
        .ifPresentOrElse((userDTO -> {
              update(userDTO.getId(), user);
            }),
            () -> {
              create(user);
            });
  }

  public Long create(final UserDTO userDTO) {
    final User user = new User();
    mapToEntity(userDTO, user);
    return userRepository.save(user).getId();
  }

  public void update(final Long id, final UserDTO userDTO) {
    final User user = userRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    mapToEntity(userDTO, user);
    userRepository.save(user);
  }

  public void delete(final Long id) {
    final User user = userRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    userRepository.delete(user);
  }


  private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
    // Catalog Entity
    CatalogEntityService.mapToDTO(user, userDTO);

    // User Entity
    userDTO.setEmail(user.getEmail());
    userDTO.setPicture(user.getPicture());
//    userDTO.setPasswordHash(user.getPasswordHash());
    userDTO.setGroups(user.getGroups().stream()
        .map(CatalogEntity::getName)
        .collect(Collectors.toSet()));
    return userDTO;
  }

  private User mapToEntity(final UserDTO userDTO, final User user) {
    // Catalog Entity
    CatalogEntityService.mapToEntity(userDTO, user);

    user.setEmail(userDTO.getEmail());
    user.setPicture(userDTO.getPicture());
    final Set<Group> groups = groupRepository.findAllByNames(
        userDTO.getGroups() == null ? Set.of() : userDTO.getGroups());
    if (groups.size() != (userDTO.getGroups() == null ? 0 : userDTO.getGroups().size())) {
      throw new NotFoundException("one of groups not found");
    }
    user.setGroups(groups);
    return user;
  }

  @EventListener(BeforeDeleteGroup.class)
  public void on(final BeforeDeleteGroup event) {
    // remove many-to-many relations at owning side
    userRepository.findAllByGroupsId(event.getId()).forEach(
        user -> user.getGroups().removeIf(group -> group.getId().equals(event.getId()))
    );
  }

}
