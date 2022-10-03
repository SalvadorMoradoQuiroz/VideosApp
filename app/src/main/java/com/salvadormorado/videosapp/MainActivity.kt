package com.salvadormorado.videosapp

import android.app.ProgressDialog
import android.content.Intent
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var controlador: MediaController
    private lateinit var pd: ProgressDialog
    private lateinit var textViewLink:TextView
    private var urlVideo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoView = findViewById(R.id.videoView)
        textViewLink = findViewById(R.id.textViewLink)
        val buttonSelected = findViewById<Button>(R.id.buttonSelected)
        val buttonTxt = findViewById<Button>(R.id.buttonTxt)

        pd = ProgressDialog(this@MainActivity)
        pd.setMessage("Cargando video, por favor espera...")

        controlador = MediaController(this@MainActivity)
        controlador.setAnchorView(videoView)

        videoView.setMediaController(controlador)

        videoView.setOnCompletionListener {
            Toast.makeText(
                applicationContext,
                "Se termino de reproducir el video.",
                Toast.LENGTH_SHORT
            ).show()
        }

        videoView.setOnErrorListener { mediaPlayer, i, i2 ->
            pd.dismiss()

            Toast.makeText(
                applicationContext,
                "Error al reproducir el video, se intentará reproducir en reproductor del dispositivo.",
                Toast.LENGTH_SHORT
            ).show()

            val uri = Uri.parse(urlVideo)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setDataAndType(uri, "video/mp4")
            startActivity(intent)

            false
        }

        videoView.setOnPreparedListener(OnPreparedListener { //close the progress dialog when buffering is done
            pd.dismiss()
        })

        buttonSelected.setOnClickListener({
            val selectVideo: Intent =
                Intent().setType("video/*").setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(selectVideo, "Seleccionar video..."), 111)
        })

        buttonTxt.setOnClickListener({
            val selectTxt: Intent =
                Intent().setType("text/plain").setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(selectTxt, "Seleccionar txt..."), 222)
        })
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK) {
            pd.show()
            val file = data?.data
            Log.e("Archivo video:", "$file")
            //Toast.makeText(applicationContext, "Se seleccionó un video.", Toast.LENGTH_SHORT).show()

            textViewLink.visibility = View.VISIBLE
            textViewLink.text = "Link de video:\n ${file}"

            videoView.setVideoURI(file)
            videoView.requestFocus()
            videoView.start()

        } else if (requestCode == 222 && resultCode == RESULT_OK) {
            pd.show()

            var inputStream: InputStream? = contentResolver.openInputStream(data?.data!!)
            Log.e("inputStream: ", "${inputStream.toString()}")

            val reader = BufferedReader(inputStream!!.reader())
            try {
                var line = reader.readLine()
                Log.e("line: ", "${line}")
                if(line!=null){
                    urlVideo = line

                }
                /*while (line != null) {
                    line = reader.readLine()
                    Log.e("line: ", "${line}")
                }*/
            } finally {
                reader.close()
            }
            textViewLink.visibility = View.VISIBLE
            textViewLink.text = "Link de video:\n ${urlVideo}"

            val uri = Uri.parse(urlVideo)
            videoView.setVideoURI(uri)
            videoView.requestFocus()
            videoView.start()
        }
    }

    /*private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Verifica permisos para Android 6.0+
            val permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.i("Mensaje", "No se tiene permiso para leer.")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    225
                )
            } else {
                Log.i("Mensaje", "Se tiene permiso para leer!")
            }
        }
    }*/
}