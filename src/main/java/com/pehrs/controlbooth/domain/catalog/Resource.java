package com.pehrs.controlbooth.domain.catalog;

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
public class Resource extends CatalogEntity {

  @ManyToOne
  @JoinColumn(name = "system_id")
  private System system;

  @ManyToOne
  @JoinColumn(name = "component_id")
  private Component attachedTo;

}
