package com.pehrs.controlbooth.domain.catalog;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "\"entity\"")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
public abstract class CatalogEntity {

  @Id
  @Column(nullable = false, updatable = false)
  @SequenceGenerator(
      name = "primary_sequence",
      sequenceName = "primary_sequence",
      allocationSize = 1,
      initialValue = 10000
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "primary_sequence"
  )
  private Long id;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private OffsetDateTime dateCreated;

  @LastModifiedDate
  @Column(nullable = false)
  private OffsetDateTime lastUpdated;

  @Column(nullable = false)
  private String name;

  @Column(nullable = true)
  private String description;

  @Column(nullable = false)
  private String displayName;

  @Column(nullable = true)
  private String entityVariant;

  @ElementCollection
  @MapKeyColumn(name="name")
  @Column(name="value")
  @CollectionTable(name="entity_annotation", joinColumns=@JoinColumn(name="entity_id"))
  Map<String, String> annotations = new HashMap<>();

}
