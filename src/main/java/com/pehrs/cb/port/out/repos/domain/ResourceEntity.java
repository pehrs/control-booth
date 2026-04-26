package com.pehrs.cb.port.out.repos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class ResourceEntity extends CatalogBaseEntity {

  @ManyToOne
  @JoinColumn(name = "system_id")
  private SystemEntity system;

  @ManyToOne
  @JoinColumn(name = "component_id")
  private ComponentEntity dependsOn;

}
