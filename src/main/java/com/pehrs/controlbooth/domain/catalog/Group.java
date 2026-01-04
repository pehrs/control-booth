package com.pehrs.controlbooth.domain.catalog;

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
public class Group extends CatalogEntity {

  @ManyToOne
  @JoinColumn(name = "parent_id")
  private Group parent;

  @Column
  private String email;

  @Column(length = 512 * 1024)
  private String picture;

  @ManyToMany(mappedBy = "groups")
  private Set<User> users = new HashSet<>();

  @OneToMany(mappedBy = "parent")
  private Set<Group> children = new HashSet<>();

}
