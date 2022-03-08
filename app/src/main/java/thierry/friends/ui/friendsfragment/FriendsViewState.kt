package thierry.friends.ui.friendsfragment

import thierry.friends.model.LastMessage
import thierry.friends.model.User

class FriendsViewState(var listOfFriends: List<User>? = null, var currentUser: User? = null, var listOfUnreadMessages: List<LastMessage>? = null)