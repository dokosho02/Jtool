package com.example.jtool

data class BookList(
    val sid: String?,
    val id: String?,

    val title: String?,
    val authors: String?,

    val cover: String,
    val url: String?,
    val webreader: Int?,

    val price: Float?,
    val old_price: Float?,
    val new_price: Float?
){

    fun getURL(): String {
        val duokanMain = "https://www.duokan.com"
        return "$duokanMain$url"
    }
}