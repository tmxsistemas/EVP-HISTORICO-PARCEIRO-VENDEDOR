# Histórico de Alterações de Vendedor em Parceiro


# Objetivo
Este código implementa um evento programável no sistema Sankhya para registrar automaticamente um histórico sempre que o vendedor de um parceiro for alterado. A informação é salva na tabela AD_HISTPARC, mantendo rastreabilidade de:

- Vendedor anterior

- Vendedor atual

- Datas da alteração

- Usuário responsável pela modificação

 # Estrutura Geral

 ## Classe principal



```
ControllerHistoricoVendendor implements EventoProgramavelJava

```
- Essa classe escuta os eventos da entidade de parceiro (Parceiro) e atua principalmente nos métodos:

- beforeUpdate: coleta os dados antes da alteração.

- afterUpdate: coleta os dados depois da alteração e grava o histórico via método lancarHistoricoParceiro2.


##  Fluxo da Lógica
1. beforeUpdate(PersistenceEvent event)
- Captura os dados anteriores à alteração:

- Código e nome do vendedor atual antes da modificação

- Data de alteração anterior (DTALTER)

2. afterUpdate(PersistenceEvent event)
Obtém:

- Novo vendedor (CODVEND)

- Nova data de alteração (DTALTER)

- Código do parceiro (CODPARC)

- ID do usuário logado

- Chama o método lancarHistoricoParceiro2 para persistir essas informações na tabela AD_HISTPARC.


## Método de Persistência
```
public void lancarHistoricoParceiro2(...)


```

## Função:
Insere um novo registro de histórico na tabela AD_HISTPARC.

🔢 Geração de código (CODHIST):
Tenta obter via sequência: SEQ_AD_HISTPARC.NEXTVAL

Se falhar (sequência não existe), usa fallback com:
```
SELECT NVL(MAX(CODHIST), 0) + 1 FROM AD_HISTPARC
```
 ## SQL Executado:

 ```
INSERT INTO AD_HISTPARC 
(CODHIST, CODPARC, CODVEND, CODVENDEP, DTALTER, DTALTERDEP, CODUSU) 
VALUES (?, ?, ?, ?, ?, ?, ?)

```
## Segurança
O ID do usuário logado (usuarioLogadoID) é capturado usando:

 ```
ServiceContext.getCurrent().getAutentication()

```
## Exemplo de Registro Gerado
| Campo        | Valor                                      |
| ------------ | ------------------------------------------ |
| `CODHIST`    | Próximo valor da sequência ou MAX + 1      |
| `CODPARC`    | Código do parceiro alterado                |
| `CODVEND`    | Código do vendedor antes da alteração      |
| `CODVENDEP`  | Código do novo vendedor                    |
| `DTALTER`    | Data da última modificação anterior        |
| `DTALTERDEP` | Data da modificação atual                  |
| `CODUSU`     | Código do usuário que realizou a alteração |

## Observações
- Certifique-se de que a tabela AD_HISTPARC existe e possui os campos esperados.

- Para melhor performance e integridade, prefira usar a sequência (SEQ_AD_HISTPARC) no banco de dados.

- O código usa JapeWrapper para acessar dados do Sankhya de forma segura.
