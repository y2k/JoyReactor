package y2k.joyreactor.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams
import android.widget.GridLayout
import android.widget.Toast
import y2k.joyreactor.common.BindableComponent
import y2k.joyreactor.common.listProperty
import y2k.joyreactor.common.px
import y2k.joyreactor.common.replaceViews
import y2k.joyreactor.model.Image
import y2k.joyreactor.model.Profile

/**
 * Created by y2k on 09/07/16.
 */
class AwardsListComponent(
    context: Context?, attrs: AttributeSet?) : GridLayout(context, attrs), BindableComponent<List<Profile.Award>> {

    override val value = listProperty<Profile.Award> { onNewAwards(it) }

    init {
        columnCount = 6
    }

    private fun onNewAwards(awards: List<Profile.Award>) {
        awards
            .take(columnCount * 4)
            .map { award ->
                WebImageView(context).apply {
                    image = Image(award.image)
                    setPadding(2.px(), 2.px(), 2.px(), 2.px())
                    layoutParams = LayoutParams(48.px(), 48.px())
                    setOnClickListener {
                        Toast.makeText(context, award.title, Toast.LENGTH_LONG).show()
                    }
                }
            }
            .let { replaceViews(it) }
    }
}