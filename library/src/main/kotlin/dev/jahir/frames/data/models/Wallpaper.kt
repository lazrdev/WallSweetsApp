package dev.jahir.frames.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import dev.jahir.frames.R
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.extensions.resources.toReadableByteCount
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(tableName = "wallpapers")
@Parcelize
data class Wallpaper(
    val name: String,
    @PrimaryKey
    val url: String,
    val author: String? = "",
    @SerializedName(value = "thumbnail", alternate = ["thumbUrl", "thumb", "url-thumb"])
    val thumbnail: String? = "",
    @SerializedName(value = "collections", alternate = ["categories", "category"])
    val collections: String? = "",
    @SerializedName(value = "dimensions", alternate = ["dimension"])
    val dimensions: String? = "",
    val copyright: String? = "",
    val downloadable: Boolean? = true,
    val size: Long? = 0
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    var isInFavorites: Boolean = false

    val detailsCount: Int
        get() = details.size

    val details: ArrayList<Pair<Int, String>>
        get() {
            val list = arrayListOf(Pair(R.string.name, name))
            if (author.hasContent())
                list.add(Pair(R.string.author, author.orEmpty()))
            if (dimensions.hasContent())
                list.add(Pair(R.string.dimensions, dimensions.orEmpty()))
            if ((size ?: 0L) > 0L)
                list.add(Pair(R.string.file_size, (size ?: 0L).toReadableByteCount()))
            if (copyright.hasContent())
                list.add(Pair(R.string.copyright, copyright.orEmpty()))
            return list
        }
}
