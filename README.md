# sgv-api

Backend do Sistema de Gestão de Viagens (SGV). Spring Boot 4 + Java 21 + PostgreSQL.

## Rodar em dev

Precisa do Postgres rodando em `localhost:5432` com o banco `sgv_db` e os
scripts de `db/` (pasta pai) já aplicados — são eles que criam os usuários e o
schema. Guarde a senha do usuário `sgv_api` em `application-local.properties`
(no `.gitignore`):

```properties
spring.datasource.password=a-senha-que-voce-definiu
```

```bash
./mvnw spring-boot:run     # sobe em http://localhost:8080
./mvnw test                # roda a suíte
```

## Documentação da API

Com a app no ar:

| O quê | URL |
| --- | --- |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI (JSON) | http://localhost:8080/v3/api-docs |
| Health check | http://localhost:8080/actuator/health |

O Swagger é a fonte da verdade dos endpoints — é gerado a partir dos controllers.

## Configuração

Tudo em `src/main/resources/application.properties`, com default para dev e
override por variável de ambiente:

| Variável | Default |
| --- | --- |
| `SERVER_PORT` | `8080` |
| `DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/sgv_db` |
| `DATASOURCE_USERNAME` | `sgv_api` |
| `DATASOURCE_PASSWORD` | *(vazio — vem de `application-local.properties` ou do ambiente)* |
| `JPA_DDL_AUTO` | `validate` |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` |

```bash
SERVER_PORT=9000 ./mvnw spring-boot:run
```

Portas do preview em Docker: ver `PORTS.md` na pasta pai (convenção: preview = dev + 1).

## Pontos críticos

**O schema não é mais criado pelo Hibernate.** Ele vem de `db/03-schema.sql`
(pasta pai) e a app roda com `ddl-auto=validate`, porque o usuário `sgv_api` não
tem permissão de DDL — ver `SEGURANCA-BD.md`. Mudou uma `@Entity`? Atualize o
script e aplique com `sgv_migrator`, senão a app não sobe: o `validate` acusa a
diferença no boot (o que é melhor do que descobrir em runtime).

**`./mvnw test` escreve no banco de dev.** O `SgvApiApplicationTests` é
`@SpringBootTest` e conecta no `sgv_db` real. Rodar os testes altera seu banco. Ainda não há perfil de teste isolado (H2 / Testcontainers).

**Padrão de recurso: um pacote por entidade**, com 7 arquivos —
`Entity`, `Repository`, `Request`, `Response`, `Service`, `Controller`,
`NotFoundException`. Use `destino/` como modelo ao criar o próximo.

**Erros são tratados em um lugar só:** `shared/GlobalExceptionHandler`.
Recurso novo não escreve handler — basta estender `NotFoundException` (404) ou
`ConflictException` (409) de `shared/`. Validação de `@Valid` já vira 400
automaticamente. O corpo de erro é sempre `ApiError`.

**Enums persistidos usam `@Enumerated(EnumType.STRING)`**, nunca `ORDINAL` —
com ORDINAL, reordenar o enum corrompe os dados já gravados.

## Branches

Conforme a especificação: `main`, `develop`, `feature/*`, `release/x.y.z`.
Tags só em release (`v1.0.0`), nunca por commit.
