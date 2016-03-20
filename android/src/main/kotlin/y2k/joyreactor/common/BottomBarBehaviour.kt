package y2k.joyreactor.common

import android.content.Context
import android.os.Handler
import android.support.design.widget.CoordinatorLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup

/**
 * Created by y2k on 3/20/16.
 */
class BottomBarBehaviour(context: Context?, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<ViewGroup>(context, attrs) {

    private val handler = Handler()

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout?, child: ViewGroup, target: View?, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        updateVisible(child, target)
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout?, child: ViewGroup, target: View?) {
        handler.post { updateVisible(child, target) }
    }

    private fun updateVisible(child: ViewGroup, target: View?) {
        val l = getList(target)
        if (l.getChildAdapterPosition(l.getChildAt(0)) != 0) {
            changeVisible(child, View.GONE)
        } else {
            val top = l.layoutManager.getDecoratedTop(l.getChildAt(0))
            Log.i("BottomBarBehaviour", "top = $top")
            changeVisible(child, if (top < l.paddingTop) View.GONE else View.VISIBLE)
        }
    }

    private fun changeVisible(child: ViewGroup, state: Int) {
        if (child.visibility == state) return
        if (state == View.VISIBLE) {
            child.translationY = child.height.toFloat()
            child.compatAnimate()
                .translationY(0f)
                .withEndAction { child.visibility = state }
        } else {
            child.compatAnimate()
                .translationY(child.height.toFloat())
                .withEndAction { child.visibility = state }
        }
    }

    private fun getList(target: View?): RecyclerView {
        return when (target) {
            is RecyclerView -> target
            is SwipeRefreshLayout -> target.getChildAt(0) as RecyclerView
            else -> throw Exception()
        }
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout?, child: ViewGroup, directTargetChild: View?, target: View?, nestedScrollAxes: Int): Boolean {
        return true
    }
}