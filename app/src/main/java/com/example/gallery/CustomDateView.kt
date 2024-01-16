import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginBottom
import com.example.gallery.R

class CustomDateView(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val dateText: TextView
    private val imagesContainer: GridLayout

    init {
        LayoutInflater.from(context).inflate(R.layout.date_item, this, true)
        dateText = findViewById(R.id.dateText)
        imagesContainer = findViewById(R.id.imagesContainer)
    }

    fun setDateAndImages(date: String, imagePaths: List<String>, imageWidth: Int) {
        dateText.text = date
        imagesContainer.removeAllViews()
        val margin = 12
        for (path in imagePaths) {
            val imageView = ImageView(context)
            val bitmap = BitmapFactory.decodeFile(path)
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, imageWidth * 25, imageWidth * 25, false)
            imageView.setImageBitmap(scaledBitmap)

            val layoutParams = GridLayout.LayoutParams()
            layoutParams.width = imageWidth * 25
            layoutParams.height = imageWidth * 25
            layoutParams.setMargins(margin, margin, margin, margin)
            imageView.layoutParams = layoutParams

            imagesContainer.addView(imageView)
        }
    }
}
