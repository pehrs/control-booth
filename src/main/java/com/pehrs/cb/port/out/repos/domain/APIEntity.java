package com.pehrs.cb.port.out.repos.domain;

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
public class APIEntity extends CatalogBaseEntity {

  @ManyToMany(mappedBy = "exposesApis")
  private Set<ComponentEntity> implementedBy = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "ComponentConsumesApi",
      joinColumns = @JoinColumn(name = "apiId"),
      inverseJoinColumns = @JoinColumn(name = "componentId")
  )
  private Set<ComponentEntity> consumedBy = new HashSet<>();

}
