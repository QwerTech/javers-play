package org.javers.organization.structure.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.javers.core.metamodel.annotation.TypeName;


@Data
@NoArgsConstructor
@Accessors(chain = true)
@TypeName("HierarchyEmployees")
public class HierarchyEmployeesDto implements Serializable {

  @Id
  private int id;

  private EmployeeDto boss;

  private EmployeeDto subordinate;

}


