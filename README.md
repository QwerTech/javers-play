# organization-structure
Sample application demonstrating the use of Javers with spring boot.

## spring-boot-starter-data-mongodb

To start app execute :

```
./gradlew organization-structure-sql:run
```

## REST API

### Application exposes Swagger UI interface:

```
http://localhost:8080/swagger-ui/
```
## Simple values example
### Make some changes on Frodo

```
POST http://localhost:8080/audit/test
```

### View audit data

```
GET http://localhost:8080/audit/persons
GET http://localhost:8080/audit/person/0

GET http://localhost:8080/audit/person/snapshots
GET http://localhost:8080/audit/person/0/snapshots
```

## Updating collections and more complex structures
### Create some hierarchical team

```
POST http://localhost:8080/audit/test/hierarchy
```

### Update created hierarchical team and show the diff

```
PUT http://localhost:8080/audit/test/hierarchy/{id}
```

### Show the diff of two hierarchies

```
GET http://localhost:8080/audit/hierarchy/{left}/diff/{right}
```