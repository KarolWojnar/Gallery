import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.ImageData
import com.example.gallery.R
import java.text.SimpleDateFormat
import java.util.*

class ImageAdapter(
    private val imagesMap: Map<String, List<ImageData>>,
    private val margin: Int,
    private val imageWidth: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dateHeaderFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val VIEW_TYPE_DATE = 1
    private val VIEW_TYPE_IMAGE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.date_item, parent, false)
                DateViewHolder(view)
            }
            VIEW_TYPE_IMAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
                ImageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DateViewHolder -> {
                val datePosition = position / 2
                val dates = imagesMap.keys.toList()

                if (datePosition < dates.size) {
                    val currentDate = dates[datePosition]
                    holder.dateText.text = currentDate
                }
            }
            is ImageViewHolder -> {
                val datePosition = (position - 1) / 2
                val dates = imagesMap.keys.toList()

                if (datePosition < dates.size) {
                    val currentDate = dates[datePosition]
                    val imagesForDate = imagesMap[currentDate] ?: emptyList()

                    if (holder.imageContainer.childCount > 0) {
                        // Usuń stare ImageView z kontenera
                        holder.imageContainer.removeAllViews()
                    }

                    // Dodaj nowe ImageView do kontenera dla każdego zdjęcia
                    for (imageData in imagesForDate) {
                        val imageView = ImageView(holder.itemView.context)
                        imageView.layoutParams = ViewGroup.LayoutParams(imageWidth, imageWidth)
                        // Tutaj możesz ustawić obraz za pomocą biblioteki do ładowania obrazów (np. Glide, Picasso)
                        // imageView.setImageResource(...)
                        holder.imageContainer.addView(imageView)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return imagesMap.size * 2 // Mnożymy przez 2, aby uwzględnić różne typy widoków
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) VIEW_TYPE_DATE else VIEW_TYPE_IMAGE
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.dateHeaderText)
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageContainer: LinearLayout = itemView.findViewById(R.id.imageContainer)
    }
}
