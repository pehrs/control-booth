package com.pehrs.controlbooth.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString
public class CatalogEntityDTO {

  // Mapped from dtype
  @Size(max = 255)
  private String entityType;

  private Long id;

  private OffsetDateTime dateCreated;

  private OffsetDateTime lastUpdated;

  @NotNull
  @Size(max = 255)
  private String name;

  @NotNull
  @Size(max = 255)
  private String displayName;

  @Size(max = 255)
  private String description;

  @Size(max = 255)
  private String entityVariant;

  Map<String, String> annotations = new HashMap<>();

}
