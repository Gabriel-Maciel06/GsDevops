# 🛰️ AeroSoil AI - DevOps & Cloud Computing

O **AeroSoil AI** é uma plataforma de agricultura de precisão inteligente projetada para otimizar o uso da água e maximizar a segurança alimentar, atendendo diretamente aos Objetivos de Desenvolvimento Sustentável da ONU de **Fome Zero (ODS 2)** e **Ação Contra a Mudança Climática (ODS 13)**.

Esta solução integra:
*   **Visão Macro (Espaço)**: Consome dados climáticos orbitais das constelações da NASA/ESA para prever ondas de calor.
*   **Visão Micro (Terra)**: Utiliza estações IoT com o microcontrolador ESP32 para monitorar a umidade local do solo e a luminosidade em tempo real.
*   **Inteligência & Automação**: Um ecossistema inteligente em nuvem cruza esses dados e toma decisões automatizadas (ex.: ativar a irrigação e disparar alertas no aplicativo mobile quando o solo está seco e há uma seca iminente).

---

## 👥 Integrantes da Equipe (Turma 2TDSR)
*   **Gabriel Maciel Alves de Oliveira - RM 562795 (Representante)**
*   **Vitória Rodrigues Martins - RM 565160**
*   **Augusto Bonomo Junior - RM 565155**
*   **Thomas Fontes - RM 562254**
*   **Matheus Pereira Molina - RM 563399**

---

## 📐 Desenho Macro da Arquitetura da Solução

Abaixo está o desenho da arquitetura macro da solução implementada em Nuvem (Azure VM + Containers Docker), demonstrando o fluxo de dados dos sensores IoT, o consumo de satélites e a comunicação interna através de uma rede de ponte Docker isolada:

![AeroSoil AI Architecture](assets/aerosoil_architecture.png)

---

## 🛠️ Detalhes dos Containers (DevOps Rules)

### 1. Container da API (Java Spring Boot)
*   **Nome do Container**: `api-562795` (Contém o RM do representante).
*   **Construção**: Imagem personalizada construída sob demanda usando um **Dockerfile multi-stage** (compilação rápida com Maven e execução isolada via JRE 21).
*   **Segurança**: Executado com usuário de baixo privilégio (**`appuser`** da UID 1001) para conformidade com as diretrizes de segurança de contêineres.
*   **Diretório de Trabalho**: `/app`.
*   **Variáveis de Ambiente**: Define chaves do token JWT e strings de conexão que sobrepõem as propriedades padrões da aplicação.
*   **Portas**: Exposição da porta externa `8080` mapeada para a porta interna `8080`.

### 2. Container do Banco de Dados (Oracle Free 23c)
*   **Nome do Container**: `db-562795` (Contém o RM do representante).
*   **Imagem Base**: `gvenzl/oracle-free:23-slim-faststart` (imagem oficial leve e inicialização expressa).
*   **Persistência**: Utiliza o volume nomeado **`oracle_data_volume`** mapeado no caminho de persistência física `/opt/oracle/oradata`.
*   **Inicialização**: O script de criação do schema (`db_setup.sql`) e de procedures/views (`db_procedures.sql`) são montados em `/container-entrypoint-initdb.d/` para execução automática no primeiro startup do banco.
*   **Portas**: Porta `1521:1521` exposta para comunicação externa.

---

## 🚀 How-To: Executar a Solução (Do Clone ao Teste)

Siga os passos detalhados abaixo para baixar, subir o ambiente, ver os logs e executar as validações exigidas.

### Passo 1: Clonar o Repositório
Abra o seu terminal no host (com Docker instalado) e execute:
```bash
git clone https://github.com/Gabriel-Maciel06/GsDevops.git
cd GsDevops
```

### Passo 2: Subir a Infraestrutura em Segundo Plano (Background)
Execute o Docker Compose para compilar a API personalizada e subir o Oracle Database em segundo plano:
```bash
docker-compose up --build -d
```
> [!NOTE]
> O banco de dados Oracle leva alguns segundos para completar a primeira inicialização. A API possui dependência de saúde (`depends_on: { db: { condition: service_healthy } }`), portanto ela só iniciará após a confirmação de que o Oracle está pronto para conexões.

### Passo 3: Exibir e Acompanhar os Logs dos Contêineres
Após rodar o comando em background, você pode exibir e acompanhar os logs para auditar o startup da solução:
```bash
# Acompanhar logs de todos os contêineres
docker-compose logs -f

# Ou monitorar individualmente
docker container logs api-562795
docker container logs db-562795
```

### Passo 4: Auditar os Contêineres (Validação de Diretório e Usuário)

#### A. Acessar o Contêiner da API
Conecte-se ao terminal da API para provar que a aplicação não roda como root:
```bash
docker container exec -it api-562795 /bin/bash
```
Dentro do terminal do contêiner, execute os comandos:
```bash
# 1. Identificar o usuário logado (Deve ser appuser e não root)
whoami

# 2. Exibir o diretório de trabalho atual (Deve ser /app)
pwd

# 3. Listar a estrutura de diretórios em formato detalhado
ls -l
```
Para sair do contêiner, digite `exit`.

#### B. Acessar o Contêiner do Banco de Dados
Conecte-se ao terminal do banco de dados para auditar a estrutura:
```bash
docker container exec -it db-562795 /bin/bash
```
Dentro do terminal do contêiner do banco de dados, execute os comandos:
```bash
whoami
pwd
ls -la /opt/oracle/oradata
```
Para sair do contêiner, digite `exit`.

### Passo 5: Prova de Persistência no Banco (Consultas SELECT)

Conecte-se diretamente à interface CLI do Banco de Dados (`sqlplus`) executada dentro do próprio contêiner do banco para rodar consultas SQL nas tabelas criadas automaticamente:

```bash
# Acessar diretamente o SQL*Plus dentro do contêiner do banco
docker container exec -it db-562795 sqlplus RM562795/oracle@//localhost:1521/FREEPDB1
```

Após o prompt abrir, execute as seguintes consultas SQL para verificar as tabelas e a estrutura relacional do AeroSoil AI:

```sql
-- 1. Verificar a estrutura das tabelas existentes no schema do usuário
SELECT table_name FROM user_tables;

-- 2. Consultar registros na tabela de usuários
SELECT id_usuario, nome, email, perfil FROM TB_USUARIO;

-- 3. Consultar registros na tabela de sensores
SELECT id_sensor, tipo_sensor, modelo, status, id_propriedade FROM TB_SENSOR;

-- 4. Consultar se há algum alerta gerado automaticamente
SELECT * FROM TB_ALERTA;

-- 5. Exibir as views criadas pelas procedures
SELECT view_name FROM user_views;
```
Para sair do SQL*Plus, digite `EXIT;`.

---

## 📑 Endpoints da API para Teste Rápido

Uma vez que a aplicação esteja no ar, você pode acessar a interface interativa do Swagger para realizar o CRUD nas tabelas:
*   **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
*   **Docs JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
