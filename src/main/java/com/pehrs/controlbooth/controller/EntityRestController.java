package com.pehrs.controlbooth.controller;

import com.pehrs.controlbooth.model.CatalogEntityDTO;
import com.pehrs.controlbooth.service.CatalogEntityService;
import java.util.List;
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

    List<CatalogEntityDTO> all = entityService.findAllEntities();

    return ResponseEntity.ok(all);
  }

}
