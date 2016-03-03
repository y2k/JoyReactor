package y2k.joyreactor.common

/**
 * Created by y2k on 08/12/15.
 */
data class PartialResult<T>(val result: T?, val progress: Int, val max: Int) {

    companion object {

        fun <T> complete(data: T): PartialResult<T> {
            return PartialResult(data, 0, 0)
        }

        fun <T> inProgress(progress: Int, max: Int = 100): PartialResult<T> {
            return PartialResult(null, progress, max)
        }
    }
}