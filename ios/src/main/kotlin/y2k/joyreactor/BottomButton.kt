package y2k.joyreactor

import org.robovm.apple.uikit.UIView

/**
 * Created by y2k on 11/1/15.
 */
internal class BottomButton(private val view: UIView) {

    fun setHidden(hidden: Boolean) {
        if (hidden) {
            UIView.animate(0.3, {
                val f = view.frame
                f.setY(view.superview.frame.height)
                view.frame = f
                view.alpha = 0.0
            }, { view.isHidden = true })
        } else {
            view.isHidden = false
            UIView.animate(0.3) {
                val f = view.frame
                f.setY(view.superview.frame.height - f.height)
                view.frame = f
                view.alpha = 1.0
            }
        }
    }
}