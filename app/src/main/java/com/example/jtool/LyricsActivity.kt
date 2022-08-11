package com.example.jtool

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.*
import java.io.IOException

class LyricsActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Default)

    lateinit var lyricsText: TextView
    lateinit var input: EditText
    private lateinit var inputNo: EditText

    private lateinit var searchBtn: Button
    lateinit var radioGroup: RadioGroup
    lateinit var radioButton: RadioButton
    lateinit var engine: String
    lateinit var keyword: String
    lateinit var getBtn: Button
    lateinit var grabBtn: Button

    lateinit var linkInput: EditText

    private var number = 1
    private var songLink = ""

    var songsInfoList = arrayListOf<Array<Any>>()
    lateinit var lyricsInfo: Array<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyrics)

        title = "Lyrics"
        lyricsText = findViewById(R.id.lyrics_text)
        linkInput   = findViewById(R.id.link_input)
        grabBtn     = findViewById(R.id.btn_grab)

        input      = findViewById(R.id.song_title_input)
        searchBtn  = findViewById(R.id.btn_search)
        inputNo    = findViewById(R.id.song_number_input)
        getBtn     = findViewById(R.id.btn_get)
        radioGroup = findViewById(R.id.radioGroup)
    }


    private fun getEngine() {
        val intSelectButton: Int = radioGroup.checkedRadioButtonId
        radioButton = findViewById(intSelectButton)
        engine    = radioButton.text.toString()
        Log.v("engine", engine)
    }

    override fun onResume() {
        super.onResume()

        searchBtn.setOnClickListener {
            getEngine()
            keyword = input.text.toString()

            scope.launch {
                searchInfo()
            }
        }

        getBtn.setOnClickListener {
            songLink=""
            number = inputNo.text.toString().toInt()

            scope.launch {
                getLyrics()
            }
        }

        grabBtn.setOnClickListener {
            getEngine()
            songLink = linkInput.text.toString()
            scope.launch { getLyrics() }
        }
    }

    override fun onPause() {
        super.onPause()
        scope.coroutineContext.cancelChildren()
    }

    private suspend fun searchInfo() {
        var info = ""
        try {
                info = getSongsInfo(keyword, engine)

                withContext(Dispatchers.Main) {
                    lyricsText.text = info

                    if (songsInfoList.size > 0){
                        getBtn.isVisible = true
                    } else{
                        getBtn.isVisible = false
                        Toast.makeText(this@LyricsActivity, "Sorry, no result...", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) { }
        }


    private suspend fun getLyrics() {
        var info = ""
        try {
            info = getLetra(number, engine)

            withContext(Dispatchers.Main) {
                lyricsText.text = info
            }

        } catch (e: IOException) { }
    }


    private fun getSongsInfo(keyword: String, engine:String): String {
        var songsInfoText = ""
        when (engine) {

            "Mojim" -> {
                val song = MojimSingle()
                songsInfoList = song.searchSongs(keyword)
                songsInfoList.reverse()
                for (i in songsInfoList.indices){
                    val item = songsInfoList[i]
                    val count = i+1
                    // count, songTitle, artist, albumInfo, releaseDate, songLink
                    songsInfoText += "[${count}] ${item[1]}\n        by ${item[2]}\n      on ${item[4]}\n    from ${item[3]}\n"
                }
            }

            "UtaNet" -> {
                val song = UtaNetSingle()
                songsInfoList =  song.searchSongs(keyword)
                for (item in songsInfoList){
                    val (count, songTitle, artist, lyricsInfo, _) = item
                    songsInfoText += "[${count}] ${songTitle}\n      by ${artist}\n     Lyrics: ${lyricsInfo}\n"
                }
            }


        }
        return songsInfoText
    }

    private fun getLetra(number: Int, engine: String): String {
        var lyricInfo = ""
        var selectSongNumber = ""
        when (engine) {
            "Mojim" -> {
                val song = MojimSingle()
                if (songLink=="") {
                    for (i in songsInfoList.indices) {
                        val item = songsInfoList[i]
                        val count = i + 1
                        val songNumber = item[5]
                        if (count.toString() == number.toString()) {
                            selectSongNumber = songNumber.toString()
                        }

                    }
                }
                else {
                    selectSongNumber = songLink.replace(song.searchEngine,"")
                }
                lyricsInfo = song.scrapLyrics(selectSongNumber)
                lyricInfo = "# ${lyricsInfo[0]}\n## Lyrics\n${lyricsInfo[1]}\n\n- Performed by ${lyricsInfo[2]}\n- Lyrics by ${lyricsInfo[3]}\n- Composed by ${lyricsInfo[4]}\n- Released on ${lyricsInfo[5]}\n- Numbered as ${lyricsInfo[6]}\n- Produced by ${lyricsInfo[7]}"
            }

            "UtaNet" -> {
                val song = UtaNetSingle()
                if (songLink=="") {
                    for (item in songsInfoList) {
                        val (count, _, _, _, songNumber) = item

                        if (count.toString() == number.toString()) {
                            selectSongNumber = songNumber.toString()
                        }
                    }
                }
                else {
                    selectSongNumber = songLink.replace(song.searchEngine, "")
                }
                lyricsInfo = song.scrapLyrics(selectSongNumber)
                val kashi = lyricsInfo[1].toString()
                        .replace("<br>\n", "\n")
                        .replace("<br>", "")

                lyricInfo = "# ${lyricsInfo[0]}\n## Lyrics\n${kashi}\n\n- Performed by ${lyricsInfo[2]}\n- Lyrics by ${lyricsInfo[3]}\n- Composed by ${lyricsInfo[4]}\n- Released on ${lyricsInfo[5]}\n- Numbered as ${lyricsInfo[6]}\n- Produced by ${lyricsInfo[7]}"
            }
        }
        return lyricInfo
    }



}