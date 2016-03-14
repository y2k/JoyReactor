package y2k.joyreactor.widget

import org.robovm.apple.foundation.NSCoder
import org.robovm.apple.uikit.UIView
import org.robovm.objc.annotation.CustomClass
import y2k.joyreactor.common.binding

/**
 * Created by y2k on 3/13/16.
 */
@CustomClass("Snackbar")
class Snackbar(aDecoder: NSCoder) : UIView(aDecoder) {

    val visible = binding(false)

    init {
        isHidden = true
        visible.subscribe {
            if (it) {
                isHidden = false
                alpha = 0.0
                UIView.animate(0.3, {
                    alpha = 1.0
                })
            } else {
                UIView.animate(0.3, {
                    alpha = 0.0
                }, {
                    isHidden = true
                })
            }
        }
    }
}