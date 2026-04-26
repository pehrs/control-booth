package com.pehrs.cb.port.out.repos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
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
public class ComponentEntity extends CatalogBaseEntity {

  @ManyToOne
  @JoinColumn(name = "system_id")
  private SystemEntity system;

  @OneToMany(mappedBy = "dependsOn")
  private Set<ResourceEntity> resources = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "ComponentExposesApi",
      joinColumns = @JoinColumn(name = "componentId"),
      inverseJoinColumns = @JoinColumn(name = "apiId")
  )
  private Set<APIEntity> exposesApis = new HashSet<>();

  @ManyToMany(mappedBy = "consumedBy")
  private Set<APIEntity> consumes = new HashSet<>();

}
