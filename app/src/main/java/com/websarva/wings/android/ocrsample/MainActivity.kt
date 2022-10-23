package com.websarva.wings.android.ocrsample

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable.CONTENTS_FILE_DESCRIPTOR
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.io.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btClick = findViewById<Button>(R.id.btOutput)
        val listener = OCROutListenner()

        btClick.setOnClickListener(listener)
    }
    private inner class OCROutListenner : View.OnClickListener{
        override fun onClick(view: View){
            val items = mutableListOf<String>()
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(
                       MediaStore.VOLUME_EXTERNAL_PRIMARY
                    )
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
            )
            val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ?"

            applicationContext.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    println(contentUri)
                    items.add(contentUri.toString())
                }

            }//Uriget


            var output = findViewById<TextView>(R.id.OCROutput)
            output.text="test"
            println(items.count())
            println(collection)
            println(projection)

            /*var image:FirebaseVisionImage? =null
            try {
                image = FirebaseVisionImage.fromFilePath(this@MainActivity, uri)
            } catch (e: IOException) {
                e.printStackTrace()
            }//uriから画像を取り込む



            val detector = FirebaseVision.getInstance()
                .onDeviceTextRecognizer
            if(image != null){
                val result = detector.processImage(image)
                    .addOnSuccessListener { firebaseVisionText ->
                        val OCRresult = processTextBlock(firebaseVisionText)
                        output.text = OCRresult
                    }
                    .addOnFailureListener { e ->
                        output.text = "OCR失敗"
                    }
            }else{
                output.text = "imageが読み込めませんでした"
            }*/
        }
    }

    private fun processTextBlock(result:FirebaseVisionText): String {
        val resultText = result.text
        for (block in result.textBlocks) {
            val blockText = block.text
            Log.d("MYOCR", "blockText=$blockText")
            val blockConfidence = block.confidence
            val blockLanguages = block.recognizedLanguages
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            for (line in block.lines) {
                val lineText = line.text
                Log.d("MYOCR", "lineText=$lineText")
                return lineText
            }
        }
        return resultText
    }
}