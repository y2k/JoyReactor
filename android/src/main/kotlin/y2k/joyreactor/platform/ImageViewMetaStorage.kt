package y2k.joyreactor.platform

import android.widget.ImageView
import y2k.joyreactor.R
import y2k.joyreactor.services.ImageService
import java.util.*

/**
 * Created by y2k on 05/07/16.
 */
class ImageViewMetaStorage : ImageService.MetaStorage {

    override fun setKey(target: Any, key: UUID?) {
        if (target !is ImageView) throw IllegalArgumentException()
        target.setTag(R.id.imageViewId, key)
    }

    override fun getKey(target: Any): UUID {
        if (target !is ImageView) throw IllegalArgumentException()
        return target.getTag(R.id.imageViewId) as UUID
    }
}