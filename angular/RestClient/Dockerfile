# Etapa 1: Construcción de la aplicación Angular
FROM node:20 AS build

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar archivos de package.json e instalar dependencias
COPY package*.json ./
RUN npm install

# Copiar el código fuente de Angular
COPY . .

# Compilar la aplicación Angular para producción
RUN npm run build -- --output-path=dist/app --configuration production

# Etapa 2: Servidor Nginx para producción
FROM nginx:alpine AS production

# Copiar los archivos de construcción generados en la etapa anterior a la carpeta de Nginx
COPY --from=build /app/dist/app/browser /usr/share/nginx/html

# Exponer el puerto 80 para Nginx
EXPOSE 7920

# Iniciar Nginx
CMD ["nginx", "-g", "daemon off;"]
