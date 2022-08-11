package com.example.jtool

import android.util.Log
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder




fun processReStr(str: String): String {
    var str2 = str.split(":")[1].drop(1).dropLast(2)
    if (str2 == "ul") {str2 = "null"}
    return str2
}

class GeniusSingle: Single() {
    override var searchEngine = "https://genius.com" //"https://genius.com"
    private val apiURL = "https://api.genius.com"
    private val client_access_token = "2srIef_PLP3IJmAUhIU6E8bTsD1iJcHjXfqwviG2CsNQQEV_1lYa9S2yX4F9J8yJ"

    fun searchSong(titleKeyword:String) : ArrayList<Array<Any>> {
        val encodeKeyword = URLEncoder.encode(titleKeyword, "utf-8")
        val queryURL = "${apiURL}/search?q=${encodeKeyword}"
        println(queryURL)

        val response: String = getHttpsResponse(queryURL, client_access_token)

        val pathList:  MutableList<String> = mutableListOf()
        val titleList: MutableList<String> = mutableListOf()
        val artiList:  MutableList<String> = mutableListOf()
        val dateList:  MutableList<String> = mutableListOf()


        val pathRe = "\"path\"(.*?)\",".toRegex()  //
        val pathSearches = pathRe.findAll(response)
        pathSearches.forEach {
            println(it.value)
            pathList.add( processReStr(it.value) )
        }

        val titleRe = "\"title\"(.*?)\",".toRegex()  //
        val titleSearches = titleRe.findAll(response)
        titleSearches.forEach {
            println(it.value)
            titleList.add( processReStr(it.value) )
        }

        val artiRe = "\"artist_names\"(.*?)\",".toRegex()  //
        val artiSearches = artiRe.findAll(response)
        artiSearches.forEach {
            println(it.value)
            artiList.add( processReStr(it.value) )
        }

        val dateRe = "\"release_date_for_display\"(.*?)(\",|null,)".toRegex()  //
        val dateSearches = dateRe.findAll(response)
        dateSearches.forEach {
            println(it.value)
            dateList.add( processReStr(it.value) )
        }

        for ( i in pathList.indices ) {

            val count = (i+1).toString()
            val artist = artiList[i]
            val songTitle = titleList[i]
            val songLink = pathList[i]
            val releaseDate = dateList[i]

            elementList = arrayOf(count, songTitle, artist, releaseDate, songLink)
            infoList.add(elementList)
        }
        return infoList
    }

    fun scrapeLyrics(songRelativeLink: String) : Array<Any> {
        val songLink = "${searchEngine}${songRelativeLink}"    //
        println(songLink)
        val response = getHttpsResponse(songLink, client_access_token)  // html text
            .replace("<br/>", "$-")
        val document = Jsoup.parse(response)

        title = document.getElementsByTag("h2").text().dropLast(7)
        println(title)

        val kashi = document.select("div[class*=Lyrics__Container]").text()
            .replace("$-", "\n")
        println(kashi)

        val about = document.select("div[class*=SongDescription__Content]").text()
            .replace("$-", "\n")
        println(about)
        val credits = document.select("div[class*=SongInfo__Columns]").text()
            .replace("$-", "\n")
        println(credits)


        Log.v("Genius Scrap", title )

        number = songRelativeLink.split('.')[0].drop(4)

        return arrayOf( title, "${lyrics}${kashi}\n\n\n${about}\n\n\n${credits}", singers, lyricists, composers, releaseDate, number, studio )
    }
}

// ------------------------
fun getHttpsResponse(queryURL: String, token: String): String {
    val url = URL(queryURL)
    val connection = url.openConnection()
    connection.setRequestProperty("Authorization", "Bearer ${token}")

    val response = StringBuffer()
    BufferedReader(
        InputStreamReader(connection.getInputStream())
    ).use {
        var inputLine = it.readLine()
        while (inputLine != null) {
            response.append(inputLine)
            inputLine = it.readLine()
        }
        it.close()
    }

    return response.toString()
}
// https://stackoverflow.com/questions/58247830/how-to-store-2d-array-in-android-shared-preference-in-kotlin