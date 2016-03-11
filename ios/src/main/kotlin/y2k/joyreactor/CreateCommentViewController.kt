package y2k.joyreactor

import org.robovm.apple.uikit.UIButton
import org.robovm.apple.uikit.UITextView
import org.robovm.apple.uikit.UIViewController
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.viewmodel.CreateCommentViewModel

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

        val vm = ServiceLocator.resolve<CreateCommentViewModel>()
        bindingBuilder {
            command(sendButton) { vm.create() }
            command(cancelButton) { navigationController.popViewController(true) }
            action(vm.isBusy) { navigationItem.setHidesBackButton(it, true) }
            textView(commentTextView, vm.commentText)
        }
    }
}