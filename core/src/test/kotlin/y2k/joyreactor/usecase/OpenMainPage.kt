package y2k.joyreactor.usecase

import org.junit.Test
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.viewmodel.MainViewModel

/**
 * Created by y2k on 25/07/16.
 */
class OpenMainPage {

    @Test
    fun test() {
        val vm = ServiceLocator.resolve<MainViewModel>()
    }
}