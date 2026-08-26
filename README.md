# FIAP To-Do List

Aplicativo Android desenvolvido em Kotlin para gerenciamento de tarefas, utilizando Jetpack Compose para a interface, Room para persistência local dos dados, Coroutines/Flow para operações assíncronas e reativas, ViewModel para controle de estado e Navigation Compose para navegação entre telas.

## Descrição do projeto

O projeto consiste em uma aplicação simples de lista de tarefas, na qual o usuário pode cadastrar, visualizar, editar, concluir e excluir tarefas.

Cada tarefa possui um título, uma descrição, um status de conclusão e uma data de criação. Os dados são armazenados localmente em um banco SQLite gerenciado pelo Room, permitindo que as tarefas permaneçam salvas mesmo após o fechamento do aplicativo.

## Objetivo da aplicação

O objetivo da aplicação é demonstrar, de forma prática, a construção de um aplicativo Android moderno com arquitetura organizada em camadas, separando responsabilidades entre banco de dados, repositório, ViewModel, telas de interface e navegação.

A aplicação também tem como finalidade exercitar conceitos importantes do desenvolvimento Android atual, como estado reativo, persistência local, composição de telas e navegação com passagem de parâmetros.

## Tecnologias utilizadas

- **Kotlin**: linguagem principal utilizada no desenvolvimento do aplicativo.
- **Jetpack Compose**: framework declarativo utilizado para construir as interfaces da aplicação.
- **Room**: biblioteca utilizada para persistência local dos dados em banco SQLite.
- **Coroutines/Flow**: utilizadas para executar operações assíncronas e observar alterações na lista de tarefas de forma reativa.
- **ViewModel**: responsável por manter e gerenciar o estado da tela, preservando dados durante mudanças de configuração.
- **Navigation Compose**: utilizada para controlar a navegação entre a tela de listagem e a tela de formulário.
- **Material 3**: utilizado nos componentes visuais, como `Scaffold`, `TopAppBar`, `Button`, `Card`, `Checkbox` e `FloatingActionButton`.

## Estrutura principal do projeto

```text
app/src/main/java
├── data
│   ├── Tarefa.kt
│   ├── TarefaDao.kt
│   └── TarefaDatabase.kt
└── joavlr03/com/github/todolist
    ├── MainActivity.kt
    ├── navigation
    │   └── AppNavigation.kt
    ├── repository
    │   └── TarefaRepository.kt
    ├── user
    │   ├── ListaTarefasScreen.kt
    │   └── FormularioTarefaScreen.kt
    └── viewmodel
        └── TarefaViewModel.kt
```

## Modelo de dados e persistência

A entidade principal do projeto é a classe `Tarefa`, localizada no pacote `data`.

```kotlin
data class Tarefa(
    val id: Int = 0,
    val titulo: String,
    val descricao: String,
    val concluida: Boolean = false,
    val dataCriacao: Long = System.currentTimeMillis()
)
```

Ela é anotada com `@Entity(tableName = "tarefas")`, indicando que será representada como uma tabela no banco de dados local.

O `TarefaDao` define as operações de acesso ao banco:

- listar todas as tarefas;
- inserir uma nova tarefa;
- atualizar uma tarefa existente;
- deletar uma tarefa.

A listagem retorna um `Flow<List<Tarefa>>`, permitindo que a interface seja atualizada automaticamente sempre que houver alteração nos dados.

O `TarefaDatabase` é responsável por configurar o banco local Room, criar a instância do banco e fornecer acesso ao `TarefaDao`.

## Responsabilidade de `TarefaRepository`

A classe `TarefaRepository` atua como uma camada intermediária entre o `TarefaDao` e a `TarefaViewModel`.

Sua principal responsabilidade é centralizar o acesso aos dados da aplicação, evitando que a ViewModel se comunique diretamente com o DAO.

No projeto, o repositório expõe a lista de tarefas por meio da propriedade:

```kotlin
val tarefas: Flow<List<Tarefa>> = dao.listarTodas()
```

Além disso, ele encapsula as operações de escrita no banco:

```kotlin
suspend fun inserir(tarefa: Tarefa) = dao.inserir(tarefa)
suspend fun atualizar(tarefa: Tarefa) = dao.atualizar(tarefa)
suspend fun deletar(tarefa: Tarefa) = dao.deletar(tarefa)
```

Dessa forma, o `TarefaRepository` organiza a camada de dados e facilita futuras mudanças, como trocar a origem dos dados, adicionar validações ou integrar uma API externa.

## Responsabilidade de `TarefaViewModel`

