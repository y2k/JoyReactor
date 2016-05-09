package y2k.joyreactor.common

import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.LifeCycleService
import kotlin.reflect.KClass

/**
 * Created by y2k on 5/9/16.
 */

fun <T> LifeCycleService.registerProperty(msgType: KClass<out BroadcastService.SubscriptionChangeMessage<T>>, property: ObservableProperty<T>) {
    register(msgType) { property.value = it.newValue }
}