package com.dozmaden.rookievpn.ui.applications

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dozmaden.rookievpn.R.id
import com.dozmaden.rookievpn.R.layout
import com.dozmaden.rookievpn.model.ApplicationInfo

class ApplicationsAdapter :
    ListAdapter<ApplicationInfo, ApplicationsAdapter.ViewHolder>(DiffCallback) {

    init {
        setHasStableIds(true)
    }

    private var onAppClickListener: ((ApplicationInfo) -> Unit)? = null

    fun setOnAppClickListener(listener: (ApplicationInfo) -> Unit) {
        onAppClickListener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(layout.item_application, viewGroup, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.unbind()
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).packageName.hashCode().toLong()
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val appName: TextView = view.findViewById(id.app_name)
        private val appIcon: ImageView = view.findViewById(id.app_image)
        private val card: CardView = view.findViewById(id.card)

        @SuppressLint("SetTextI18n")
        fun bind(data: ApplicationInfo) {
            appName.text = data.appLabel
            Glide.with(view)
                .load(data.appIcon)
                .into(appIcon)
            card.setOnClickListener { onAppClickListener?.invoke(data) }
        }

        fun unbind() {
            card.setOnClickListener(null)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ApplicationInfo>() {
        override fun areItemsTheSame(oldItem: ApplicationInfo, newItem: ApplicationInfo): Boolean =
            oldItem.packageName == newItem.packageName

        override fun areContentsTheSame(
            oldItem: ApplicationInfo,
            newItem: ApplicationInfo
        ): Boolean =
            oldItem == newItem
    }
}