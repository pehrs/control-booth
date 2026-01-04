package com.pehrs.controlbooth.domain.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
//@Table(name = "\"system\"")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class System extends CatalogEntity{

  @ManyToOne
  @JoinColumn(name = "domain_id")
  private Domain domain;

  @OneToMany(mappedBy = "system")
  private Set<Resource> resources = new HashSet<>();

  @OneToMany(mappedBy = "system")
  private Set<Component> components = new HashSet<>();

}
