package com.pehrs.controlbooth.service;

import com.pehrs.controlbooth.domain.catalog.CatalogEntity;
import com.pehrs.controlbooth.model.CatalogEntityDTO;
import com.pehrs.controlbooth.repos.EntityRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class CatalogEntityService {

  private final EntityRepository entityRepository;


  static CatalogEntityDTO mapToDTO(final CatalogEntity entity, final CatalogEntityDTO dto) {
    // Catalog Entity
    dto.setEntityType(entity.getClass().getSimpleName());
    dto.setId(entity.getId());
    dto.setDateCreated(entity.getDateCreated());
    dto.setLastUpdated(entity.getLastUpdated());
    dto.setName(entity.getName());
    dto.setDisplayName(entity.getDisplayName());
    dto.setDescription(entity.getDescription());
    dto.setEntityVariant(entity.getEntityVariant());
    dto.setAnnotations(entity.getAnnotations());
    return dto;
  }

  static CatalogEntity mapToEntity(CatalogEntityDTO dto, CatalogEntity entity) {
    entity.setName(dto.getName());
    entity.setDisplayName(dto.getDisplayName());
    entity.setDescription(dto.getDescription());
    entity.setEntityVariant(dto.getEntityType());
    entity.setAnnotations(dto.getAnnotations());
    return entity;
  }


  public List<CatalogEntityDTO> findAllEntities() {
    final List<CatalogEntity> entities = entityRepository.findAllWithAnnotations();
    return entities.stream()
        .map(entity -> mapToDTO(entity, CatalogEntityDTO.builder().build()))
        .toList();
  }
}
