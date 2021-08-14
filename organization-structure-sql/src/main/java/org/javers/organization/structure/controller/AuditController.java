package org.javers.organization.structure.controller;


import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import lombok.SneakyThrows;
import lombok.var;
import org.apache.commons.io.FileUtils;
import org.javers.core.Changes;
import org.javers.core.ChangesByObject;
import org.javers.core.Javers;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.organization.structure.domain.Employee;
import org.javers.organization.structure.domain.Hierarchy;
import org.javers.organization.structure.domain.HierarchyEmployees;
import org.javers.organization.structure.domain.HierarchyRepository;
import org.javers.organization.structure.domain.Person;
import org.javers.organization.structure.domain.PersonRepository;
import org.javers.organization.structure.domain.Position;
import org.javers.organization.structure.domain.Sex;
import org.javers.organization.structure.dto.HierarchyDto;
import org.javers.organization.structure.mapper.HierarchyMapper;
import org.javers.repository.jql.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/audit")
public class AuditController {

  private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

  private final Javers javers;
  private final HierarchyMapper hierarchyMapper;
  private final HierarchyRepository hierarchyRepository;
  private final PersonRepository personRepository;
  private final Iterator<String> strings;

  @Autowired
  public AuditController(Javers javers, HierarchyMapper hierarchyMapper, HierarchyRepository hierarchyRepository,
      PersonRepository personRepository) {
    this.javers = javers;
    this.hierarchyMapper = hierarchyMapper;
    this.hierarchyRepository = hierarchyRepository;
    this.personRepository = personRepository;
    var dictionary = loadDictionary();
    Collections.shuffle(dictionary);
    strings = Iterables.cycle(dictionary).iterator();
  }

  @SneakyThrows
  private List<String> loadDictionary() {
    var resource = getClass().getClassLoader().getResource("dictionary.txt");
    return FileUtils.readLines(new File(requireNonNull(resource).toURI()), StandardCharsets.UTF_8);
  }

  @PostMapping("/test")
  public void updateFrodo() {
    logger.info("updating Frodo ...");

    Person frodo = personRepository.findById(0).orElse(
        new Person(0, "Frodo", "Baggins", Sex.MALE, 9_000, Position.DEVELOPER)
    );
    logger.info(frodo.toString());

    frodo.setSalary(frodo.getSalary() + 100);
    personRepository.save(frodo);
  }

  @PostMapping("/test/hierarchy")
  public Hierarchy createHierarchy() {
    logger.info("creating hierarchy ...");

    String name = strings.next();
    String teamName = "team " + name;
    Person frodo = personRepository.findById(0).orElseGet(() -> {
      updateFrodo();
      return personRepository.findById(0).orElseThrow(IllegalStateException::new);
    });

    HierarchyEmployees employees = new HierarchyEmployees()
        .setSubordinate(new Employee()
            .setDomainName(name))
        .setBoss(
            new Employee()
                .setDomainName("frodo")
                .setPerson(frodo));
    Hierarchy defaultHierarchy = new Hierarchy()
        .setName(teamName)
        .setHierarchyEmployees(ImmutableList.of(employees));
    employees.setHierarchy(defaultHierarchy);
    Hierarchy hierarchy = hierarchyRepository.findById(teamName).orElse(defaultHierarchy);
    logger.info(hierarchy.toString());

    hierarchyRepository.save(hierarchy);
    return hierarchy;
  }

  @PutMapping("/test/hierarchy/{id}")
  @Transactional
  public String updateHierarchy(@PathVariable("id") String id) {
    logger.info("updating hierarchy ...");

    Hierarchy hierarchy = hierarchyRepository.getOne(id);
    HierarchyDto before = hierarchyMapper.toDto(hierarchy);
    Employee boss = hierarchy.getHierarchyEmployees().iterator().next().getBoss();

    HierarchyEmployees employees = new HierarchyEmployees()
        .setBoss(boss)
        .setSubordinate(new Employee().setDomainName(strings.next()));

    hierarchy.getHierarchyEmployees().add(employees);
    hierarchyRepository.save(hierarchy);
    HierarchyDto after = hierarchyMapper.toDto(hierarchy);
    Diff diff = javers.compare(before, after);

    return toJson(diff);
  }

  @GetMapping("/persons")
  public String getPersonChanges() {
    QueryBuilder jqlQuery = QueryBuilder.byClass(Person.class)
        .withNewObjectChanges();

    Changes changes = javers.findChanges(jqlQuery.build());

    return toJson(changes.groupByObject());
  }

  @GetMapping("/person/{id}")
  public String getPersonChanges(@PathVariable Integer id) {
    QueryBuilder jqlQuery = QueryBuilder.byInstanceId(id, Person.class)
        .withNewObjectChanges();
    Changes changes = javers.findChanges(jqlQuery.build());
    return toJson(changes.groupByCommit());
  }

  @GetMapping("/person/snapshots")
  public String getAllPersonSnapshots() {
    QueryBuilder jqlQuery = QueryBuilder.byClass(Person.class);

    List<CdoSnapshot> changes = new ArrayList<>(javers.findSnapshots(jqlQuery.build()));

    changes.sort((o1, o2) -> -1 * o1.getCommitMetadata().getCommitDate().compareTo(o2.getCommitMetadata().getCommitDate()));
    return toJson(changes);
  }

  @GetMapping("/person/{login}/snapshots")
  public String getPersonSnapshots(@PathVariable String login) {
    QueryBuilder jqlQuery = QueryBuilder.byInstanceId(login, Person.class);

    List<CdoSnapshot> changes = javers.findSnapshots(jqlQuery.build());

    changes.sort((o1, o2) -> -1 * o1.getCommitMetadata().getCommitDate().compareTo(o2.getCommitMetadata().getCommitDate()));

    return toJson(changes);
  }

  @GetMapping("/hierarchy/{left}/diff/{right}")
  public String getPersonDiffSnapshots(@PathVariable String left, @PathVariable String right) {
    Hierarchy l = hierarchyRepository.findById(left).orElseThrow(IllegalStateException::new);
    Hierarchy p = hierarchyRepository.findById(right).orElseThrow(IllegalStateException::new);

    Diff diff = javers.compare(l, p);

    return toJson(diff.getChanges());
  }

  private String toJson(Object object) {
    return javers.getJsonConverter().toJson(object);
  }

}
