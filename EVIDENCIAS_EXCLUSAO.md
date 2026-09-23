# Implementação da Confirmação de Exclusão de Tarefas

## Objetivo

Foi implementado um fluxo de confirmação para a exclusão de tarefas no aplicativo **To-Do List**.

Anteriormente, ao tocar no ícone de lixeira, a tarefa era excluída imediatamente. Com a nova implementação, o usuário precisa confirmar a exclusão antes que a tarefa seja removida definitivamente.

A solução utiliza os componentes do **Jetpack Compose Material 3** e mantém a arquitetura **MVVM** já utilizada pelo projeto.

## Funcionamento

O fluxo de exclusão funciona da seguinte maneira:

1. O usuário toca no ícone de lixeira de uma tarefa.
2. A tarefa selecionada é armazenada temporariamente na variável `tarefaParaExcluir`.
3. Um `AlertDialog` é exibido sobre a tela da lista.
4. O diálogo apresenta o título da tarefa que será excluída.
5. O usuário pode escolher entre:
   - **Cancelar:** fecha o diálogo e mantém a tarefa na lista.
   - **Excluir:** chama o método `deletar()` do `ViewModel` e remove a tarefa.
6. Após a confirmação ou cancelamento, o estado `tarefaParaExcluir` é definido como `null`, fazendo com que o diálogo seja fechado.

## Controle da tarefa selecionada

Para controlar qual tarefa está aguardando confirmação, foi utilizado um estado do Compose:

```kotlin
var tarefaParaExcluir by remember {
    mutableStateOf<Tarefa?>(null)
}
```

## Telas

1. Item 
<img width="322" height="621" alt="image" src="https://github.com/user-attachments/assets/36ca028e-b954-4ac3-8db6-095d537bfa69" />



2. Lista antes da exclusao
   <img width="325" height="690" alt="image" src="https://github.com/user-attachments/assets/7faf49ef-6051-44cc-a67a-c03badfec546" />



3. Dialogo de exclusao
   <img width="321" height="694" alt="image" src="https://github.com/user-attachments/assets/3a20285c-42e2-4eb0-a65a-8dd650d04339" />

4. Resultado ao cancelar
   <img width="330" height="687" alt="image" src="https://github.com/user-attachments/assets/86de070f-0f07-41df-bd67-128059ba6033" />

5. Resultado ao excluir
   <img width="325" height="694" alt="image" src="https://github.com/user-attachments/assets/c5701b40-d19c-4d49-874e-dd8af1174f22" />


