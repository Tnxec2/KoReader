package com.kontranik.koreader.reader

import android.os.AsyncTask
import android.os.Build
import androidx.annotation.RequiresApi
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.Page


class LoadPageAsync(private var asyncResponse: AsyncResponse?) : AsyncTask<LoadPageParams, Unit, Page?>() {
    interface AsyncResponse {
        fun processFinish(output: Page?)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun doInBackground(vararg params: LoadPageParams?): Page? {
        val book = params[0]!!.book
        val page = params[0]!!.page
        val pageSplitter = params[0]!!.pageSplitterOne
        return if ( params[0]!!.prev)
            book!!.loadPageRevers(page!!, pageSplitter!!)
        else
            book!!.loadPage(page!!, pageSplitter!!)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        // ...
    }

    override fun onCancelled() {
        super.onCancelled()
    }

    override fun onPostExecute(result: Page?) {
        asyncResponse?.processFinish(result)
        super.onPostExecute(result)
    }
}

class LoadPageParams(
        var prev: Boolean,
        var book: Book?,
        var page: Page?,
        var pageSplitterOne: PageSplitterOne?
) {}

