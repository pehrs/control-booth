package com.pehrs.cb.port.in;

import com.pehrs.cb.core.domain.Group;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GroupService {

    List<Group> findAll();

    Optional<Group> getByName(String name);

    List<Group> getChildren(Long parentId);

    Group get(final Long id);

    Long create(final Group groupDTO);

    void upsert(Group group);

    void update(final Long id, final Group groupDTO);

    void delete(final Long id);

    Map<Long, String> getGroupValues();

}
