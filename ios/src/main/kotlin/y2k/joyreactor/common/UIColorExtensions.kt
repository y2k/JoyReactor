package y2k.joyreactor.common

import org.robovm.apple.uikit.UIColor

/**
 * Created by y2k on 3/13/16.
 */
fun uiColor(color: Int): UIColor {
    return UIColor(
        ((color ushr 16) and 0xFF) / 255.0,
        ((color ushr 8) and 0xFF) / 255.0,
        ((color) and 0xFF) / 255.0,
        1.0)
}