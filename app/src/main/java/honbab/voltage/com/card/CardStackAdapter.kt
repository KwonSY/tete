package honbab.voltage.com.card;

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import honbab.voltage.com.data.RestData
import honbab.voltage.com.tete.OneRestaurantActivity
import honbab.voltage.com.tete.R

class CardStackAdapter(
        private var data: List<RestData> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_spot, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = data[position]
        holder.name.text = "${data.rest_id}. ${data.rest_name}"
        holder.city.text = data.vicinity
        Glide.with(holder.image)
                .load(data.rest_img)
                .into(holder.image)
        holder.itemView.setOnClickListener { v ->
//            Toast.makeText(v.context, data.rest_name, Toast.LENGTH_SHORT).show()
            val intent = Intent(v.context, OneRestaurantActivity::class.java)
            intent.putExtra("rest_name", data.rest_name)
            intent.putExtra("compound_code", data.compound_code)
            intent.putExtra("rest_phone", data.rest_phone)
            intent.putExtra("feed_time", "")
            intent.putExtra("latLng", data.latLng)
            intent.putExtra("place_id", data.place_id)
            intent.putExtra("vicinity", data.vicinity)
            intent.putExtra("status", "")
            v.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setSpots(data: List<RestData>) {
        this.data = data
    }

    fun getSpots(): List<RestData> {
        return data
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var city: TextView = view.findViewById(R.id.item_city)
        var image: ImageView = view.findViewById(R.id.item_image)
    }

}
