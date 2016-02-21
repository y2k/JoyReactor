package y2k.joyreactor

import org.robovm.apple.uikit.*
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.presenters.AddTagPresenter

/**
 * Created by y2k on 08/10/15.
 */
@CustomClass("AddTagViewController")
class AddTagViewController : UIViewController(), AddTagPresenter.View {

    @IBOutlet lateinit var tagNameView: UITextField
    @IBOutlet lateinit var activityView: UIActivityIndicatorView

    override fun viewDidLoad() {
        super.viewDidLoad()
        val presenter = ServiceLocator.resolve(this)

        navigationItem.rightBarButtonItem.setOnClickListener { presenter.add(tagNameView.text) }

        tagNameView.delegate = object : UITextFieldDelegateAdapter() {

            override fun shouldReturn(textField: UITextField?): Boolean {
                presenter.add(tagNameView.text)
                return false
            }
        }

        tagNameView.becomeFirstResponder()
        activityView.stopAnimating()
    }

    override fun setIsBusy(isBusy: Boolean) {
        navigationItem.rightBarButtonItem.isEnabled = !isBusy
        navigationItem.setHidesBackButton(isBusy, true)
        if (isBusy) activityView.startAnimating()
        else activityView.stopAnimating()
    }

    override fun showErrorMessage() {
        // TODO:
    }

    override fun getPreferredStatusBarStyle(): UIStatusBarStyle {
        return UIStatusBarStyle.LightContent
    }
}