package com.pehrs.controlbooth.repos;

import com.pehrs.controlbooth.domain.catalog.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  List<User> findAllByGroupsId(Long id);

  Optional<User> findByName(String name);

  Optional<User> findByEmail(String email);


  @Query("SELECT u FROM User u JOIN FETCH u.annotations WHERE u.name = :username")
  Optional<User> findUserAndAnnotationsByUserId(@Param("username") String username);


  @Query("SELECT u FROM User u WHERE u.name in :names")
  Set<User> findAllByNames(@Param("names") Set<String> names);

}
