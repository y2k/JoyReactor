package y2k.joyreactor.common

/**
 * Created by y2k on 16/07/16.
 */
data class WorkStatus(
    val isFinished: Boolean,
    val error: Throwable? = null) {

    val isInProgress: Boolean
        get() = !isFinished

    val isFinishedWithError: Boolean
        get() = error != null
}