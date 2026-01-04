package com.pehrs.controlbooth.domain.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class User extends CatalogEntity {

  @Column(length = 128)
  private String email;

  @Column(length = 512 * 1024)
  private String picture;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "UserGroup",
      joinColumns = @JoinColumn(name = "userId"),
      inverseJoinColumns = @JoinColumn(name = "groupId")
  )
  private Set<Group> groups = new HashSet<>();

}
