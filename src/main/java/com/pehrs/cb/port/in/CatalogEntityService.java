package com.pehrs.cb.port.in;

import com.pehrs.cb.core.domain.CatalogEntity;

import java.util.List;

public interface CatalogEntityService {
    List<CatalogEntity> findAllEntities();
}
