package com.example.jtool

import android.util.Log
import kotlin.properties.Delegates


open class Single {
    open var title       by Delegates.notNull<String>()
    open var composers   = ""
    open var lyricists   = ""
    open var arrangers   = ""
    open var singers     = ""
    open var releaseDate = ""
    open var studio      = ""
    open var number      = ""

    open var searchEngine by Delegates.notNull<String>()

    private val letraStrucuture = "[Verse]\n[Pre-chorus]\n[Chorus]\n[Interlude]\n[Bridge]\n[Chorus-variation]\n[End]\n×\n\n"
    open var lyrics = letraStrucuture

    var infoList = arrayListOf<Array<Any>>()
    lateinit var elementList: Array<Any>

    fun ShowInfo(){
        val s = 'S'.toString()
        Log.v(s, title)
        Log.v(s, singers)
        Log.v(s, releaseDate)
        Log.v(s, composers)
        Log.v(s, lyricists)
        Log.v(s, lyrics)
        Log.v(s, searchEngine)
    }

}