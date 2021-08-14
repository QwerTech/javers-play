package org.javers.organization.structure.dto;

import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.javers.core.metamodel.annotation.TypeName;
import org.javers.organization.structure.domain.Position;
import org.javers.organization.structure.domain.Sex;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@TypeName("Person")
public class PersonDto {

  @Id
  private int id;
  private String firstName;
  private String lastName;
  private Sex sex;
  private Integer salary;
  private Position position;
}

