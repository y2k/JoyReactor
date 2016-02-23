package y2k.joyreactor.common

import android.text.Editable
import android.text.TextWatcher

/**
 * Created by y2k on 2/23/16.
 */
abstract class TextWatcherAdapter : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}