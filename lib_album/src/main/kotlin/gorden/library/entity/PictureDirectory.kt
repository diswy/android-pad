package gorden.library.entity

/**
 * 照片目录
 */
data class PictureDirectory(var dirName: String,
                            val dirPath: String,
                            val coverPicture: Picture,
                            val pictures: MutableList<Picture>,
                            var showCamera:Boolean = false) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PictureDirectory

        if (dirName != other.dirName) return false
        if (dirPath != other.dirPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dirName.hashCode()
        result = 31 * result + dirPath.hashCode()
        return result
    }

}