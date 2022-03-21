package com.example.jtool

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.withContext

import org.jsoup.Jsoup
import java.lang.reflect.Type

class DuoKanActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private lateinit var textView: TextView
    var limitedTimeSite = "https://www.duokan.com/special/18956"
    lateinit var scrapBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_duokan)

        title = "Duokan Limited Time -- Weekly"
        textView = findViewById(R.id.textView)
        scrapBtn = findViewById(R.id.btn_scrap)
    }

    override fun onResume() {
        super.onResume()
        scrapBtn.setOnClickListener {
            scope.launch { webScratch() }
        }
    }

    override fun onPause() {
        super.onPause()
        scope.coroutineContext.cancelChildren()
    }

    private suspend fun webScratch() {
        try {
            var words: String = ""

            // onPreExecuteと同等の処理
            withContext(Dispatchers.Main) { Log.v("Check", "Starting") }

            // doInBackgroundメソッドと同等の処理
            val document = Jsoup.connect(limitedTimeSite).get()    //
            val bookList = document.getElementById("book_list")
            val temp = bookList.text()

            val listType: Type = object : TypeToken<List<BookList?>?>() {}.type
            val sarray: List<BookList> = Gson().fromJson(temp, listType)

            for (i in sarray.indices) {
                Log.v("Check", sarray[i].toString())
                val bk = Book(sarray[i].getURL())
//                    val (title, bookContent) = bk.bookPage()
                val (title, datePublished, score, reviewCount, bookContent) = bk.bookPage()
                val sakka = sarray[i].authors.toString()
                Log.v("Authors", sakka)

                words =
                    "$words\n## ${i + 1} .  $title\n$sakka\n\t\t$datePublished\n\t\t$score by $reviewCount\n$bookContent\n********\n"

            }

            // onPostExecuteメソッドと同等の処理
            withContext(Dispatchers.Main) {
                textView.text = words
            }
        } catch (e: Exception) {
            // onCancelledメソッドと同等の処理
            Log.v("Check", "Cancelled")
        }
    }


}