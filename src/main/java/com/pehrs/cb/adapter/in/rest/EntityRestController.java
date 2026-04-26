package com.pehrs.cb.adapter.in.rest;

import com.pehrs.cb.core.domain.CatalogEntity;
import com.pehrs.cb.core.service.CatalogEntityServiceImpl;
import java.util.List;

import com.pehrs.cb.port.in.CatalogEntityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/entity")
@Slf4j
@AllArgsConstructor
public class EntityRestController {

  CatalogEntityService entityService;

  @GetMapping()
  public ResponseEntity<?> getAll() {

    List<CatalogEntity> all = entityService.findAllEntities();

    return ResponseEntity.ok(all);
  }

}
