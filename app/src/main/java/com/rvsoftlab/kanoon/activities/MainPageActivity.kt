package com.rvsoftlab.kanoon.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.support.v4.view.animation.PathInterpolatorCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import com.rvsoftlab.kanoon.R
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.content_main_page.*

class MainPageActivity : AppBaseActivity() {
    private val TAG = MainPageActivity::class.simpleName
    private val DURATION_TRANSITION_MS: Long = 400
    private var isOpen:Boolean = false
    private var isTextMode:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.hide()
        camera_preview.visibility = GONE

        btnSendCamera.setOnClickListener {
            if (!isTextMode)changeContentVisibility()
        }



        postText.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                homeCharProgress?.progress = postText.length()
                if (postText.length() > 0 && !isTextMode) {
                    btnSendCamera.morph()
                    isTextMode = true
                }else if (postText.length()== 0){
                    btnSendCamera.morph()
                    isTextMode = false
                }
            }

        })
    }

    private fun changeContentVisibility() {
        var targetTranslation = 0
        var contTranslation = 0
        val interpolator = PathInterpolatorCompat.create(0.790f, -0.130f, 0.205f, 1.160f)

        if (!isOpen) {
            targetTranslation = bottom_content.height + 100
            contTranslation = upper_content.height + 100
            isOpen = true
            supportActionBar?.show()
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)


            if (!camera_preview.isStarted) camera_preview.start()
            camera_preview.alpha = 0f
            camera_preview.visibility = VISIBLE
            camera_preview.animate()
                    .alpha(1f)
                    .setDuration(DURATION_TRANSITION_MS)
                    .setListener(object : AnimatorListenerAdapter(){
                        override fun onAnimationEnd(animation: Animator?) {

                        }
                    })
        }else{
            isOpen = false
            supportActionBar?.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

            camera_preview.alpha = 1f
            camera_preview.animate()
                    .alpha(0f)
                    .setDuration(DURATION_TRANSITION_MS)
                    .setListener(object : AnimatorListenerAdapter(){
                        override fun onAnimationEnd(animation: Animator?) {
                            camera_preview.visibility = GONE
                            if (camera_preview.isStarted) camera_preview.stop()
                        }
                    })
        }



        bottom_content.animate().cancel()
        bottom_content.animate()
                .translationY(targetTranslation.toFloat())
                .setInterpolator(interpolator)
                .setDuration(DURATION_TRANSITION_MS)
                .start()

        upper_content.animate().cancel()
        upper_content.animate()
                .translationY((-(contTranslation)).toFloat())
                .setInterpolator(interpolator)
                .setDuration(DURATION_TRANSITION_MS)
                .start()

    }

    override fun onBackPressed() {
        if (isOpen) {
            changeContentVisibility()
        }else{
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                changeContentVisibility()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
