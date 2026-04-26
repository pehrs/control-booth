package com.pehrs.cb.port.out.repos;

import com.pehrs.cb.port.out.repos.domain.CatalogBaseEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository extends JpaRepository<CatalogBaseEntity, Long> {

  @Query("SELECT u FROM CatalogBaseEntity u JOIN FETCH u.annotations")
  List<CatalogBaseEntity> findAllWithAnnotations();

}
