# Etapa de construção (Build)
FROM ubuntu:latest AS build

# Atualiza o sistema e instala o OpenJDK 17
RUN apt-get update && apt-get install -y openjdk-17-jdk

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos do projeto para o diretório de trabalho
COPY . .

# Instala o Maven
RUN apt-get install -y maven

# Executa o comando Maven para construir o projeto
RUN mvn clean install

# Etapa de produção
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho
WORKDIR /app

# Expõe a porta 8080, se necessário
# EXPOSE 8080

# Copia o arquivo JAR do estágio de construção para a etapa de produção
COPY --from=build /app/target/todolist-0.0.1.jar app.jar

# Define o comando de entrada
ENTRYPOINT ["java", "-jar", "app.jar"]
