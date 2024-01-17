import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.ImageData

import com.example.gallery.R
import com.example.gallery.VideoData
import java.text.SimpleDateFormat
import java.util.*

class ImageAdapter(
    private val imagesList: List<ImageData>,
    private val videosList: List<VideoData>,
    private val margin: Int,
    private val imageWidth: Int
) : RecyclerView.Adapter<ImageAdapter.DateViewHolder>() {

    private val dateHeaderFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val imagesContainer: GridLayout = itemView.findViewById(R.id.imagesContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.date_item, parent, false)
        return DateViewHolder(view)
    }




    // Interfejs do nasłuchiwania zdarzeń kliknięć
    interface OnItemClickListener {
        fun onItemClick(imageData: ImageData)
    }

    // Zmienna przechowująca nasłuchiwacz
    private var onItemClickListener: OnItemClickListener? = null

    // Metoda ustawiająca nasłuchiwacz
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }



    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val currentDate = findUniqueDates(imagesList, videosList)[position]
        val paths = findPathsByDate(currentDate)

        holder.imagesContainer.removeAllViews()

        val customDateView = CustomDateView(holder.itemView.context)
        customDateView.setDateAndImages(currentDate, paths, imageWidth)


        customDateView.setOnItemClickListener(object : CustomDateView.OnItemClickListener {
            override fun onItemClick(imageData: ImageData) {
                onItemClickListener?.onItemClick(imageData)
            }
        })


        holder.imagesContainer.addView(customDateView)
    }

    override fun getItemCount(): Int {
        return findUniqueDates(imagesList, videosList).size
    }

    private fun findPathsByDate(date: String): List<String> {
        val imagePaths = imagesList
            .filter { image -> formatDate(image.imageDate) == date }
            .map { image -> image.imagePath }

        val videoPaths = videosList
            .filter { video -> formatDate(video.videoDate) == date }
            .map { video -> video.videoPath }

        return imagePaths + videoPaths
    }


    private fun findUniqueDates(imagesList: List<ImageData>, videosList: List<VideoData>): List<String> {
        val uniqueDatesList = mutableListOf<String>()
        for (imageData in imagesList) {
            val formattedDate = formatDate(imageData.imageDate)
            if (!uniqueDatesList.contains(formattedDate)) {
                uniqueDatesList.add(formattedDate)
            }
        }
        for (videoData in videosList) {
            val formattedDate = formatDate(videoData.videoDate)
            if (!uniqueDatesList.contains(formattedDate)) {
                uniqueDatesList.add(formattedDate)
            }
        }
        return uniqueDatesList
    }

    private fun formatDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }


}

