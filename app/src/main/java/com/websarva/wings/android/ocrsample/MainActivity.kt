package com.websarva.wings.android.ocrsample

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable.CONTENTS_FILE_DESCRIPTOR
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.io.File
import java.io.IOException

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
            val output = findViewById<TextView>(R.id.OCROutput)

            val uriStr = "/storage/self/primary/Download/test.jpeg"
            val uri = Uri.parse(uriStr)
            var image:FirebaseVisionImage? =null
            try {
                image = FirebaseVisionImage.fromFilePath(this@MainActivity, uri)
            } catch (e: IOException) {
                e.printStackTrace()
            }



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
            }
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