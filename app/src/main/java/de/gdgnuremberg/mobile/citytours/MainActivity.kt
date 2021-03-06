package de.gdgnuremberg.mobile.citytours

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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

                detector(imageBitmap)
            }
        }
    }

    private fun setupNavigation() {
        val navController = findNavController(mainNavigationFragment)
        setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp() =
            Navigation.findNavController(this, R.id.mainNavigationFragment).navigateUp()

    private fun detector(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)

        // Set the default settings
        val detector = FirebaseVision.getInstance()
                .visionCloudLandmarkDetector

        detector.detectInImage(image)
                .addOnSuccessListener { firebaseVisionCloudLandmarks ->
                    for (landmark in firebaseVisionCloudLandmarks) {
                        val landmarkName = landmark.landmark
                        Log.v("detector", "bounds: $landmarkName")

                        // Multiple locations are possible, e.g., the location of the depicted
                        // landmark and the location the picture was taken.
                        for (loc in landmark.locations) {
                            Log.v("detector", "latitude: ${loc.latitude}")
                            Log.v("detector", "longitude: ${loc.longitude}")
                        }

                        Toast.makeText(this@MainActivity,
                                "Success", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    Log.e("Landmark", e.message)

                    Toast.makeText(this@MainActivity,
                            e.message, Toast.LENGTH_LONG).show()
                }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }
}
