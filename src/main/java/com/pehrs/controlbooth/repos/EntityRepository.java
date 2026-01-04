package com.pehrs.controlbooth.repos;

import com.pehrs.controlbooth.domain.catalog.CatalogEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository extends JpaRepository<CatalogEntity, Long> {


  @Query("SELECT u FROM CatalogEntity u JOIN FETCH u.annotations")
  List<CatalogEntity> findAllWithAnnotations();


}
