package y2k.joyreactor

import android.os.Bundle
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.VMActivity
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.viewmodel.LoginViewModel

class LoginActivity : VMActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vm = ServiceLocator.resolve<LoginViewModel>()
        bindingBuilder(this) {
            visibility(R.id.progress, vm.isBusy)
            command(R.id.login, { vm.login() })
            editText(R.id.username, vm.username)
            editText(R.id.password, vm.password)
        }
    }
}