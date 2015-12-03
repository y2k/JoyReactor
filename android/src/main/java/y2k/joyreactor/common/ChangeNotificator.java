package y2k.joyreactor.common;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by y2k on 03/12/15.
 */
public class ChangeNotificator {

    private RecyclerView.Adapter adapter;

    public ChangeNotificator(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public <T extends Comparable<T>> void update(List<T> oldItems, List<T> newItems) {
        for (int i = 0; i < newItems.size(); i++) {
            if (i >= oldItems.size()) {
                adapter.notifyItemInserted(i);
            } else {
                T s = newItems.get(i);

                int n;
                for (n = 0; n < oldItems.size(); n++) {
                    if (s.compareTo(oldItems.get(n)) == 0)
                        break;
                }

                if (n < oldItems.size()) {
                    if (n != i)
                        adapter.notifyItemMoved(n, i);
                } else {
                    adapter.notifyItemChanged(n);
                }
            }
        }
    }
}