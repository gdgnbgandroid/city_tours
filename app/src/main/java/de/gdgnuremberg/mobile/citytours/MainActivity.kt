package de.gdgnuremberg.mobile.citytours

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()

        val mStartCameraButton = findViewById<Button>(R.id.button_take_image)
        mStartCameraButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.let {
                it.get("data") as Bitmap

            }
            if (imageBitmap != null) {
                val mImageView = findViewById<ImageView>(R.id.iv_taken_image)
                mImageView.setImageBitmap(imageBitmap)
            }

        }
    }

    private fun setupNavigation() {
        val navController = findNavController(mainNavigationFragment)
        setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp() =
            Navigation.findNavController(this, R.id.mainNavigationFragment).navigateUp()

}
