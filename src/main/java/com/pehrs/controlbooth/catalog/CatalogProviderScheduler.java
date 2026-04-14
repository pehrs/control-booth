package com.pehrs.controlbooth.catalog;

import com.pehrs.controlbooth.catalog.spi.CatalogApi;
import com.pehrs.controlbooth.catalog.spi.CatalogProvider;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Drives all registered {@link CatalogProvider} beans on a shared schedule.
 *
 * <p>Every Spring bean that implements {@link CatalogProvider} is automatically
 * collected here via constructor injection. To register a new provider, declare
 * it as a {@code @Component} (or any {@code @Bean}) that implements the interface.
 *
 * <p>The refresh interval is controlled by:
 * <ul>
 *   <li>{@code catalog.refresh-rate} — how often to refresh (default {@code 5m})</li>
 *   <li>{@code catalog.initial-delay} — delay before the first run (default {@code 3s})</li>
 * </ul>
 *
 * <p>A failure in one provider is caught and logged; it does not prevent the
 * remaining providers from running.
 */
@Component
@Slf4j
class CatalogProviderScheduler {

  private final List<CatalogProvider> providers;
  private final CatalogApi catalogApi;

  CatalogProviderScheduler(List<CatalogProvider> providers, CatalogApi catalogApi) {
    this.providers = providers;
    this.catalogApi = catalogApi;
    log.info("Catalog provider scheduler initialized with {} provider(s): {}",
        providers.size(),
        providers.stream().map(CatalogProvider::getId).toList());
  }

  @Scheduled(
      fixedRateString = "${catalog.refresh-rate:5m}",
      initialDelayString = "${catalog.initial-delay:3s}"
  )
  public void refreshAll() {
    log.info("Starting catalog refresh ({} provider(s))", providers.size());
    for (CatalogProvider provider : providers) {
      log.info("Refreshing provider: {}", provider.getId());
      try {
        provider.refresh(catalogApi);
        log.info("Provider {} refresh complete", provider.getId());
      } catch (Exception e) {
        log.error("Provider {} failed: {}", provider.getId(), e.getMessage(), e);
      }
    }
    log.info("Catalog refresh finished");
  }
}