A `TarefaViewModel` é responsável por gerenciar o estado da aplicação e intermediar a comunicação entre as telas e o repositório.

Ela recebe uma instância de `TarefaRepository` e transforma o `Flow<List<Tarefa>>` do repositório em um `StateFlow<List<Tarefa>>`:

```kotlin
val tarefas: StateFlow<List<Tarefa>> = repository.tarefas
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
```

Com isso, a lista de tarefas pode ser observada pelas telas Compose de forma segura e reativa.

A ViewModel também fornece métodos para inserir, atualizar e deletar tarefas:

```kotlin
fun inserir(tarefa: Tarefa) = viewModelScope.launch {
    repository.inserir(tarefa)
}

fun atualizar(tarefa: Tarefa) = viewModelScope.launch {
    repository.atualizar(tarefa)
}

fun deletar(tarefa: Tarefa) = viewModelScope.launch {
    repository.deletar(tarefa)
}
```

Essas operações são executadas dentro do `viewModelScope`, garantindo o uso correto de corrotinas e evitando bloquear a interface do usuário.

A classe também possui uma `factory`, responsável por criar a ViewModel com suas dependências: banco de dados, DAO e repositório.

## Como `ListaTarefasScreen` observa o estado e dispara ações

A tela `ListaTarefasScreen` é responsável por exibir a lista de tarefas cadastradas.

Ela observa o estado da ViewModel por meio de:

```kotlin
val tarefas by viewModel.tarefas.collectAsStateWithLifecycle()
```

Esse código coleta o `StateFlow` de tarefas respeitando o ciclo de vida da tela. Assim, sempre que a lista de tarefas muda no banco de dados, a interface é recomposta automaticamente.

Depois de observar o estado, a tela chama o componente `ListaTarefasContent`, passando a lista e as ações que podem ser executadas pelo usuário:

- criar uma nova tarefa;
- editar uma tarefa existente;
- marcar ou desmarcar uma tarefa como concluída;
- deletar uma tarefa.

Quando o usuário marca uma tarefa como concluída, a tela chama:

```kotlin
viewModel.atualizar(tarefa.copy(concluida = concluida))
```

Quando o usuário exclui uma tarefa, a tela chama:

```kotlin
viewModel.deletar(tarefa)
```

O botão flutuante (`FloatingActionButton`) dispara a navegação para o formulário de nova tarefa, enquanto o clique em um card de tarefa dispara a navegação para edição.

## Como `FormularioTarefaScreen` diferencia cadastro e edição

A tela `FormularioTarefaScreen` é utilizada tanto para cadastrar novas tarefas quanto para editar tarefas existentes.

A diferenciação acontece por meio do parâmetro `tarefaId`.

Quando `tarefaId` é igual a `0`, a tela entende que se trata de um novo cadastro:

```kotlin
if (tarefaId == 0) {
    viewModel.inserir(Tarefa(titulo = titulo, descricao = descricao))
}
```

Quando `tarefaId` é diferente de `0`, a tela busca a tarefa existente na lista:

```kotlin
val tarefaExistente = remember(tarefas, tarefaId) {
    tarefas.find { it.id == tarefaId }
}
```

Nesse caso, os campos do formulário são preenchidos com o título e a descrição da tarefa encontrada. Ao salvar, a tarefa é atualizada mantendo os demais dados:

```kotlin
tarefaExistente?.let {
    viewModel.atualizar(it.copy(titulo = titulo, descricao = descricao))
}
```

Além disso, o título da tela muda conforme o modo de uso:

```kotlin
Text(if (isEdicao) "Editar Tarefa" else "Nova Tarefa")
```

Portanto, a mesma tela consegue atender aos dois fluxos: criação e edição.

## Rotas configuradas em `AppNavigation` e passagem do ID da tarefa

A navegação do aplicativo é configurada na função `AppNavigation`, que cria um `NavController` e define um `NavHost`.

A rota inicial da aplicação é:

```kotlin
startDestination = "lista"
```

### Rota `lista`

A rota `lista` exibe a `ListaTarefasScreen`.

Nessa tela, existem dois fluxos de navegação:

```kotlin
onNovaTarefa = { navController.navigate("formulario/0") }
onEditarTarefa = { id -> navController.navigate("formulario/$id") }
```

Para cadastrar uma nova tarefa, o aplicativo navega para `formulario/0`.

Para editar uma tarefa, o aplicativo navega para `formulario/{id}`, passando o ID real da tarefa selecionada.

### Rota `formulario/{tarefaId}`

A rota `formulario/{tarefaId}` recebe o ID da tarefa como argumento:

