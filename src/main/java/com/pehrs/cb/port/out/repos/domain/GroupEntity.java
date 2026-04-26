package com.pehrs.cb.port.out.repos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class GroupEntity extends CatalogBaseEntity {

  @ManyToOne
  @JoinColumn(name = "parent_id")
  private GroupEntity parent;

  @Column
  private String email;

  @Column(length = 512 * 1024)
  private String picture;

  @ManyToMany(mappedBy = "groups")
  private Set<UserEntity> users = new HashSet<>();

  @OneToMany(mappedBy = "parent")
  private Set<GroupEntity> children = new HashSet<>();

}
