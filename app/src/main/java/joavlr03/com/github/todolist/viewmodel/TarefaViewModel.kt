package joavlr03.com.github.todolist.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import data.Tarefa
import data.TarefaDatabase
import joavlr03.com.github.todolist.repository.TarefaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TarefaViewModel(private val repository: TarefaRepository) :
    ViewModel() {

        val tarefas: StateFlow<List<Tarefa>> = repository.tarefas
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
        )

    fun inserir(tarefa: Tarefa) = viewModelScope.launch {
        repository.inserir(tarefa) }

    fun atualizar(tarefa: Tarefa) = viewModelScope.launch {
        repository.atualizar(tarefa) }

    fun deletar(tarefa: Tarefa) = viewModelScope.launch {
        repository.deletar(tarefa) }

    companion object {
        fun factory(context: Context) : ViewModelProvider.Factory =
            object : ViewModelProvider.Factory{
                @Suppress("Unchecked_Cast")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {

                    val dao = TarefaDatabase.getDatabase(context).tarefaDao()
                    return TarefaViewModel(TarefaRepository(dao)) as T
                }
            }
    }

}