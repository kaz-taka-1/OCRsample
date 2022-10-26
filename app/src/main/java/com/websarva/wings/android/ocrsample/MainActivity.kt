package com.websarva.wings.android.ocrsample

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.io.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 端末の画像を取得する
        loadImages()
    }

    // requestPermissionsの後に呼び出される
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_CODE_PERMISSION) return

        if (grantResults.isNotEmpty() && (grantResults.first() == PackageManager.PERMISSION_GRANTED)) {
            // 許可した後の処理
        }
    }

    private fun loadImages() {

        // READ_EXTERNAL_STORAGEパーミッションが許可されているかチェックする
        val REQUEST_CODE_PERMISSION = 1
        val isGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        // 許可されていない場合、パーミッションの許可を選択するダイアログを表示する
        if (!isGranted) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSION
            )
            return
        }

        val uris = mutableListOf<Uri>()

        // 外部ストレージを指定する
        val queryUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        // queryで取得したいカラムを指定する
        val projection: Array<String> = arrayOf(MediaStore.Images.Media._ID)

        /**
         * 検索条件を指定する。
         * 条件は「"${MediaStore.Images.Media.DATE_TAKEN} >= ?"」のように記述する。
         * 全件取得のときはnullを指定する。
         */
        val selection: String? = null

        /**
         * selectionの「?」に代入する条件を指定する。
         * 全件取得のときはnullを指定する。
         */
        val selectionArgs: Array<String>? = null

        /**
         * ソート条件を指定する。
         * 条件は「"${MediaStore.Images.Media.DATE_ADDED} DESC"」のように記述する。
         * 条件を指定しない場合はnullを設定する。
         */
        val sortOrder: String? = null

        contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        ).use { cursor ->

            cursor ?: return@use

            // projectionで指定していないカラムを取得しようとすると、例外がスローされる。
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {

                val imageId = cursor.getLong(columnIndex)

                // メディアアイテムのURIを取得する
                val uri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId.toString()
                )

                uris.add(uri)
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