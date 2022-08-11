package com.example.jtool

import android.util.Log
import org.jsoup.Jsoup
import java.io.IOException

class UtaNetSingle : Single() {
    override var searchEngine = "https://www.uta-net.com"
    private val aSelect = "2"

    fun searchSongs(titleKeyword:String): ArrayList<Array<Any>> {
        val searchLink = "${searchEngine}/search/?Aselect=${aSelect}&Keyword=${titleKeyword}&Bselect=1&x=0&y=0"
        // https://www.uta-net.com/search/?Keyword=%E7%BF%BC%E3%82%92%E5%BA%83%E3%81%92%E3%81%A6&Aselect=2&Bselect=3
        val document = Jsoup.connect(searchLink).get()
        val resultTable = document.getElementsByTag("tr")

        println(resultTable)

        for ( i in resultTable.indices.drop(1).dropLast(1) ) {
            val count = i.toString()
            val songTitle  = resultTable[i].select("span[class*=songlist-title]").text()
            val artist     = resultTable[i].select("a[href*=artist]").text()
            val lyricsInfo = resultTable[i].select("span[class*=pc-utaidashi]").text().split(graphicSpace)[0]
            val songLink = resultTable[i].select("a[href*=song]").attr("href")

            elementList = arrayOf(count, songTitle, artist, lyricsInfo, songLink )
            infoList.add( elementList )
        }
        return infoList
    }

    fun scrapLyrics(songRelativeLink:String): Array<Any> {
        number = songRelativeLink.drop(1).dropLast(1).split('/')[1]
        val graphicSpace = "\u3000"

        val songLink = "${searchEngine}${songRelativeLink}"  //
        Log.v("Scrap", songLink)
        val document = Jsoup.connect(songLink).get()

        title = document.getElementsByTag("h2").first()!!.text()
        val kashi = document.getElementById("kashi_area")

        val temp = kashi.toString()
            .replace(graphicSpace, " ")
            .drop(39)
            .dropLast(6)
            .replace(" \n", "\n")  // remove right space
            .replace("\n ", "\n") // remove left space

        singers = document.select("span[itemprop=byArtist name]").text()
        lyricists = document.select("a[itemprop=lyricist]").text()
        composers = document.select("a[itemprop=composer]").text()

        val meta = document.select("p[class=ms-2 ms-md-3 detail mb-0]")
            .toString().split("<br>")

        for (item in meta) {
            if (item.contains("<")){
                continue
            }
            Log.v("Meta", item)

            when {
                item.contains("発売日") -> {
                    try {
                        releaseDate = item.trim().drop(4).replace("\n","")
                        Log.v("Date", releaseDate)

                    } catch (e: IOException){}
                }
                else -> {
                    try {
                        studio = item.dropLast(1)
                    } catch (e: IOException){}
                }
            }
        }
        return arrayOf( title, "${lyrics}${temp}", singers, lyricists, composers, releaseDate, number, studio )
    }
}