package thierry.friends.model

import java.util.*
import kotlin.collections.ArrayList

data class Message(
    val from: String,
    val to: String,
    val message: String,
    val date: Date,
    val between: List<String>,
    val urlPicFrom: String
) {
    constructor() : this("", "", "", Date(), ArrayList<String>(), "")

}