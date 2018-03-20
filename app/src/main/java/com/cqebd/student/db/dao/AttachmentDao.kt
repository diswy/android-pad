package com.cqebd.student.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.cqebd.student.db.entity.Attachment
import com.cqebd.student.db.entity.ClassSchedule

/**
 * 描述
 * Created by gorden on 2018/3/20.
 */
@Dao
interface AttachmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttachment(attachment: Attachment)

    @Query("SELECT * from ex_attachment WHERE id = :id")
    fun queryAttachment(id:String):Attachment?
}