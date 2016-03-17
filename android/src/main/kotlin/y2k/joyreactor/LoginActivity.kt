package y2k.joyreactor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val vm = ServiceLocator.resolve<LoginViewModel>()
        bindingBuilder(this) {
            visibility(R.id.progress, vm.isBusy)
            command(R.id.login, { vm.login() })
            editText(R.id.username, vm.username)
            editText(R.id.password, vm.password)
        }
    }
}