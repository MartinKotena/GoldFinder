package cz.unknown.domain.simpletracker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class MyRecyclerViewAdapter
internal constructor(context: Context, private val mData: ArrayList<MyMarker>) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater
    private var mContext:Context? =null

    private var mClickListener: ItemClickListener? = null

    init {
        mContext=context
        this.mInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.recyclerview_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animal = mData[position]
        Glide.with(mContext).asBitmap().load(animal.path).into(holder.image)
        holder.imageTitle?.setText(animal.title)
        holder.imageDescription?.setText(animal.snippet)
    }

    override fun getItemCount(): Int {
        return mData.size
    }


    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var image:CircleImageView? = null
        internal var imageTitle:TextView? = null
        internal var imageDescription:TextView? = null



        init {
            image = itemView.findViewById(R.id.image_circle)
            imageTitle = itemView.findViewById(R.id.image_title)
            imageDescription = itemView.findViewById(R.id.image_description)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }
    }

    internal fun getItem(id: Int): MyMarker {
        return mData[id]
    }

    internal fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}