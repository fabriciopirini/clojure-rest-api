# Robot _VS_ Dino

## TODO

<!-- - DONE :white_check_mark: -->
- Warn when trying to move outside the limits

## How to Run

There are 3 alternatives to run this project. For these commands to work, they must all be run inside the project root folder.

**Due to the addition of a method in the java.util.Collection interface on JDK 11, this project must run using JDK 9.** [Link to explanation](https://www.deps.co/blog/how-to-upgrade-clojure-projects-to-use-java-11/#java-util-collection-toarray)

### Starting a web server

```
lein ring server
```

### Running it in a container with Docker

```
docker build --tag=robot_vs_dino .

docker run -p 8080:3000 robot_vs_dino
```

### Executable jar file

Lein-Ring can generate executable jar files for deployment purposes:

```
lein ring uberjar
```

This generates a jar file with all dependencies and execute it with:

```
java -jar target/uberjar/robot-vs-dino.jar
```

## Testing

To run the implemented tests, use the following command:

```
lein test
```

## List of Endpoints available

```
GET /simulations
GET /simulations/{simulationId}
GET /simulations/{simulationId}/elements/{col}/{row}

POST /simulations
POST /simulations/{simulationId}/dinos/{col}/{row}
POST /simulations/{simulationId}/robots/{col}/{row}/{direction}
POST /simulations/{simulationId}/instructions/{col}/{row}?instruction={instruction}
POST /simulations/{simulationId}/attacks/{col}/{row}?attackDirection={attackDirection}

DELETE /simulations/{simulationId}
DELETE /simulations/{simulationId}/elements/{col}/{row}
```

## Usage

All responses will have the form

```json
{
  "data": "Mixed type holding the content of the response",
  "message": "Description of what happened"
}
```

Subsequent response definitions will only detail the expected value of the `data field`

### Get list of created simulations

**Definition**

`GET /simulations`

**Response**

- `200 OK` on success

```json
{
  "simulations_list": [
    {
      "id": 2,
      "identifier": "simulation-2",
      "currentState": [
        "⛶", "T", "B",
        "⛶", "⛶", "⛶",
        "⛶", "D", "⛶",
      ]
    },
    {
      "id": 1,
      "identifier": "simulation-1",
      "currentState": [
        "D", "⛶", "⛶",
        "⛶", "L", "D",
        "⛶", "⛶", "⛶",
      ]
    }
  ]
}
```

### Get simulation current state

**Definition**

`GET /simulations/{simulationId}`

**Parameters**

- `"simulationId":number` id of simulation

**Response**

- `200 OK` on success
- `404 Not Found` if the simulation does not exists

```json
{
  "id": 1,
  "identifier": "simulation-1",
  "currentState": [
    "⛶", "T", "B",
    "⛶", "⛶", "⛶",
    "⛶", "D", "⛶",
  ]
}
```

### Get the element in that position on simulation

**Definition**

`GET /simulations/{simulationId}/elements/{col}/{row}`

**Parameters**

- `"simulationId":number` id of simulation
- `"col":number` collumn to be accessed
- `"row":number` row to be accessed

**Response**

- `200 OK` on success
- `400 Bad Request` if the given position is invalid
- `404 Not Found` if the simulation does not exists

```json
{
  "identifier": "s1-col3-row4",
  "position_state": "D",
}
```


### Place a new dino inside a simulation

**Definition**

`POST /simulations/dino/{simulationId}/{col}/{row}`

**Arguments**

- `"simulationId":number` id of simulation
- `"col":number` collumn to be accessed
- `"row":number` row to be accessed


If the simulation does not exists or the position is invalid or already taken, the request fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `400 Bad Request` if the position is invalid, empty or already taken or the simulation could not be updated on the simulation list
- `404 Not Found` if the simulation was not found

```json
{
  "id": 1,
  "identifier": "simulation-1",
  "currentState": [
    "⛶", "T", "B",
    "⛶", "⛶", "⛶",
    "⛶", "D", "⛶",
  ]
}
```

### Place a new robot inside a simulation

**Definition**

`POST /simulations/robot/{simulationId}/{col}/{row}/{direction}`

**Arguments**

- `"simulationId":number` id of simulation
- `"col":number` collumn to be accessed
- `"row":number` row to be accessed
- `"direction":keyword` single letter defining the direction. Being those looking **T**op, **B**ottom, **R**ight, **L**eft


If the simulation does not exists or the position is invalid or already taken, the request fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `400 Bad Request` if the position is invalid, empty or already taken
- `404 Not Found` if the simulation was not found

```json
{
  "currentState": [
    "⛶", "T", "B",
    "⛶", "⛶", "⛶",
    "⛶", "D", "⛶",
  ]
}
```

### Create a new simulation

`POST /simulations`

**Response**

- `200 OK` on success
- `404 Not Found` if the simulation already exists

```json
{
  "id": 42,
  "identifier": "simulation-42",
  "currentState": [
    "⛶", "⛶", "⛶",
    "⛶", "⛶", "⛶",
    "⛶", "⛶", "⛶",
  ]
}
```

### Move/rotate a robot inside an existing simulation

**Definition**

`POST /simulations/{simulationId}/instructions/{col}/{row}?instruction={instruction}`

**Arguments**

- `"simulationId":number` id of simulation
- `"col":number` collumn to be accessed
- `"row":number` row to be accessed
- `"instruction":string` instruction to be performed. Being those goForward, goBackwards, turnLeft or turnRight.

If the simulation does not exists or the position is invalid, empty or a dinosaur, the action fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `400 Bad Request` if the position is invalid, empty or a dinosaur
- `404 Not Found` if the simulation was not found

```json
{
  "before": "col: 1, row: 3, direction: R",
  "after": "col: 2, row: 3, direction: R",
  "currentState": [
    "⛶", "T", "B",
    "⛶", "⛶", "⛶",
    "⛶", "R", "⛶",
  ]
}
```

### Sends an action to a robot on desired simulation

**Definition**

`POST /simulations/{simulationId}/attacks/{col}/{row}?attackDirection={attackDirection}`

**Arguments**

- `"simulationId":number` id of simulation
- `"col":number` collumn to be accessed
- `"row":number` row to be accessed
- `"attackDirection":string` action to be performed. Being those up, down, toTheLeft or toTheRight

If the simulation does not exists or the position is invalid, empty or a dinosaur, the action fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `400 Bad Request` if the position is invalid, empty or a dinosaur
- `404 Not Found` if the simulation was not found

```json
{
  "before": "col: 1, row: 3, direction: R",
  "after": "col: 2, row: 3, direction: R",
  "currentState": [
    "⛶", "T", "B",
    "⛶", "⛶", "⛶",
    "⛶", "R", "⛶",
  ]
}
```

### Delete a simulation

**Definition**

`DELETE /simulations/{simulationId}`

**Arguments**

- `"simulationId":number` id of simulation

**Response**

- `200 OK` on success
- `404 Not Found` if the simulation does not exists

```json
{
  "identifier": "simulation-1"
}
```

### Delete an element inside a simulation

**Definition**

`DELETE /simulations/{simulationId}/{col}/{row}`

**Arguments**

- `"simulationId":number` id of simulation
- `"col":number` collumn to be accessed
- `"row":number` row to be accessed

**Response**

- `200 OK` on success
- `400 Bad Request` if the position is invalid or empty
- `404 Not Found` if the simulation does not exists

```json
{
  "id": 1,
  "identifier": "simulation-1",
  "currentState": [
    "⛶", "T", "B",
    "⛶", "⛶", "⛶",
    "⛶", "⛶", "⛶",
  ]
}
```


## Project Decisions

- Use simulation indexes from 1 to 2500 instead of 0 to 2499;
- Use a 1D vector instead of implementing a new 2D data structure;
- Use simple characters to show dinos and robots + its directions instead of
more complex data structure;
- Adoption of TDD technique;
- Usage of Clojure standard testing library (clojure.test);
- Use Docker to run Uberjar file;
- Use Swagger for API testing and documentation;
