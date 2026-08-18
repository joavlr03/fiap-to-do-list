package data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver

@Database(
    entities = [Tarefa::class],
    version = 1,
    exportSchema = false
)
abstract class TarefaDatabase : RoomDatabase() {

    abstract fun tarefaDao(): TarefaDao

    companion object {

        @Volatile
        private var INSTANCE: TarefaDatabase? = null

        fun getDatabase(context: Context): TarefaDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance =
                    Room.databaseBuilder<TarefaDatabase>(
                        context.applicationContext,
                        "tarefas.db"
                    )
                        .setDriver(AndroidSQLiteDriver())
                        .build()

                INSTANCE = instance
                instance
            }
        }
    }
}