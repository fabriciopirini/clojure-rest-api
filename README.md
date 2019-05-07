# Robot _VS_ Dino

## List of Endpoints available

```
GET /simulation/{id}
GET /simulation/{id}/{posX}/{posY}

POST /action/{posX}/{posY}/{action}/simulation/{id}
POST /simulation/new
POST /dino/new/{posX}/{posY}/simulation/{id}
POST /robot/new/{posX}/{posY}/simulation/{id}
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

- `ID` Simulation's ID

**Response**

- `200 OK` on success
- `404 Not Found` if the simulation does not exist

```json
[
    {
        "identifier": "simulation-1",
        "name": "Simulation 1",
        "content": []
    }
]
```

### Get the element on that position on board

**Definition**

`GET /simulation/{id}/{posX}/{posY}`

**Parameters**

- `ID` Simulation's ID
- `posX` X-axis position of element
- `posY` Y-axis position of element

**Response**

- `200 OK` on success
- `404 Not Found` if the simulation does not exist

```json
[
    {
        "identifier": "s{id}-x{posX}-y{posY}",
        "content": "F",
    }
]
```

### Registering a new device

**Definition**

`POST /devices`

**Arguments**

- `"identifier":string` a globally unique identifier for this device
- `"name":string` a friendly name for this device
- `"device_type":string` the type of the device as understood by the client
- `"controller_gateway":string` the IP address of the device's controller

If a device with the given identifier already exists, the existing device will be overwritten.

**Response**

- `201 Created` on success

```json
{
    "identifier": "floor-lamp",
    "name": "Floor Lamp",
    "device_type": "switch",
    "controller_gateway": "192.1.68.0.2"
}
```

### Lookup device details

`GET /device/<identifier>`

**Response**

- `404 Not Found` if the device does not exist
- `200 OK` on success

```json
{
    "identifier": "floor-lamp",
    "name": "Floor Lamp",
    "device_type": "switch",
    "controller_gateway": "192.1.68.0.2"
}
```

### Delete a device

**Definition**

`DELETE /devices/<identifier>`

**Response**

- `404 Not Found` if the device does not exist
- `204 No Content` on success

## Project Decisions

- Use board indexes from 1 to 2500 instead of 0 to 2499;
- Use a 1D vector instead of implementing a new 2D data structure;
- Use simple characters to show dinos and robots + its directions instead of
more complex data structure;
- Adoption of TDD technique;
- Usage of Clojure standard testing library (clojure.test);