```kotlin
composable("formulario/{tarefaId}") { backStackEntry ->
    val tarefaId = backStackEntry.arguments?.getString("tarefaId")?.toInt() ?: 0
}
```

Esse valor é convertido para `Int` e enviado para a `FormularioTarefaScreen`.

Se o ID recebido for `0`, o formulário funciona em modo de cadastro. Se for diferente de `0`, funciona em modo de edição.

Ao finalizar o cadastro ou edição, a tela chama:

```kotlin
navController.popBackStack()
```

Assim, o aplicativo retorna para a tela anterior.

## Como a `MainActivity` cria a ViewModel e inicia a navegação

A `MainActivity` é o ponto de entrada do aplicativo.

No método `onCreate`, ela habilita o modo edge-to-edge e define a interface usando `setContent`:

```kotlin
setContent {
    FiaptodolistTheme {
        val viewModel: TarefaViewModel = viewModel(
            factory = TarefaViewModel.factory(applicationContext)
        )
        AppNavigation(viewModel = viewModel)
    }
}
```

Dentro do tema `FiaptodolistTheme`, a Activity cria a `TarefaViewModel` utilizando a factory definida na própria ViewModel.

Essa factory recebe o `applicationContext`, acessa o banco `TarefaDatabase`, obtém o `TarefaDao`, cria o `TarefaRepository` e finalmente instancia a `TarefaViewModel`.

Depois disso, a `MainActivity` chama `AppNavigation(viewModel = viewModel)`, iniciando a navegação da aplicação e compartilhando a mesma ViewModel entre as telas.

## Fluxo geral da aplicação

```text
MainActivity
    ↓
TarefaViewModel
    ↓
TarefaRepository
    ↓
TarefaDao
    ↓
TarefaDatabase / Room / SQLite
```

Na interface, o fluxo principal é:

```text
ListaTarefasScreen
    ├── Nova tarefa → formulario/0
    ├── Editar tarefa → formulario/{id}
    ├── Concluir tarefa → atualizar status
    └── Deletar tarefa → remover do banco
```

## Instruções básicas para executar o projeto

### Pré-requisitos

Antes de executar o projeto, é necessário ter instalado:

- Android Studio;
- JDK compatível com o projeto;
- SDK Android configurado;
- Emulador Android ou dispositivo físico conectado.

### Passos para execução

1. Clone ou baixe este repositório.

```bash
git clone <url-do-repositorio>
```

2. Abra o projeto no Android Studio.

3. Aguarde a sincronização do Gradle.

4. Verifique se o emulador Android ou dispositivo físico está disponível.

5. Execute o projeto clicando em **Run** ou usando o atalho do Android Studio.

6. Ao abrir o aplicativo, será exibida a tela de lista de tarefas.

### Como usar

- Clique no botão `+` para cadastrar uma nova tarefa.
- Preencha o título e, se desejar, uma descrição.
- Clique em **Salvar** para registrar a tarefa.
- Toque em uma tarefa da lista para editá-la.
- Marque o checkbox para concluir uma tarefa.
- Clique no ícone de lixeira para excluir uma tarefa.

## Evidências da atividade

Abaixo devem ser adicionadas as imagens produzidas durante a execução da atividade, como prints do aplicativo rodando, tela de listagem, tela de cadastro, tela de edição e banco de dados funcionando.

> Observação: no arquivo analisado, não foram encontradas imagens de evidência além dos ícones padrão do aplicativo. Caso as imagens estejam em outra pasta ou ainda precisem ser inseridas, recomenda-se criar uma pasta chamada `docs/evidencias` e adicionar os prints nela.

### Tela inicial / lista de tarefas
<img width="292" height="576" alt="Screenshot 2026-08-25 205506" src="https://github.com/user-attachments/assets/359ed029-fc79-4d5f-826b-b43cf5969395" />

<img width="286" height="585" alt="Screenshot 2026-08-25 205520" src="https://github.com/user-attachments/assets/8d3c2af7-198c-4549-8945-493eab540c8f" />

<img width="293" height="584" alt="Screenshot 2026-08-25 205527" src="https://github.com/user-attachments/assets/d33da3af-af92-4f7a-aa3b-b25d1504d531" />

## Conclusão

Este projeto demonstra a criação de um aplicativo Android de tarefas com uma arquitetura simples e organizada, utilizando recursos modernos do ecossistema Android.

A separação entre `DAO`, `Repository`, `ViewModel`, telas Compose e navegação facilita a manutenção do código, melhora a organização da aplicação e permite evoluções futuras, como filtros, categorias, sincronização com API externa ou autenticação de usuários.
