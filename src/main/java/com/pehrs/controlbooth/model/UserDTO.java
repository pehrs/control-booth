package com.pehrs.controlbooth.model;

import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString
public class UserDTO extends CatalogEntityDTO {

  @Size(max = 255)
  private String email;

  @Size(max = 16 * 1024)
  private String picture;

  // Group names
  private Set<String> groups = new HashSet<>();

}
