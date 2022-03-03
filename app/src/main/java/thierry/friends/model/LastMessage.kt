package thierry.friends.model

data class LastMessage(var date: String, var uid: String) {
    constructor() : this("", "")
}