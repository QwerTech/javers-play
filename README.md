
Sample application demonstrating the use of Javers with spring boot.

To start app execute :

```
./gradlew organization-structure-sql:run
```

## REST API

### Application exposes Swagger UI interface:

```http request
http://localhost:8080/swagger-ui/
```
## Simple values example
### Make some changes on Frodo

```http request
POST http://localhost:8080/audit/test
```

### View audit data

```http request
GET http://localhost:8080/audit/persons
GET http://localhost:8080/audit/person/0

GET http://localhost:8080/audit/person/snapshots
GET http://localhost:8080/audit/person/0/snapshots
```

## Updating collections and more complex structures
### Create some hierarchical team

```http request
POST http://localhost:8080/audit/test/hierarchy
```

### Update created hierarchical team and show the diff

```http request
PUT http://localhost:8080/audit/test/hierarchy/{id}
```
Example output
```
Diff:
* new object: Employee/Holegrim
* changes on Employee/Holegrim :
  - 'domainName' value changed from '' to 'Holegrim'
* new object: HierarchyEmployees/3
* changes on HierarchyEmployees/3 :
  - 'boss' reference changed from '' to 'Employee/frodo'
  - 'subordinate' reference changed from '' to 'Employee/Holegrim'
* changes on Hierarchy/team Hamegard :
  - 'hierarchyEmployees' collection changes :
    1. 'HierarchyEmployees/3' added
```

```json
{
  "changes": [
    {
      "changeType": "NewObject",
      "globalId": {
        "entity": "HierarchyEmployees",
        "cdoId": 3
      }
    },
    {
      "changeType": "NewObject",
      "globalId": {
        "entity": "Employee",
        "cdoId": "Holegrim"
      }
    },
    {
      "changeType": "ValueChange",
      "globalId": {
        "entity": "Employee",
        "cdoId": "Holegrim"
      },
      "property": "domainName",
      "propertyChangeType": "PROPERTY_VALUE_CHANGED",
      "left": null,
      "right": "Holegrim"
    },
    {
      "changeType": "ReferenceChange",
      "globalId": {
        "entity": "HierarchyEmployees",
        "cdoId": 3
      },
      "property": "boss",
      "propertyChangeType": "PROPERTY_VALUE_CHANGED",
      "left": null,
      "right": {
        "entity": "Employee",
        "cdoId": "frodo"
      }
    },
    {
      "changeType": "ReferenceChange",
      "globalId": {
        "entity": "HierarchyEmployees",
        "cdoId": 3
      },
      "property": "subordinate",
      "propertyChangeType": "PROPERTY_VALUE_CHANGED",
      "left": null,
      "right": {
        "entity": "Employee",
        "cdoId": "Holegrim"
      }
    },
    {
      "changeType": "ListChange",
      "globalId": {
        "entity": "Hierarchy",
        "cdoId": "team Hamegard"
      },
      "property": "hierarchyEmployees",
      "propertyChangeType": "PROPERTY_VALUE_CHANGED",
      "elementChanges": [
        {
          "elementChangeType": "ValueAdded",
          "index": 1,
          "value": {
            "entity": "HierarchyEmployees",
            "cdoId": 3
          }
        }
      ]
    }
  ]
}
```
### Show the diff of two hierarchies

```http request
GET http://localhost:8080/audit/hierarchy/{left}/diff/{right}
```