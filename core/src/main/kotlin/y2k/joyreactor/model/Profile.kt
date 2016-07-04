package y2k.joyreactor.model

/**
 * Created by y2k on 9/30/15.
 */
data class Profile(
    val userName: String,
    val userImage: Image,
    val rating: Float,
    val stars: Int,
    val progressToNewStar: Float,
    val subRatings: List<SubRating>,
    val awards: List<Award>) {

    data class SubRating(
        val rating: Float,
        val tag: String)

    data class Award(
        val image: String,
        val title: String)
}