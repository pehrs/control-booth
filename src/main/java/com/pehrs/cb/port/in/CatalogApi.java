package com.pehrs.cb.port.in;

import com.pehrs.cb.core.domain.Group;
import com.pehrs.cb.core.domain.User;
import com.pehrs.cb.port.in.spi.CatalogProvider;

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
   * Insert or update a user by {@link User#getName()}.
   * If a user with that name already exists it is updated; otherwise created.
   */
  void upsertUser(User user);

  /** Look up a user by their username (uid). */
  Optional<User> findUser(String username);

  /** Update a user identified by its DB id. */
  void updateUser(Long id, User user);

  // ── Groups ───────────────────────────────────────────────────────────────

  /**
   * Insert or update a group by {@link Group#getName()}.
   * If a group with that name already exists it is updated; otherwise created.
   */
  void upsertGroup(Group group);

  /** Look up a group by name. */
  Optional<Group> findGroup(String name);
}
