package y2k.joyreactor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.viewmodel.ImageViewModel

/**
 * Created by y2k on 4/13/16.
 */
class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val vm = ServiceLocator.resolve<ImageViewModel>()
        bindingBuilder(this) {
            imageView(R.id.image, vm.imageFile)
        }
    }
}