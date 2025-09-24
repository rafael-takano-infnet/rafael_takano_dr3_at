# Todo API - Sistema de Tarefas

AT - Desenvolvimento de Serviços Web e Testes com Java

## Como usar

- Inicie o servidor: `./gradlew run` (porta 7000)
- Testes: `./gradlew test`

## Endpoints

- `GET /hello` - Teste do servidor
- `GET /tarefas` - Lista tarefas
- `POST /tarefas` - Cria tarefa
- `GET /tarefas/{id}` - Busca tarefa por ID

### Exemplo de tarefa

```json
{
  "titulo": "Estudar",
  "descricao": "Prova dia DD/MM"
}
```

## Tecnologias

Java 21, Javalin 6.6.0, JUnit, Gradle

## Estrutura

```
src/
├── main/java/com/todoapp/
│   ├── TodoApp.java
│   ├── model/Tarefa.java
│   ├── service/
│   ├── controller/
│   └── client/
└── test/
```

Projeto acadêmico sobre APIs REST com Java.
