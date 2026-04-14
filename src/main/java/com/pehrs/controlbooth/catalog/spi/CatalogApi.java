package com.pehrs.controlbooth.catalog.spi;

import com.pehrs.controlbooth.model.GroupDTO;
import com.pehrs.controlbooth.model.UserDTO;
import java.util.Optional;

/**
 * The API surface available to {@link CatalogProvider} implementations.
 *
 * <p>Providers must not inject services directly; all catalog reads and writes
 * go through this interface so the framework can intercept, log, and validate
 * operations uniformly.
 */
public interface CatalogApi {

  // ── Users ────────────────────────────────────────────────────────────────

  /**
   * Insert or update a user by {@link UserDTO#getName()}.
   * If a user with that name already exists it is updated; otherwise created.
   */
  void upsertUser(UserDTO user);

  /** Look up a user by their username (uid). */
  Optional<UserDTO> findUser(String username);

  /** Update a user identified by its DB id. */
  void updateUser(Long id, UserDTO user);

  // ── Groups ───────────────────────────────────────────────────────────────

  /**
   * Insert or update a group by {@link GroupDTO#getName()}.
   * If a group with that name already exists it is updated; otherwise created.
   */
  void upsertGroup(GroupDTO group);

  /** Look up a group by name. */
  Optional<GroupDTO> findGroup(String name);
}
