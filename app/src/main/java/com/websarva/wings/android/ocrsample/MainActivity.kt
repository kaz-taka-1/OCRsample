package com.websarva.wings.android.ocrsample

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import java.io.File

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
            val bt = findViewById<Button>(R.id.btOutput)
            val inputStr = bt.text.toString()

            output.text = inputStr + "の結果が表示されます。"
        }
    }


}