package com.cqebd.student.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.cqebd.student.app.App
import com.cqebd.student.db.dao.AttachmentDao
import com.cqebd.student.db.dao.CourseDao
import com.cqebd.student.db.entity.Attachment
import com.cqebd.student.db.entity.ClassSchedule
import com.cqebd.student.db.type.Converters

/**
 * 描述
 * Created by gorden on 2018/3/12.
 */
@Database(entities = [ClassSchedule::class,Attachment::class],version = 1)
@TypeConverters(Converters::class)
abstract class ExDataBase: RoomDatabase() {
    abstract fun courseDao():CourseDao
    abstract fun attachmentDao():AttachmentDao

    companion object {
        @Volatile private var INSTANCE: ExDataBase? = null

        fun getInstance():ExDataBase{
            return INSTANCE?: synchronized(this){
                INSTANCE?: buildDatabase().also {
                    INSTANCE = it
                }
            }
        }

        private fun buildDatabase():ExDataBase =
                Room.databaseBuilder(App.mContext, ExDataBase::class.java, "db_student")
                        .fallbackToDestructiveMigration()
                        .addMigrations()
                        .build()
    }
}