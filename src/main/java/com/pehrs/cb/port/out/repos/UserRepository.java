package com.pehrs.cb.port.out.repos;

import com.pehrs.cb.port.out.repos.domain.UserEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  List<UserEntity> findAllByGroupsId(Long id);

  Optional<UserEntity> findByName(String name);

  Optional<UserEntity> findByEmail(String email);


  @Query("SELECT u FROM UserEntity u JOIN FETCH u.annotations WHERE u.name = :username")
  Optional<UserEntity> findUserAndAnnotationsByUserId(@Param("username") String username);


  @Query("SELECT u FROM UserEntity u WHERE u.name in :names")
  Set<UserEntity> findAllByNames(@Param("names") Set<String> names);

}
