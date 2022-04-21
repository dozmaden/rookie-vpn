package com.dozmaden.rookievpn.ui.servers


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
import com.dozmaden.rookievpn.R
import com.dozmaden.rookievpn.model.VpnServer

class ServersAdapter : ListAdapter<VpnServer, ServersAdapter.ViewHolder>(DiffCallback) {

    init {
        setHasStableIds(true)
    }

    private var onServerClickListener: ((VpnServer) -> Unit)? = null

    fun setOnServerClickListener(listener: (VpnServer) -> Unit) {
        onServerClickListener = listener
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ServersAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_server, viewGroup, false)
        )
    }

    override fun onBindViewHolder(holder: ServersAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewDetachedFromWindow(holder: ServersAdapter.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.unbind()
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).filename.hashCode().toLong()
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val serverCountry: TextView = view.findViewById(R.id.server_country)
        private val serverFilename: TextView = view.findViewById(R.id.server_filename)
        private val serverImage: ImageView = view.findViewById(R.id.server_image)
        private val card: CardView = view.findViewById(R.id.server_card)

        fun bind(data: VpnServer) {
            serverCountry.text = data.country
            serverFilename.text = data.filename
            Glide.with(view)
                .load(data.flagUrl)
                .into(serverImage)
            card.setOnClickListener { onServerClickListener?.invoke(data) }
        }

        fun unbind() {
            card.setOnClickListener(null)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<VpnServer>() {
        override fun areItemsTheSame(
            oldItem: VpnServer,
            newItem: VpnServer
        ): Boolean =
            oldItem.filename == newItem.filename

        override fun areContentsTheSame(
            oldItem: VpnServer,
            newItem: VpnServer
        ): Boolean =
            oldItem == newItem
    }
}