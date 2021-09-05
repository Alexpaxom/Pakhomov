package com.alexpaxom.developerslife.app.presenters

import android.util.Log
import com.alexpaxom.developerslife.app.views.MainGifView
import com.alexpaxom.developerslife.data.models.GifInfo
import com.alexpaxom.developerslife.data.remote.DevelopersLifeGifRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

@InjectViewState
class MainGifViewPresenter: MvpPresenter<MainGifView>() {
    private var developersLifeGifRequest: DevelopersLifeGifRequest? = null
    private val imageStackCache = ImageStackCache()
    private val disposable = CompositeDisposable()


    init {
        developersLifeGifRequest = initRemoteImageApi()
    }

    private fun initRemoteImageApi():DevelopersLifeGifRequest {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://developerslife.ru/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(DevelopersLifeGifRequest::class.java)
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    fun loadNextImage() {
        if(imageStackCache.hasNext())
            viewState.setImage(imageStackCache.getNext())
        else {
            disposable.add( developersLifeGifRequest?.getRandom()!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.gifURL != null) {
                        imageStackCache.addLast(it)
                        viewState.setImage(imageStackCache.getNext())
                    }
                }, {
                    Log.e("ERROR", "HERE")
                    viewState.showError()
                }
                ))
        }
    }

    fun refreshImage() {
        if(imageStackCache.isEmpty())
            loadNextImage()
        else
            viewState.setImage(imageStackCache.getCurrent())
    }

    fun loadPrevImage() {
        viewState.setImage(imageStackCache.getPrevious())
    }

    fun hasPrevImages():Boolean {
        return imageStackCache.hasPrevious()
    }
}

class ImageStackCache {
    private val prevImageStack: LinkedList<GifInfo> = LinkedList<GifInfo>()
    private val nextImageStack: LinkedList<GifInfo> = LinkedList<GifInfo>()
    private var currentElement = GifInfo()

    fun addLast(e: GifInfo) {
        if(currentElement.id != null)
            nextImageStack.addLast(e)
        else
            currentElement = e

    }

    fun getCurrent(): GifInfo {
        return currentElement
    }

    fun getPrevious(): GifInfo {
        if(hasPrevious()) {
            nextImageStack.addLast(currentElement)
            currentElement = prevImageStack.pollLast()
        }
        return currentElement
    }

    fun getNext(): GifInfo {
        if(hasNext()) {
            prevImageStack.addLast(currentElement)
            currentElement = nextImageStack.pollLast()
        }
        return currentElement
    }

    fun hasPrevious(): Boolean {
        if(prevImageStack.isEmpty())
            return false
        return true
    }

    fun hasNext(): Boolean {
        if(nextImageStack.isEmpty())
            return false
        return true
    }

    fun isEmpty(): Boolean {
        return currentElement.id == null && prevImageStack.isEmpty() && nextImageStack.isEmpty()
    }

}


