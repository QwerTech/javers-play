package org.javers.organization.structure.mapper;

import org.javers.organization.structure.domain.Hierarchy;
import org.javers.organization.structure.dto.HierarchyDto;
import org.mapstruct.Mapper;

@Mapper(config = CentralConfig.class)
public interface HierarchyMapper {

  HierarchyDto toDto(Hierarchy domain);
}
