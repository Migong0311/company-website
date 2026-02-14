# [Stage 1] 빌드 단계 (Node.js 사용)
FROM node:lts-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# [Stage 2] 실행 단계 (Nginx 사용)
FROM nginx:stable-alpine as production-stage

# 빌드 결과물(dist)을 Nginx 폴더로 복사
COPY --from=build-stage /app/dist /usr/share/nginx/html

# 아까 옮긴 nginx.conf 파일을 설정 폴더로 복사
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]