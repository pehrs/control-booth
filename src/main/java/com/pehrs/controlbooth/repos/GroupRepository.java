package com.pehrs.controlbooth.repos;

import com.pehrs.controlbooth.domain.catalog.Group;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

  List<Group> findByParent(Long id);

  Optional<Group> findByName(String name);

  @Query("SELECT u FROM Group u WHERE u.name in :names")
  Set<Group> findAllByNames(@Param("names") Set<String> names);

}
