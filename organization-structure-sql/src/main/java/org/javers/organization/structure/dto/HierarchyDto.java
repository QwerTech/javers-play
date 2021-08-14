package org.javers.organization.structure.dto;


import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.javers.core.metamodel.annotation.TypeName;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@TypeName("Hierarchy")
public class HierarchyDto implements Serializable {

  @Id
  private String name;

  private List<HierarchyEmployeesDto> hierarchyEmployees;
}
