package y2k.joyreactor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import y2k.joyreactor.common.ListAdapter
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.find
import y2k.joyreactor.common.inflate
import y2k.joyreactor.presenters.GalleryPresenter

/**
 * Created by y2k on 2/7/16.
 */
class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        setSupportActionBar(find<Toolbar>(R.id.toolbar))

        val list = findViewById(R.id.list) as RecyclerView
        list.layoutManager = GridLayoutManager(this, 3)
        val adapter = ImageAdapter(); list.adapter = adapter

        ServiceLocator.resolve(object : GalleryPresenter.View {

            override fun update(images: List<Image>) = adapter.update(images)
        })
    }

    class ImageAdapter : ListAdapter<Image, ImageAdapter.VH>() {

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.image.setImage(items[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH? {
            return VH(parent.inflate(R.layout.item_image))
        }

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val image = view.find<WebImageView>(R.id.image)
        }
    }
}