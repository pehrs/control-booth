package com.pehrs.cb.core.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString
public class Resource extends CatalogEntity {

    private String systemId;

    private String dependsOn;
}
