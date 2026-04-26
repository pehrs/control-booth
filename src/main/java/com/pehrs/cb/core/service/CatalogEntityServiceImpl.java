package com.pehrs.cb.core.service;

import com.pehrs.cb.port.in.CatalogEntityService;
import com.pehrs.cb.core.domain.CatalogEntity;
import com.pehrs.cb.port.out.repos.EntityRepository;
import java.util.List;

import com.pehrs.cb.port.out.repos.domain.CatalogBaseEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class CatalogEntityServiceImpl implements CatalogEntityService {

  private final EntityRepository entityRepository;


  static CatalogEntity mapToDomain(final CatalogBaseEntity portEntity, final CatalogEntity catalogEntity) {
    // Catalog Entity
    catalogEntity.setEntityType(portEntity.getClass().getSimpleName());
    catalogEntity.setId(portEntity.getId());
    catalogEntity.setDateCreated(portEntity.getDateCreated());
    catalogEntity.setLastUpdated(portEntity.getLastUpdated());
    catalogEntity.setName(portEntity.getName());
    catalogEntity.setDisplayName(portEntity.getDisplayName());
    catalogEntity.setDescription(portEntity.getDescription());
    catalogEntity.setEntityVariant(portEntity.getEntityVariant());
    catalogEntity.setAnnotations(portEntity.getAnnotations());
    return catalogEntity;
  }

  static CatalogBaseEntity mapToEntity(CatalogEntity catalogEntity,
                                       CatalogBaseEntity portEntity) {
    portEntity.setName(catalogEntity.getName());
    portEntity.setDisplayName(catalogEntity.getDisplayName());
    portEntity.setDescription(catalogEntity.getDescription());
    portEntity.setEntityVariant(catalogEntity.getEntityType());
    portEntity.setAnnotations(catalogEntity.getAnnotations());
    return portEntity;
  }


  public List<CatalogEntity> findAllEntities() {
    final List<CatalogBaseEntity> entities = entityRepository.findAllWithAnnotations();
    return entities.stream()
        .map(entity -> mapToDomain(entity, CatalogEntity.builder().build()))
        .toList();
  }
}
