package org.javers.organization.structure.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.javers.core.metamodel.annotation.TypeName;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@TypeName("Employee")
public class EmployeeDto implements Serializable {

  @Id
  private String domainName;

  private PersonDto person;
}

