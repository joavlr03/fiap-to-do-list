package data

import androidx.room3.Entity
import androidx.room3.PrimaryKey



@Entity(tableName = "tarefas")
data class Tarefa(

        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,

        val titulo: String,

        val descricao: String,

        val concluida: Boolean = false,

        val dataCriacao: Long = System.currentTimeMillis()
)