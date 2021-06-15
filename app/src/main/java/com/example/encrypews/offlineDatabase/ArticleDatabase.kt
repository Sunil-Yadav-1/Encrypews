package com.example.encrypews.offlineDatabase

import android.content.Context
import androidx.room.*
import com.example.encrypews.models.Article
//synchronized block makes sure that everything that happens inside the block can't be accessed by other thread at the same time
// in invoke function we return the instance if it is already created and if not then new instance is created


@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase(){
    abstract fun getArticleDao(): ArticleDao

    companion object{
        @Volatile
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also{ instance = it}
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
    }
}