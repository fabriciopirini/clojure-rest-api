# Robot _VS_ Dino

## TODO

- Check Datomic :x: (will add too much unnecessary complexity)
- Check Swagger :white_check_mark: (compojure-api)

## List of Endpoints available

```
GET /simulations
GET /simulation/{id}
GET /simulation/{id}/{posX}/{posY}

POST /action/simulation/{id}/{posX}/{posY}/{action}
POST /dino/simulation/{id}/{posX}/{posY}
POST /robot/simulation/{id}/{posX}/{posY}/{direction}
POST /simulation

DELETE /simulation/{id}
DELETE /element/simulation/{id}/{posX}/{posY}
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
- `404 Not Found` if there are no simulations running

```json
[
    {
        "simulations_list": {
            "simulation_1_state": [
              "⛶", "🅃", "🄱",
              "⛶", "⛶", "⛶",
              "⛶", "🄳", "⛶",
            ],
            "simulation_2_state": [
              "🄳", "⛶", "⛶",
              "⛶", "🄻", "🄳",
              "⛶", "⛶", "⛶",
            ],
        }

    }
]
```

### Get simulation current state

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
        "simulation_state": [
          "⛶", "🅃", "🄱",
          "⛶", "⛶", "⛶",
          "⛶", "🄳", "⛶",
        ]
    }
]
```

### Get the element in that position on board

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
- `400 Bad Request` if the position is invalid, empty or a dinosaur
- `404 Not Found` if the board was not found

```json
{
    "before": "X: {posX}, Y: {posY}, direction: {old_direction}",
    "after": "X: {new_posX}, Y: {new_posY}, direction: {new_direction}",
    "new_simulation_state": [
      "⛶", "🅃", "🄱",
      "⛶", "⛶", "⛶",
      "⛶", "🄳", "⛶",
    ]
}
```

### Place a new dino inside a simulation

**Definition**

`POST /dino/simulation/{id}/{posX}/{posY}`

**Arguments**

- `"ID":number` ID of simulation
- `"posX":number` position of dino on x-axis
- `"posY":number` position of dino on y-axis


If the simulation does not exists or the position is invalid or already taken, the request fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `400 Bad Request` if the position is invalid, empty or already taken
- `404 Not Found` if the board was not found

```json
{
    "new_simulation_state": [
      "⛶", "🅃", "🄱",
      "⛶", "⛶", "⛶",
      "⛶", "🄳", "⛶",
    ]
}
```

### Place a new robot inside a simulation

**Definition**

`POST /robot/simulation/{id}/{posX}/{posY}/{direction}`

**Arguments**

- `"ID":number` ID of simulation
- `"posX":number` position of dino on x-axis
- `"posY":number` position of dino on y-axis
- `"direction":keyword` single letter defining the direction. Being those looking **T**op, **B**ottom, **R**ight, **L**eft


If the simulation does not exists or the position is invalid or already taken, the request fails and the simulation keeps unchanged.

**Response**

- `200 OK` on success
- `400 Bad Request` if the position is invalid, empty or already taken
- `404 Not Found` if the board was not found

```json
{
    "new_simulation_state": [
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
- `400 Bad Request` if the position is invalid or empty
- `404 Not Found` if the simulation does not exists

```json
{
    "identifier": "simulation-{id}",
    "new_simulation_state": [
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
