# Robot _VS_ Dino

## TODO

- Check Datomic
- Check Swagger :white_check_mark: (compojure-api)
- Remove /new on POST Endpoints??
- How to show simulation contents after a change??

## List of Endpoints available

```
GET /simulation/{id}
GET /simulation/{id}/{posX}/{posY}

POST /action/simulation/{id}/{posX}/{posY}/{action}
POST /dino/simulation/{id}/new/{posX}/{posY}
POST /robot/simulation/{id}/new/{posX}/{posY}/{direction}
POST /simulation/new

DELETE /simulation/{id}
DELETE /element/simulation/{id}
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

### Get board actual state

**Definition**

`GET /simulation/{id}`

**Parameters**

- `"ID":number` ID of simulation

**Response**

- `200 OK` on success
- `404 Not Found` if the simulation does not exists

```json
[
    {
        "identifier": "simulation-{id}",
        "simulation_state": []
    }
]
```

### Get the element on that position on board

**Definition**

`GET /simulation/{id}/{posX}/{posY}`

**Parameters**

- `"ID":number` ID of simulation
- `"posX":number` position of element on x-axis
- `"posY":number` position of element on y-axis

**Response**

- `200 OK` on success
- `404 Not Found` if the simulation does not exists

```json
[
    {
        "identifier": "s{id}-x{posX}-y{posY}",
        "position_state": "F",
    }
]
```

### Sends an action to a robot on desired simulation

**Definition**

`POST /action/simulation/{id}/{posX}/{posY}/{action}`

**Arguments**

- `"ID":number` ID of simulation
- `"posX":number` position of element on x-axis
- `"posY":number` position of element on y-axis
- `"action":string` single letter defining one action. Being those move **F**orward, move **B**ackwards, turn **R**ight, turn **L**eft or **A**ttack

If the simulation does not exists or the position is invalid, empty or a dinosaur, the action fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `403 Invalid Position` if the position is invalid, empty or a dinosaur
- `404 Not Found` if the board was not found

```json
{
    "before": "X: {posX}, Y: {posY}, direction: {old_direction}",
    "after": "X: {new_posX}, Y: {new_posY}, direction: {new_direction}",
    "new_simulation_state": "????"
}
```

### Place a new dino inside a simulation

**Definition**

`POST /dino/simulation/{id}/new/{posX}/{posY}`

**Arguments**

- `"ID":number` ID of simulation
- `"posX":number` position of dino on x-axis
- `"posY":number` position of dino on y-axis


If the simulation does not exists or the position is invalid or already taken, the request fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `403 Invalid Position` if the position is invalid, empty or already taken
- `404 Not Found` if the board was not found

```json
{
    "new_simulation_state": []
}
```

### Place a new robot inside a simulation

**Definition**

`POST /robot/simulation/{id}/new/{posX}/{posY}/{direction}`

**Arguments**

- `"ID":number` ID of simulation
- `"posX":number` position of dino on x-axis
- `"posY":number` position of dino on y-axis
- `"direction":string` single letter defining the direction. Being those **F**orward, **B**ackwards, **R**ight, **L**eft


If the simulation does not exists or the position is invalid or already taken, the request fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `403 Invalid Position` if the position is invalid, empty or already taken
- `404 Not Found` if the board was not found

```json
{
    "new_simulation_state": []
}
```

### Create a new simulation

`POST /simulation/new`

**Response**

- `200 OK` on success
- `404 Not Found` if the simulation already exists

```json
{
    "identifier": "simulation-42",
    "simulation_state": []
}
```

### Delete a simulation

**Definition**

`DELETE /simulation/{id}`

**Response**

- `200 OK` on success
- `404 Not Found` if the simulation does not exists

```json
{
    "identifier": "simulation-{id}"
}
```

### Delete an element inside a simulation

**Definition**

`DELETE /simulation/{id}/{posX}/{posY}`

**Response**

- `200 OK` on success
- `403 Invalid Position` if the position is invalid or empty
- `404 Not Found` if the simulation does not exists

```json
{
    "identifier": "simulation-{id}"
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
