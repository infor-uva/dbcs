#!/bin/bash

# Configuración de la conexión a MySQL
MYSQL_USER="user"          # Usuario de MySQL
MYSQL_PASSWORD="password"  # Contraseña del usuario
MYSQL_HOST="localhost"     # Dirección del servidor MySQL
MYSQL_PORT="3306"          # Puerto de MySQL (3306 por defecto)

# Nombres de las bases de datos a eliminar
DATABASES=("Users" "Hotels" "Bookings")

# Confirmación antes de eliminar
echo "Las siguientes bases de datos serán eliminadas: ${DATABASES[*]}"
read -p "¿Estás seguro de que deseas continuar? (escribe 'yes' para confirmar): " CONFIRMATION

if [ "$CONFIRMATION" != "yes" ]; then
    echo "Cancelado. Las bases de datos no se eliminarán."
    exit 1
fi

# Eliminar cada base de datos
for DB in "${DATABASES[@]}"; do
    echo "Eliminando base de datos: $DB"
    mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -h"$MYSQL_HOST" -P"$MYSQL_PORT" -e "DROP DATABASE IF EXISTS $DB;"
    if [ $? -eq 0 ]; then
        echo "Base de datos $DB eliminada correctamente."
    else
        echo "Error al eliminar la base de datos $DB."
    fi
done

echo "Proceso completado."
