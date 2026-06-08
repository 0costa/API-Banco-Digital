# Banco Digital - API de Transferência

API REST simplificada para simulação de um banco digital, permitindo transferências entre contas e registro de movimentações financeiras.

O projeto foi desenvolvido com foco em **consistência de dados, simplicidade arquitetural e boas práticas de backend com Spring Boot**.

---
## Tecnologias utilizadas

- Java 17
- Spring Boot
- Spring Data JPA
- Maven
- PostgreSQL
- Swagger / OpenAPI
- JUnit 5 + Mockito
- Docker
- Docker compose

---
## Acessos
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Colecao no Postman

---
## Como executar o projeto
O projeto utiliza Docker Compose para subir a aplicação e o banco de dados.

### Pré-requisitos
- Docker instalado
- Docker Compose funcionando
- Porta 8080 e 5432 livres

## Subindo a aplicação
    docker compose up --build

Isso irá:
- Subir o banco de dados
- Subir a API Spring Boot
- Expor a aplicação na porta 8080

---
## Como testar a API
Após subir o projeto, você pode testar os endpoints de duas formas:

### 1. Swagger UI

A documentação interativa da API está disponível via Swagger:

- Swagger UI: http://localhost:8080/swagger-ui.html

Nele você pode:

- Visualizar todos os endpoints
- Executar requisições diretamente pelo navegador
- Ver schemas de request/response
### 2. Postman (coleção pronta)

Também é possível testar a API utilizando o Postman.

- Abra o Postman
- Clique em Import
- Selecione o arquivo:
> /postman/api-banco-digital.postman_collection.json

---
##  Funcionalidades

### Gestão de Contas
- Consulta de contas
- Consulta de histórico de transações por conta

---
### Transferência de valores
- Transferência entre contas
- Validação de saldo suficiente
- Garantia de consistência transacional
- Proteção contra concorrência com **lock pessimista**

---
### Notificações
- Após uma transferência bem-sucedida, o sistema envia uma notificação simulada ao cliente.
- As notificações aparecerão no log do docker compose
---

## Decisões de arquitetura

### Separação por Use Cases
A lógica de negócio foi isolada em use cases para melhorar:
- testabilidade
- organização
- responsabilidade única

---

### Consistência de dados
Foi utilizado **pessimistic lock** na leitura das contas para evitar inconsistências em cenários de concorrência.

---

### Idempotência
Transferências possuem suporte a idempotência via chave de requisição, evitando duplicidade de operações.

---

### Domínio rico
A entidade `Conta` contém regras de negócio como:
- débito
- crédito
- validação de saldo

---

## Testes

O projeto possui **testes unitários cobrindo regras de negócio e casos de uso principais**, incluindo:

- Transferência com sucesso
- Saldo insuficiente
- Conta inexistente
- Validação de origem e destino iguais
- Idempotência de transferência
- Regras da entidade Conta
