package de.gdgnuremberg.mobile.citytours

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark
import com.google.firebase.ml.vision.common.FirebaseVisionImage
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

    private fun detector(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)

        // Set the default settings
        val detector = FirebaseVision.getInstance()
                .visionCloudLandmarkDetector

        val result = detector.detectInImage(image)
                .addOnSuccessListener { firebaseVisionCloudLandmarks ->
                    for (landmark in firebaseVisionCloudLandmarks) {

                        val bounds = landmark.boundingBox
                        val landmarkName = landmark.landmark
                        val entityId = landmark.entityId
                        val confidence = landmark.confidence
                        Log.v("detector", "bounds: $landmarkName")
                        Log.v("detector", "entityId: $entityId")

                        // Multiple locations are possible, e.g., the location of the depicted
                        // landmark and the location the picture was taken.
                        for (loc in landmark.locations) {
                            val latitude = loc.latitude
                            val longitude = loc.longitude

                            Log.v("detector", "latitude: " + java.lang.Double.toString(latitude))
                            Log.v("detector", "longitude: " + java.lang.Double.toString(longitude))

                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    Log.e("Landmark", e.message)
                    Toast.makeText(this@LandmarkDetector,
                            e.message, Toast.LENGTH_LONG).show()
                }

    }

}
