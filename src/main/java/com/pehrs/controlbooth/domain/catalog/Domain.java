package com.pehrs.controlbooth.domain.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
//@Table(name = "\"domain\"")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Domain extends CatalogEntity {

  @OneToMany(mappedBy = "domain")
  private Set<System> systems = new HashSet<>();

}
