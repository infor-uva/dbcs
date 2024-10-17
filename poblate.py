import requests
import json

# Definir las URLs base para las rutas de la API
base_url = "http://localhost:8080"

# Datos en formato JSONvar users = [
users = [
    {
        "name": "John Doe",
        "email": "john.doe@example.com",
        "status": "NO_BOOKINGS",
    },
    {
        "name": "Pepe",
        "email": "pepe@example.com",
        "status": "WITH_ACTIVE_BOOKINGS",
    },
];
rooms = [
    {
        "roomNumber": "101",
        "roomType": "single",
        "available": True,
    },
    {
        "roomNumber": "102",
        "roomType": "double",
        "available": False,
    },
    {
        "roomNumber": "103",
        "roomType": "suite",
        "available": True,
    },
];
hotels = [
    {
        "name": "Hotel 1",
        "address": {
            "streetName": "Aca al lao",
            "streetKind": "Alargada",
            "number": 12,
            "postCode": "12345",
        },
        "rooms": rooms[0:-1],
    },
    {
        "name": "Hotel 2",
        "address": {
            "streetName": "Calle de la plaza",
            "streetKind": "Alargada",
            "number": 12,
            "postCode": "12345",
        },
        "rooms": rooms[-1:-2]
    },
];
bookings = [
    {
        "user": users[1],
        "room": rooms[0],
        "startDate": "2024-03-01",
        "endDate": "2024-03-08",
    },
    {
        "user": users[1],
        "room": rooms[1],
        "startDate": "2024-03-15",
        "endDate": "2024-03-22",
    },
];

# Función para enviar datos a la API
def post_data(endpoint, data):
    url = f"{base_url}/{endpoint}"
    headers = {'Content-Type': 'application/json'}
    response = requests.post(url, data=json.dumps(data), headers=headers)
    
    if response.status_code == 201:
        print(f"Success: {endpoint}")
    else:
        print(f"Failed: {endpoint} - Status Code: {response.status_code}, Response: {response.text}")

# Enviar usuarios
for user in users:
    post_data("users", user)

# Enviar hoteles
for hotel in hotels:
    post_data("hotels", hotel)

# Enviar habitaciones
for room in rooms:
    post_data(f"hotels/{room['hotelId']}/rooms", room)

# Enviar reservas
for booking in bookings:
    post_data("bookings", booking)