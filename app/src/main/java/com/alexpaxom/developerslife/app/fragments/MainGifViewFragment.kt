package com.alexpaxom.developerslife.app.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.alexpaxom.developerslife.app.presenters.MainGifViewPresenter
import com.alexpaxom.developerslife.app.views.MainGifView
import com.alexpaxom.developerslife.databinding.FragmentMainGifViewBinding
import com.bumptech.glide.Glide
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import androidx.core.view.isVisible
import com.alexpaxom.developerslife.R
import com.alexpaxom.developerslife.data.models.GifInfo
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener

import com.bumptech.glide.request.target.Target


class MainGifViewFragment : MvpAppCompatFragment(), MainGifView {
    private var _binding: FragmentMainGifViewBinding? = null
    private val binding get() = _binding!!
    private var navController: NavController? = null

    @InjectPresenter
    lateinit var presenter: MainGifViewPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainGifViewBinding.inflate(inflater, container, false)

        if(savedInstanceState == null) {
            navController = NavHostFragment.findNavController(this);
            setStatePrevButton(false)
            presenter.loadNextImage()
        }
        else
            btnRefreshGifClick()

        binding.btnNextGif.setOnClickListener { btnNextGifClick() }
        binding.btnPrevGif.setOnClickListener { btnPrevGifClick() }
        binding.btnRefreshGif.setOnClickListener { btnRefreshGifClick() }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun setImage(gifInfo: GifInfo) {
        setStatePrevButton(presenter.hasPrevImages())
        showLoad()
        Glide.with(this)
            .asGif()
            .load(gifInfo.gifURL)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .listener(LoadImageListener())
            .into(binding.gifView)

        binding.txtGifDescription.text = gifInfo.description
    }

    override fun showLoad() {
        binding.imageLoadBar.isVisible = true
        binding.gifConteinerLayout.isVisible = true
        binding.errorLayout.isVisible = false
    }

    override fun showError() {
        binding.imageLoadBar.isVisible = false
        binding.gifConteinerLayout.isVisible = false
        binding.errorLayout.isVisible = true
    }

    override fun setStatePrevButton(state: Boolean) {
        binding.btnPrevGif.isEnabled = state
        binding.btnPrevGif.backgroundTintList = ColorStateList.valueOf(
            requireActivity().resources.getColor(
                if(state) R.color.nav_buttons else R.color.nav_buttons_not_active
            ))
    }

    fun btnNextGifClick() {
        presenter.loadNextImage()
    }

    fun btnPrevGifClick() {
        presenter.loadPrevImage()
    }

    fun btnRefreshGifClick() {
        presenter.refreshImage()
    }

    private inner class LoadImageListener: RequestListener<GifDrawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<GifDrawable>?,
            isFirstResource: Boolean
        ): Boolean {
            showError()
            return true
        }

        override fun onResourceReady(
            resource: GifDrawable?,
            model: Any?,
            target: Target<GifDrawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            binding.imageLoadBar.isVisible = false
            return false
        }
    }
}