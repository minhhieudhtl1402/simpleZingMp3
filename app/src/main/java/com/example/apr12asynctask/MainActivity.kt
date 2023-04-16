package com.example.apr12asynctask

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.example.apr12asynctask.model.Song
import de.hdodenhof.circleimageview.CircleImageView
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import java.sql.Time
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var btnBackward: ImageButton
    private lateinit var btnForward: ImageButton
    private lateinit var btnPause: ImageButton
    private lateinit var btnPlay: ImageButton
    private lateinit var sbMusic: SeekBar
    private lateinit var tvTitle: TextView
    private lateinit var tvTime: TextView
    private lateinit var ivSong: CircleImageView
    private lateinit var mediaPlayer: MediaPlayer
    private var finalTime: Double = 0.0
    private var startTime: Double = 0.0
    private var forwardTime = 10000
    private var backwardTime = 10000
    final var oneTimeOnly: Int = 0
    private var handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnBackward = findViewById(R.id.btn_backward)
        btnForward = findViewById(R.id.btn_forward)
        btnPause = findViewById(R.id.btn_pause)
        btnPlay = findViewById(R.id.btn_play)
        sbMusic = findViewById(R.id.sb_music)
        tvTitle = findViewById(R.id.tv_title)
        tvTime = findViewById(R.id.tv_time)
        ivSong = findViewById(R.id.iv_music)


        val link = "https://nhachayvn.net/bai-hat/cach-mang"
        mediaPlayer = MediaPlayer.create(this, R.raw.uqc)
        sbMusic.isClickable = false


        btnPlay.setOnClickListener {


            playMusic()
            changeImage()


        }
        btnPause.setOnClickListener {

            ivSong.clearAnimation()
            mediaPlayer.pause()
        }
        btnForward.setOnClickListener(
            View.OnClickListener {
                val temp: Int = startTime.toInt()
                if ((temp + forwardTime) <= finalTime) {
                    startTime = startTime + forwardTime
                    mediaPlayer.seekTo(startTime.toInt())
                } else {
                    Toast.makeText(this, "Cant jump forward", Toast.LENGTH_SHORT).show()
                }
            }
        )

        btnBackward.setOnClickListener(
            View.OnClickListener {
                val temp: Int = startTime.toInt()
                if ((temp - backwardTime) >= 0) {
                    startTime = startTime - backwardTime
                    mediaPlayer.seekTo(startTime.toInt())
                } else {
                    Toast.makeText(this, "Cant go back", Toast.LENGTH_SHORT).show()
                }
            }
        )

        tvTitle.setText(
            resources.getIdentifier(
                "uqc",
                "raw",
                packageName
            )
        )

    }

    private fun changeImage() {
        DownloadFile().execute("https://media.cdnandroid.com/item_images/1095966/imagen-zing-zing-mp3-music-player-0ori.jpg")
        val animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.spin_around)
        ivSong.animation = animation
        ivSong.startAnimation(animation)
    }

    private fun playMusic() {
        mediaPlayer.start();

        finalTime = mediaPlayer.duration.toDouble()
        startTime = mediaPlayer.currentPosition.toDouble()
        if (oneTimeOnly == 0) {
            sbMusic.max = finalTime.toInt()
            oneTimeOnly = 1
        }


        //format time
        tvTime.setText(
            String.format(
                "%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))
            )
        )

        sbMusic.setProgress(startTime.toInt())
        handler.postDelayed(UpdateSongTime, 100)
    }

    private var UpdateSongTime: Runnable = object : Runnable {
        override fun run() {
            startTime = mediaPlayer.currentPosition.toDouble()
            tvTime.setText(
                String.format(
                    "%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))
                )
            )
            sbMusic.setProgress(startTime.toInt())
            handler.postDelayed(this, 100)
        }

    }

//     private var UpdateSongTime:Runnable= Runnable {
//        startTime=mediaPlayer.currentPosition.toDouble()
//        tvTime.setText(
//            String.format(
//                "%d min, %d sec",
//                TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
//                TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - TimeUnit.MINUTES.toMinutes(
//                    startTime.toLong()
//                )
//            )
//        )
//        sbMusic.setProgress(startTime.toInt())
//        handler.postDelayed(this.UpdateSongTime,100)
//
//    }


    inner class DownloadFile : AsyncTask<String, Void, Bitmap>() {
        override fun onPreExecute() {
            Toast.makeText(this@MainActivity, "Prepare for downloading", Toast.LENGTH_SHORT).show()
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): Bitmap {
            val link = params[0]
            val url = URL(link)
            val connection = url.openConnection()
            val inputStream = connection.getInputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            return bitmap
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: Bitmap?) {
            ivSong.setImageBitmap(result)
        }

    }

}