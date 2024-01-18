import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.media.MediaMetadataRetriever
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.gallery.ImageData
import com.example.gallery.MediaData
import com.example.gallery.R
import com.example.gallery.VideoData

class CustomDateView(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val dateText: TextView
    private val imagesContainer: GridLayout

    init {
        LayoutInflater.from(context).inflate(R.layout.date_item, this, true)
        dateText = findViewById(R.id.dateText)
        imagesContainer = findViewById(R.id.imagesContainer)
    }



    // Interfejs do nasłuchiwania zdarzeń kliknięć w CustomDateView
    interface OnItemClickListener {
        fun onItemClick(mediaData: MediaData)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    private fun showImagePreview(mediaData: MediaData) {
        onItemClickListener?.onItemClick(mediaData)
    }

    fun setDateAndImages(date: String, imagePaths: List<String>, imageWidth: Int) {
        dateText.text = date
        imagesContainer.removeAllViews()
        val margin = 12
        for (path in imagePaths) {
            if (path.endsWith(".jpg")) {
                val imageView = ImageView(context)
                val bitmap = BitmapFactory.decodeFile(path)
                val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, imageWidth * 23, imageWidth * 23, false) }
                imageView.setImageBitmap(scaledBitmap)
                val layoutParams = GridLayout.LayoutParams()
                layoutParams.width = imageWidth * 23
                layoutParams.height = imageWidth * 23
                layoutParams.setMargins(margin, margin, margin, margin)
                imageView.layoutParams = layoutParams
                imagesContainer.addView(imageView)
                imageView.setOnClickListener {
                    // Po kliknięciu na obraz przekazujemy informacje do nasłuchiwacza
                    showImagePreview(ImageData(path, 0, false)) // Uwaga: Brak daty w tym przykładzie
                }
            }
            else if (path.endsWith(".mp4")) {
                val imageView = ImageView(context)
                val layoutParams = GridLayout.LayoutParams()
                layoutParams.width = imageWidth * 23
                layoutParams.height = imageWidth * 23
                layoutParams.setMargins(margin, margin, margin, margin)
                imageView.layoutParams = layoutParams

                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(path)

                val bitmap = retriever.frameAtTime
                val scaledBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, imageWidth * 23, imageWidth * 23, false) }
                val videoDuration = getVideoDuration(path)
                val triangleBitmap = addTriangleToBitmap(scaledBitmap, videoDuration)

                imageView.setImageBitmap(triangleBitmap)
                imagesContainer.addView(imageView)

                imageView.setOnClickListener {
                    showImagePreview(VideoData(path, 0, false))
                }
            }
        }
    }

    private fun addTriangleToBitmap(originalBitmap: Bitmap?, videoDuration: String?): Bitmap? {
        originalBitmap?.let {
            val triangleBitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(triangleBitmap)
            canvas.drawBitmap(it, 0f, 0f, null)

            val strokePaint = Paint().apply {
                style = Paint.Style.FILL_AND_STROKE
                shader = LinearGradient(
                    it.width.toFloat() / 3f, (it.height.toFloat() / 2),
                    it.width.toFloat() / 3f * 1.7f, (it.height.toFloat() / 2),
                    Color.GRAY, Color.BLACK, Shader.TileMode.REPEAT
                )
            }

            val path = Path()
            path.moveTo(it.width.toFloat() / 3f * 1.6f, (it.height.toFloat() / 2))
            path.lineTo(it.width.toFloat() / 3f * 1.1f, it.height.toFloat() / 3f * 1.2f)
            path.lineTo(it.width.toFloat() / 3f * 1.1f, it.height.toFloat() / 3f * 1.8f)
            path.close()

            canvas.drawPath(path, strokePaint)

            videoDuration?.let { duration ->
                val textPaint = Paint().apply {
                    color = Color.WHITE
                    textSize = 45f
                    isFakeBoldText = true
                }
                canvas.drawText(duration, 5f, 40f, textPaint)
            }
            return triangleBitmap
        }
        return null
    }

    private fun getVideoDuration(videoPath: String): String? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath)

        val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val duration = durationString?.toLongOrNull()

        return if (duration != null) {
            val seconds = duration / 1000
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            String.format("%02d:%02d", minutes, remainingSeconds)
        } else {
            null
        }
    }
}
