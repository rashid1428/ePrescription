package com.doctor.eprescription.extension

import android.os.Bundle
import android.os.Parcelable
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

fun <A> String.fromJson(type: Class<A>): A {
    return Gson().fromJson(this, type)
}

fun <A> A.toJson(): String {
    return try {
        Gson().toJson(this)
    } catch (ex: Exception) {
        ""
    }
}

fun <T> Gson.fromJsonSafe(json: String, klass: Class<T>): T? {
    return try {
        this.fromJson(json, klass)
    } catch (ex: Exception) {
        null
    }
}

fun generateRandomString(): String {
    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val length = 10
    return (1..length).asSequence()
        .map {
            val randomInt = Random.nextInt(0, charPool.size)
            charPool[randomInt]
        }
        .joinToString("")
}

fun Long.millisToDate(format: String = "dd/MM/yyyy"): String {
    return try {
        val sdf = SimpleDateFormat(format, Locale.US)
        sdf.format(Date(this))
    } catch (ex: Exception) {
        ""
    }
}


inline fun <reified T : Parcelable> Bundle.putParcelableList(key: String, list: List<T>) {
    val parcelableList: ArrayList<out Parcelable> = ArrayList(list)
    putParcelableArrayList(key, parcelableList)
}