package com.kontranik.koreader.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord

class SharedViewModelTypefaceRecord : ViewModel() {
    val selected = MutableLiveData<TypefaceRecord>()

    fun select(item: TypefaceRecord) {
        selected.value = item
    }
}

