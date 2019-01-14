FROM node:10.15-stretch as builder

WORKDIR /app
ADD package.json package.json
ADD package-lock.json package-lock.json
RUN npm ci

COPY src src
COPY angular.json angular.json
COPY tsconfig.json tsconfig.json
COPY tslint.json tslint.json
RUN npm run prod

FROM nginx:1.15.8

COPY mime.types /etc/nginx/mime.types
COPY nginx.conf /etc/nginx/nginx.conf

COPY --from=builder /app/dist/ida-chatbot /app
