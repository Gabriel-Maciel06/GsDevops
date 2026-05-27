# Estágio de Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio de Execução
FROM eclipse-temurin:21-jre-jammy

# Diretório de trabalho conforme regras DevOps
WORKDIR /app

# Criar grupo e usuário não privilegiado para execução segura (não rodar como root)
RUN groupadd -r appgroup && useradd -r -g appgroup -u 1001 appuser

# Copiar o jar gerado definindo a propriedade para o usuário não-root
COPY --from=build --chown=appuser:appgroup /app/target/*.jar app.jar

# Variável de ambiente configurada no Dockerfile
ENV SPRING_PROFILES_ACTIVE=prod

# Alterar para o usuário não-root
USER appuser

# Porta exposta para acesso à aplicação
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
