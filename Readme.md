# Gradle Plugins

## Valid migrations order

Valida a ordem de migrations do flyway, verificando o histórico do git para garantir que não está sendo criada uma
versão antes da última versão aplicada.

A seguinte propriedade pode ser utilizada para configurar o diretório de migrations(default="src/main/resources/"):

```
  valid_migrations_order.migrationsFolderPath = "src/main/resources/db/migration"
```
