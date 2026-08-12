package data
import androidx.room3.Entity


@Entity (tableName = "Tarefas")
data class Tarefa(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val id: Int = 0,
        val titulo: String,
        val descricao: String,
        val concluido: String,
        val dataCriacao: Long = System.currentTimeMillis()
)