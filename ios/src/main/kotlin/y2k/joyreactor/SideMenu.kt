package y2k.joyreactor

import org.robovm.apple.uikit.*

/**
 * Created by y2k on 9/26/15.
 */
class SideMenu(private val parent: UIViewController, menuStoryboardId: String) {
    private val parentView: UIView

    private val closeButton: UIButton
    private val menuView: UIView

    private val menuController: UIViewController

    init {
        parentView = parent.navigationController.view
        menuController = parent.storyboard.instantiateViewController(menuStoryboardId)
        menuView = menuController.view

        closeButton = UIButton(parentView.frame)
        closeButton.addOnTouchUpInsideListener { sender, e -> closeButtonClicked() }
    }

    internal fun closeButtonClicked() {
        UIView.animate(0.3, {
            this.restoreViewPosition()
        }, {
            removeMenuViews()
            menuController.viewDidDisappear(false)
        })
    }

    fun attach() {
        val menuButton = UIBarButtonItem()
        menuButton.image = UIImage.getImage("MenuIcon.png")
        menuButton.setOnClickListener { sender -> menuButtonClicked() }
        parent.navigationItem.leftBarButtonItem = menuButton

        val edgeGesture = UIScreenEdgePanGestureRecognizer { s -> menuButtonClicked() }
        edgeGesture.edges = UIRectEdge.Left
        parent.view.addGestureRecognizer(edgeGesture)
    }

    internal fun menuButtonClicked() {
        if (menuView.superview != null)
            return

        val menuFrame = parentView.frame
        menuFrame.size.setWidth(PanelWidth.toDouble())
        menuView.frame = menuFrame
        parentView.addSubview(menuView)
        parentView.sendSubviewToBack(menuView)
        menuFrame.origin.setX((-PanelWidth).toDouble())

        parentView.addSubview(closeButton)

        UIView.animate(0.3, {
            menuView.frame = menuFrame
            for (s in parentView.subviews) {
                val f = s.frame
                s.frame = f.offset(PanelWidth.toDouble(), 0.0)
            }
        }) { s -> menuController.viewWillAppear(false) }
    }

    fun deactive() {
        if (closeButton.superview == null)
            return
        restoreViewPosition()
        removeMenuViews()
    }

    internal fun restoreViewPosition() {
        for (s in parentView.subviews) {
            if (s === menuView)
                continue
            val f = s.frame
            s.frame = f.offset((-PanelWidth).toDouble(), 0.0)
        }
    }

    internal fun removeMenuViews() {
        closeButton.removeFromSuperview()
        menuView.removeFromSuperview()
    }

    companion object {

        internal val PanelWidth = 270f
    }
}