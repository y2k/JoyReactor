package y2k.joyreactor

import org.robovm.apple.uikit.UIButton
import org.robovm.apple.uikit.UITextView
import org.robovm.apple.uikit.UIViewController
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.model.Profile
import y2k.joyreactor.presenters.CreateCommentPresenter

/**
 * Created by y2k on 10/4/15.
 */
@CustomClass("CreateCommentViewController")
class CreateCommentViewController : UIViewController() {

    @IBOutlet lateinit var commentTextView: UITextView
    @IBOutlet lateinit var sendButton: UIButton
    @IBOutlet lateinit var cancelButton: UIButton

    override fun viewDidLoad() {
        super.viewDidLoad()

        val presenter = ServiceLocator.resolve(
            object : CreateCommentPresenter.View {

                override fun setIsBusy(isBusy: Boolean) {
                    navigationItem.setHidesBackButton(isBusy, true)
                }

                override fun setUser(profile: Profile) {
                    // TODO
                }
            }
        )

        cancelButton.addOnTouchUpInsideListener { sender, e -> navigationController.popViewController(true) }
        sendButton.addOnTouchUpInsideListener { sender, e -> presenter.create(commentTextView.text) }
    }
}