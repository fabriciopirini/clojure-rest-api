# Robot _VS_ Dino

## About

This project is an application to simulate a fight between Robots and Dinosaurs on a 50x50 grid and, via API, it is be possible to place new Robots and Dinosaurs and issue movements and attacks to the machines. It is possible to create and maintain more than one simulation at time and check their current states.

Features:
- Create a new simulation;
- Get list of created simulations;
- Get simulation current state;
- Get the element in certain position on simulation;
- Place a new dino inside a simulation;
- Place a new robot inside a simulation and choose its facing direction;
- Move/rotate a robot on desired simulation;
- Issue an order to a robot to attack on desired simulation;
- Delete a simulation;
- Delete an element from desired  simulation;

## Documentation

This README contains the documentation with all endpoints and how to run the code. Swagger is implemented and it has a duplicate of this docs and it is another way to test the application. Even though it is set up, everything will run fine via UNIX command.

## How to Run

There are 3 alternatives to run this project. For these commands to work, they must all be run inside the project root folder.

**Due to the addition of a method in the java.util.Collection interface on JDK 11, this project must run using JDK 8.** [Link to explanation](https://www.deps.co/blog/how-to-upgrade-clojure-projects-to-use-java-11/#java-util-collection-toarray)

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
POST /simulations/{simulationId}/robots/{col}/{row}?direction={direction}
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

### Get the element in certain position on simulation

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
  "element": "D",
}
```

### Place a new dino inside a simulation

**Definition**

`POST /simulations/{simulationId}/dinos/{col}/{row}`

**Arguments**

- `"simulationId":number` id of simulation
- `"col":number` collumn to be accessed
- `"row":number` row to be accessed


If the simulation does not exists or the position is invalid or already taken, the request fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `400 Bad Request` if the position is invalid, empty or already taken
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

### Place a new robot inside a simulation and choose its facing direction

**Definition**

`POST /simulations/{simulationId}/robots/{col}/{row}?direction={direction}`

**Arguments**

- `"simulationId":number` id of simulation
- `"col":number` collumn to be accessed
- `"row":number` row to be accessed
- `"direction":string (optional)` single letter defining the direction. Being those **lookingUp**, **lookingDown**, **lookingLeft** or **lookingRight**


If the simulation does not exists or the position is invalid or already taken, the request fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `400 Bad Request` if the position is invalid, empty or already taken
- `404 Not Found` if the simulation does not exists

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
- `400 Bad Request` if the simulation could not be created

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
- `"instruction":string (optional)` instruction to be performed. Being those **goForward**, **goBackwards**, **turnLeft** or **turnRight**.

If the simulation does not exists or the position is invalid, empty or a dinosaur, the action fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `400 Bad Request` if the position is invalid, empty or a dinosaur
- `404 Not Found` if the simulation does not exists

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

### Issue an order to a robot to attack on desired simulation

**Definition**

`POST /simulations/{simulationId}/attacks/{col}/{row}?attackDirection={attackDirection}`

**Arguments**

- `"simulationId":number` id of simulation
- `"col":number` collumn to be accessed
- `"row":number` row to be accessed
- `"attackDirection":string (optional)` action to be performed. Being those **up**, **down**, **toTheLeft** or **toTheRight**

If the simulation does not exists or the position is invalid, empty or a dinosaur, the action fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `400 Bad Request` if the position is invalid, empty or a dinosaur
- `404 Not Found` if the simulation does not exists

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

`DELETE /simulations/{simulationId}/elements/{col}/{row}`

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

- **Use a 1D vector instead of implementing a new 2D data structure**: A 1D vector was preferred in order to not have a simple and straightforward implementation of a matrix on Clojure so it was chosen to use a simple structure to simulate it;
- **Use simple characters to show dinos and robots + its directions instead of
more complex data structure**: Since just a few directions were used, they could be expressed as single characters;
- **Adoption of TDD technique**: The Test-Driven Development has been chosen in order to help a better and healthier coding of the application and it helped to speed up the process since the feedback for all changes are immediate;
- **Usage of Clojure standard testing library (clojure.test)**: The native library is simple and powerful enough and fits perfectly the test cases for this project;
- **Use Docker and Uberjar file**: Docker and Uberjar were set up in order to make the portability, running and testing easier and painless;
- **Use Swagger for API testing and documentation**: Swagger was picked to provide a nice interface to easily test all endpoints with instant and a visual feedback;
