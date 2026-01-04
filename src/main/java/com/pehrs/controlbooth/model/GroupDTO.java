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
public class GroupDTO extends CatalogEntityDTO {

  @Size(max = 255)
  private String email;

  @Size(max = 255)
  private String picture;

  private String parentName;

  // User names/uid
  private Set<String> users = new HashSet<>();

  // Group names
  private Set<String> children = new HashSet<>();

}
