package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import coil3.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class MainActivity : AppCompatActivity() {

    companion object {
        const val IMAGE_URL = "https://png.pngtree.com/png-clipart/20190614/original/pngtree-beautiful-earth-elements-png-image_3704319.jpg"
        const val MARS_URL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSeksFtqTWHTRuXVIspqVOjf1gDs9q-pSGHDw&s"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       /* CoroutineScope(Dispatchers.IO).launch {
            val bitmap = getImage()
            withContext(Dispatchers.Main) {
                //delay(30000)
               // Thread.sleep(30000)
                showImage(bitmap)
            }
        }*/
       /* CoroutineScope(Dispatchers.Main).launch {
            val bitmap: Bitmap
            withContext(Dispatchers.IO) {
                bitmap = getImage()
            }
            showImage(bitmap)
        }*/
        CoroutineScope(Dispatchers.Main).launch {
            val first = async(Dispatchers.IO) {
                getImage(IMAGE_URL)
            }
            val second = async(Dispatchers.Default) {
                toGrayscale(withContext(Dispatchers.IO){
                    getImage(MARS_URL)
                })
            }
            val bitmap1 = first.await()
            val bitmap2 = second.await()
            showImage(bitmap1)
            showImage2(bitmap2)
            findViewById<ImageView>(R.id.imageView2).load("data:image/jpeg;base64,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")
        }
    }

    private fun getImage(url: String) = URL(url).openStream().use { BitmapFactory.decodeStream(it) }

    private fun showImage(bitmap: Bitmap) {
        findViewById<ImageView>(R.id.imageView).apply {
            setImageBitmap(bitmap)
            visibility = View.VISIBLE
        }
    }

    private fun showImage2(bitmap: Bitmap) {
        findViewById<ImageView>(R.id.imageView2).apply {
            setImageBitmap(bitmap)
            visibility = View.VISIBLE
        }
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
    }

    fun toGrayscale(bmpOriginal: Bitmap): Bitmap {
        val height = bmpOriginal.height
        val width = bmpOriginal.width
        val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.setColorFilter(f)
        c.drawBitmap(bmpOriginal, 0f, 0f, paint)
        return bmpGrayscale
    }
}