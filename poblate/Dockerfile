FROM node:20

# Establecer el directorio de trabajo
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

CMD ["npm", "run", "prod", "--", "-d"]