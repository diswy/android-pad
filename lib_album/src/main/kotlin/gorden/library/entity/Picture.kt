package gorden.library.entity

/**
 * 照片实体
 */
data class Picture(val id:String,
                   val name:String,
                   val path:String,
                   val size:Long,
                   val mineType:String,
                   val addTime:Long) {

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Picture

        if (id != other.id) return false

        return true
    }

}