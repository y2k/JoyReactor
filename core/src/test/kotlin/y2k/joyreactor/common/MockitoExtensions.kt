package y2k.joyreactor.common

import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

/**
 * Created by y2k on 5/22/16.
 */

fun <T> when_(instance: T): OngoingStubbing<T> {
    return Mockito.`when`(instance)
}