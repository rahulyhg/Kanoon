package com.rvsoftlab.kanoon.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.support.v4.view.animation.PathInterpolatorCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import com.dewarder.camerabutton.CameraButton
import com.google.firebase.firestore.FirebaseFirestore
import com.rvsoftlab.kanoon.R
import com.rvsoftlab.kanoon.adapters.PostAdapter
import com.rvsoftlab.kanoon.helper.Constants
import com.rvsoftlab.kanoon.models.Posts
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.content_main_page.*

class MainPageActivity : AppBaseActivity() {
    private val TAG = MainPageActivity::class.simpleName
    private val DURATION_TRANSITION_MS: Long = 400
    private val ANIMATION_TRANSLATION_DURATION = 200L
    private var isOpen:Boolean = false
    private var isTextMode:Boolean = false
    private var listArray:ArrayList<Posts> = ArrayList()
    private lateinit var adapter:PostAdapter

    private val db:FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.hide()

        camera_preview.visibility = GONE

        initViewListeners()
        initRecyclerView()
    }

    private fun initViewListeners() {
        btnSendCamera.setOnClickListener {
            if (!isTextMode)changeContentVisibility()
            else createTextPost()
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
        cameraCaptureButton.setOnTapEventListener {

        }
        cameraCaptureButton.setOnStateChangeListener {
            if (it == CameraButton.State.START_EXPANDING) {
                translateToLeft(gallerySwitch,false)
                translateToRight(cameraSwitch,false)
            }else if (it == CameraButton.State.START_COLLAPSING) {
                translateToLeft(gallerySwitch,true)
                translateToRight(cameraSwitch,true)
            }
        }

    }

    private fun initRecyclerView() {
        adapter = PostAdapter(this,listArray)
        postList.adapter = adapter
    }


    private fun createTextPost() {
        val post = Posts()
        post.postText = postText.text.toString()
        post.postLikeCount = 0
        post.postCommentCount = 0
        listArray.add(0,post)
        adapter.notifyDataSetChanged()
        postText.setText("")
        hideKeyboard()
        postToServer(post)
    }

    private fun postToServer(post: Posts) {
        db.collection(Constants.FIRESTORE_NODES.POSTS)
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
                .setInterpolator(AccelerateInterpolator())
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

    private fun hideKeyboard() {
        val inputManager:InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(this.currentFocus.windowToken,HIDE_NOT_ALWAYS)
    }
}
