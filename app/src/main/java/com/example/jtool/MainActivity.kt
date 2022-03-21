package com.example.jtool

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var dkLimitedTimeBtn: Button
    lateinit var lyricsBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Jtool"

        dkLimitedTimeBtn = findViewById(R.id.btn_duokan)
        lyricsBtn = findViewById(R.id.btn_lyrics)


    }

    override fun onResume() {
        super.onResume()

        dkLimitedTimeBtn.setOnClickListener {
            val intent1 = Intent(this, DuoKanActivity::class.java)
            startActivity(intent1)
        }

        lyricsBtn.setOnClickListener {
            val intent2 = Intent(this, LyricsActivity::class.java)
            startActivity(intent2)
        }
    }


}