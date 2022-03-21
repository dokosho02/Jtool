package com.example.jtool

import android.util.Log
import org.jsoup.Jsoup
import kotlin.properties.Delegates


class Book(
    private val url: String
) {
    fun bookPage(): Array<Any> {
        var title by Delegates.notNull<String>()

        var score by Delegates.notNull<Float>()
        var reviewCount by Delegates.notNull<Int>()
        var datePublished by Delegates.notNull<String>()

        val document = Jsoup.connect(url).get()

        title = document.getElementsByTag("title")
            .toString().split("【").first()
            .drop(7)

        // Content
        val bookContentElement = document.getElementById("book-content")
        val bkCon = Jsoup.parse(bookContentElement.toString())
        val temp = bkCon.getElementsByTag("p")
        val bookContent = temp.toString()
            .replace("<br>","\n")
            .drop(3).dropLast(4)
            .replace("<p>", "\n")
            .replace("</p>", "\n")

        Log.v("Content", bookContent)    // no problem on 2021-03-02

        // score
        val allEm = document.getElementsByTag("em")
        for (item in allEm){
            if (item.toString().contains("score") ){
                score = item.text().toFloat()
            }
            else {
                score = 0F
            }
        }
        Log.v("Score", score.toString() )   // no problem on 2021-03-02

        // date published
        val allTd = document.getElementsByTag("td")
        for (item in allTd){
            if (item.toString().contains("datePublished") ){
                datePublished = item.text()
            }
            else {
                datePublished = "None"
            }
        }
        Log.v("datePublished", datePublished )

        // reviewCount
        val allSpan = document.getElementsByTag("span")
        for (item in allSpan){
            if (item.toString().contains("reviewCount") ){
                reviewCount = item.text().filter { it.isDigit() }.toInt()
            }
            else {
                reviewCount = 0
            }
        }
        Log.v("reviewCount", reviewCount.toString() )
//        return arrayOf(title, bookContent)

        return arrayOf(title, datePublished, score, reviewCount, bookContent)
    }
}