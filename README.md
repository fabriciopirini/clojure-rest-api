# Robot _VS_ Dino

## TODO

- Check Swagger (compojure-api) :white_check_mark:
- Implement update board
- Save new boards and its updates to board_list
- Add ID parameter to functions
- Implement Swagger

## List of Endpoints available

```
GET /simulation/all
GET /simulation/{simulation-id}
GET /simulation/{simulation-id}/{col}/{row}

POST /simulation/action/{simulation-id}/{col}/{row}/{action}
POST /simulation/dino/{simulation-id}/{col}/{row}
POST /simulation/robot/{simulation-id}/{col}/{row}/{direction}
POST /simulation

DELETE /simulation/{simulation-id}
DELETE /simulation/element/{simulation-id}/{col}/{row}
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

`GET /simulation/all`

**Response**

- `200 OK` on success

```json
{
  "simulations_list": [
    {
      "id": 2,
      "identifier": "simulation-2",
      "simulation_state": [
        "⛶", "🅃", "🄱",
        "⛶", "⛶", "⛶",
        "⛶", "🄳", "⛶",
      ]
    },
    {
      "id": 1,
      "identifier": "simulation-1",
      "simulation_state": [
        "🄳", "⛶", "⛶",
        "⛶", "🄻", "🄳",
        "⛶", "⛶", "⛶",
      ]
    }
  ]
}
```

### Get simulation current state

**Definition**

`GET /simulation/{simulation-id}`

**Parameters**

- `"simulation-id":number` id of simulation

**Response**

- `200 OK` on success
- `404 Not Found` if the simulation does not exists

```json
{
  "id": 1,
  "identifier": "simulation-1",
  "simulation_state": [
    "⛶", "🅃", "🄱",
    "⛶", "⛶", "⛶",
    "⛶", "🄳", "⛶",
  ]
}
```

### Get the element in that position on board

**Definition**

`GET /simulation/{simulation-id}/{col}/{row}`

**Parameters**

- `"simulation-id":number` id of simulation
- `"col":number` position of element on x-axis
- `"row":number` position of element on y-axis

**Response**

- `200 OK` on success
- `400 Bad Request` if the given position is invalid
- `404 Not Found` if the simulation does not exists

```json
{
  "identifier": "s1-col3-row4",
  "position_state": "🄳",
}
```

### Sends an action to a robot on desired simulation

**Definition**

`POST /simulation/action/{simulation-id}/{col}/{row}/{action}`

**Arguments**

- `"simulation-id":number` id of simulation
- `"col":number` collumn to be accessed
- `"row":number` row to be accessed
- `"action":string` single letter defining one action. Being those move **F**orward, move **B**ackwards, turn **R**ight, turn **L**eft or **A**ttack

If the simulation does not exists or the position is invalid, empty or a dinosaur, the action fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `400 Bad Request` if the position is invalid, empty or a dinosaur
- `404 Not Found` if the board was not found

```json
{
  "before": "col: 1, row: 3, direction: R",
  "after": "col: 2, row: 3, direction: R",
  "simulation_state": [
    "⛶", "🅃", "🄱",
    "⛶", "⛶", "⛶",
    "⛶", "🅁", "⛶",
  ]
}
```

### Place a new dino inside a simulation

**Definition**

`POST /simulation/dino/{simulation-id}/{col}/{row}`

**Arguments**

- `"simulation-id":number` id of simulation
- `"col":number` collumn to be accessed
- `"row":number` row to be accessed


If the simulation does not exists or the position is invalid or already taken, the request fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `400 Bad Request` if the position is invalid, empty or already taken or the board could not be updated on the board list
- `404 Not Found` if the board was not found

```json
{
  "id": 1,
  "identifier": "simulation-1",
  "simulation_state": [
    "⛶", "🅃", "🄱",
    "⛶", "⛶", "⛶",
    "⛶", "🄳", "⛶",
  ]
}
```

### Place a new robot inside a simulation

**Definition**

`POST /simulation/robot/{simulation-id}/{col}/{row}/{direction}`

**Arguments**

- `"simulation-id":number` id of simulation
- `"col":number` collumn to be accessed
- `"row":number` row to be accessed
- `"direction":keyword` single letter defining the direction. Being those looking **T**op, **B**ottom, **R**ight, **L**eft


If the simulation does not exists or the position is invalid or already taken, the request fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `400 Bad Request` if the position is invalid, empty or already taken
- `404 Not Found` if the board was not found

```json
{
  "simulation_state": [
    "⛶", "🅃", "🄱",
    "⛶", "⛶", "⛶",
    "⛶", "🄳", "⛶",
  ]
}
```

### Create a new simulation

`POST /simulation`

**Response**

- `200 OK` on success
- `404 Not Found` if the simulation already exists

```json
{
  "id": 42,
  "identifier": "simulation-42",
  "simulation_state": [
    "⛶", "⛶", "⛶",
    "⛶", "⛶", "⛶",
    "⛶", "⛶", "⛶",
  ]
}
```

### Delete a simulation

**Definition**

`DELETE /simulation/{simulation-id}`

**Arguments**

- `"simulation-id":number` id of simulation

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

`DELETE /simulation/{simulation-id}/{col}/{row}`

**Arguments**

- `"simulation-id":number` id of simulation
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
  "simulation_state": [
    "⛶", "🅃", "🄱",
    "⛶", "⛶", "⛶",
    "⛶", "⛶", "⛶",
  ]
}
```


## Project Decisions

- Use board indexes from 1 to 2500 instead of 0 to 2499;
- Use a 1D vector instead of implementing a new 2D data structure;
- Use simple characters to show dinos and robots + its directions instead of
more complex data structure;
- Adoption of TDD technique;
- Usage of Clojure standard testing library (clojure.test);
- Use Docker to run Uberjar file;
- Use Swagger for API testing and documentation;
