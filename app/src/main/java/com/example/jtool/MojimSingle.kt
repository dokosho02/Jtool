package com.example.jtool

import android.util.Log
import org.jsoup.Jsoup

val graphicSpace = "\u3000"


class MojimSingle: Single() {
    override var searchEngine = "https://mojim.com"
    // ---------------------------------
    fun searchSong(titleKeyword:String) : ArrayList<Array<Any>> {
        // https://mojim.com/%E4%B8%8D%E8%83%BD%E6%94%BE%E6%89%8B.html?t3
        val searchLink = "${searchEngine}/${titleKeyword}.html?t3"
        val document = Jsoup.connect(searchLink).get()
        val resultTable = document.select("dd")

        for ( i in resultTable.indices.drop(1) ) {
            if (resultTable[i].toString().contains("<p")){ continue }

            val count       = resultTable[i].select("span[class=mxsh_ss1]").text()
            val artist      = resultTable[i].select("span[class=mxsh_ss2]").text()
            val albumInfo   = resultTable[i].select("span[class=mxsh_ss3]").text()
            val songTitle   = resultTable[i].select("span[class=mxsh_ss4]").text().split(".")[1]
            val songLink    = resultTable[i].select("span[class=mxsh_ss4]").select("a[href]").attr("href")
            val releaseDate = resultTable[i].select("span[class=mxsh_ss5]").text()

            elementList = arrayOf(count, songTitle, artist, albumInfo, releaseDate, songLink)
            infoList.add(elementList)
        }
        return infoList
    }
    // ---------------------------------
    fun scrapeLyrics(songRelativeLink: String) : Array<Any> {
        val songLink = "${searchEngine}${songRelativeLink}"    // https://mojim.com/twy100019x21x8.htm
        Log.v("Chinese Link", songLink)
        val document = Jsoup.connect(songLink).get()

        title = document.getElementsByTag("title").first()!!.text().split(" 歌詞")[0]
        val kashi = document.getElementById("fsZx1")

        val ad0 = "<br>更多更詳盡歌詞 在 ※ Mojim.com 魔鏡歌詞網"    // more lyrics at Mojim.com
        //val ad1 = """\u66F4\u591A\u66F4\u8A73\u76E1\u6B4C\u8A5E \u5728 <a href="http://mojim.com">\u203B Mojim.com\u3000\u9B54\u93E1\u6B4C\u8A5E\u7DB2 </a><br/>"""   // more lyrics at Mojim.com

        val ad3 = "<dl.*> ".toRegex()
        val ad4 = "<dt.*</dt>".toRegex()
        val ad5 = "<ol>.*</dl>".toRegex()

        var temp = kashi.toString()
//            .replace(" ", "")
            .replace("\n", " ")
            .replace(graphicSpace, " ")
            .replace(ad0, "")
            .replace("<br>", "\n")

        for (i in 1..5) {
            temp = temp.replace(" \n", "\n")  // remove right space
                .replace("\n ", "\n") // remove left space
        }

        for (item in listOf(ad3, ad4, ad5) ){
            temp = item.replace(temp, "")
        }
        temp = replaceKanji(temp)
        temp = replaceMetaData(temp)

        Log.v("Mojim Scrap", title )
        Log.v("Mojim Scrap", temp )

        number = songRelativeLink.split('.')[0].drop(4)

        return arrayOf( title, "${lyrics}${temp}", singers, lyricists, composers, releaseDate, number, studio )
    }
}

// --------------------------------------------------
fun replaceKanji(kanjiString: String) : String {
    var kanjiLiteral = kanjiString

    val kanjiVariants = ArrayList<Array<String>>()
    kanjiVariants.add(arrayOf("線", "綫") )
    kanjiVariants.add(arrayOf("啟", "啓") )
    kanjiVariants.add(arrayOf("躲", "躱") )
    kanjiVariants.add(arrayOf("裡", "裏") )
    kanjiVariants.add(arrayOf("為", "爲") )
    kanjiVariants.add(arrayOf("真", "眞") )
    kanjiVariants.add(arrayOf("偽", "僞") )

    // replace the kanji variants
    for (i in kanjiVariants.indices ) {
        kanjiLiteral = kanjiLiteral.replace( kanjiVariants[i][0],kanjiVariants[i][1] )
    }
    return kanjiLiteral
}

fun replaceMetaData(kanjiString: String) : String {
    var kanjiLiteral = kanjiString

    val kanjiVariants = ArrayList<Array<String>>()
    kanjiVariants.add(arrayOf("作詞：", "Lyrics by ") )
    kanjiVariants.add(arrayOf("作曲：", "Composed by ") )
    kanjiVariants.add(arrayOf("編曲：", "Arranged by ") )
    kanjiVariants.add(arrayOf("監製：", "Produced by ") )
    kanjiVariants.add(arrayOf("演唱：", "Performed by ") )

    kanjiVariants.add(arrayOf("&lt;", "「") )
    kanjiVariants.add(arrayOf("2&gt;", "」") )

    // replace the kanji variants
    for (i in kanjiVariants.indices ) {
        kanjiLiteral = kanjiLiteral.replace( kanjiVariants[i][0],kanjiVariants[i][1] )
    }
    return kanjiLiteral
}



// https://stackoverflow.com/questions/58247830/how-to-store-2d-array-in-android-shared-preference-in-kotlin

