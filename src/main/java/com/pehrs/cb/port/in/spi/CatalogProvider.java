package com.pehrs.cb.port.in.spi;

import com.pehrs.cb.port.in.CatalogApi;

/**
 * SPI for catalog data providers (plugins).
 *
 * <p>Declare a Spring {@code @Component} that implements this interface and it will be
 * automatically discovered by the {@code CatalogProviderScheduler}, which calls
 * {@link #refresh} on the configured schedule.
 *
 * <pre>{@code
 * @Component
 * public class MyProvider implements CatalogProvider {
 *
 *     @Override
 *     public String getId() { return "my-provider"; }
 *
 *     @Override
 *     public void refresh(CatalogApi api) throws Exception {
 *         api.upsertUser(UserDTO.builder()
 *             .name("alice")
 *             .displayName("Alice")
 *             .build());
 *     }
 * }
 * }</pre>
 */
public interface CatalogProvider {

  /**
   * Stable, unique identifier for this provider (e.g. {@code "ldap"}, {@code "gitlab"}).
   * Used in log messages and metrics.
   */
  String getId();

  /**
   * Perform a full refresh of the entities owned by this provider.
   * Called periodically by the scheduler; any exception is caught and logged
   * so one failing provider does not block others.
   *
   * @param api the catalog API through which entities are written and read
   */
  void refresh(CatalogApi api) throws Exception;
}
