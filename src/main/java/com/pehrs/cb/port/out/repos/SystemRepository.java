package com.pehrs.cb.port.out.repos;

import com.pehrs.cb.port.out.repos.domain.GroupEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemRepository extends JpaRepository<GroupEntity, Long> {

  List<GroupEntity> findByParent(Long id);

  Optional<GroupEntity> findByName(String name);

  @Query("SELECT u FROM GroupEntity u WHERE u.name in :names")
  Set<GroupEntity> findAllByNames(@Param("names") Set<String> names);

}
