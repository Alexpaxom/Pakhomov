package com.alexpaxom.developerslife.app.views

import com.alexpaxom.developerslife.data.models.GifInfo
import com.bumptech.glide.Glide
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface MainGifView: MvpView {
    fun setImage(gif: GifInfo)
    fun showLoad()
    fun showError()
    fun setStatePrevButton(state: Boolean)
}
