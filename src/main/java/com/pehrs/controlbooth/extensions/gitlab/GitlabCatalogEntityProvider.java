package com.pehrs.controlbooth.extensions.gitlab;

import com.pehrs.controlbooth.catalog.spi.CatalogApi;
import com.pehrs.controlbooth.catalog.spi.CatalogProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Catalog provider that imports entities from a GitLab instance.
 *
 * <p>Activated only when {@code catalog.gitlab-provider.enabled=true} is set,
 * so it does not run (or fail to connect) in environments without GitLab.
 *
 * <p>Expected implementation: scan GitLab projects for {@code catalog-info.yaml}
 * files and upsert the described Components, Systems, APIs, etc. via the
 * {@link CatalogApi}.
 */
@Component
@ConditionalOnProperty(name = "catalog.gitlab-provider.enabled", havingValue = "true")
@Slf4j
public class GitlabCatalogEntityProvider implements CatalogProvider {

  @Override
  public String getId() {
    return "gitlab";
  }

  @Override
  public void refresh(CatalogApi api) throws Exception {
    log.info("GitLab catalog provider refresh — not yet implemented");
    // TODO: connect to GitLab, discover catalog-info.yaml files, upsert entities via api
  }
}
