package com.rvsoftlab.kanoon.activities

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.view.animation.PathInterpolatorCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.widget.Toast
import com.dewarder.camerabutton.CameraButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.otaliastudios.cameraview.CameraListener
import com.rvsoftlab.kanoon.R
import com.rvsoftlab.kanoon.adapters.PostAdapter
import com.rvsoftlab.kanoon.helper.Constants
import com.rvsoftlab.kanoon.helper.PermissionUtil
import com.rvsoftlab.kanoon.helper.RealmHelper
import com.rvsoftlab.kanoon.helper.convertToByteArray
import com.rvsoftlab.kanoon.models.Posts
import com.rvsoftlab.kanoon.models.ResultHolder
import com.rvsoftlab.kanoon.models.User
import com.zxy.tiny.Tiny
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.content_main_page.*
import java.util.*


class MainPageActivity : AppBaseActivity() {
    private val TAG = MainPageActivity::class.simpleName
    private val DURATION_TRANSITION_MS: Long = 400
    private val ANIMATION_TRANSLATION_DURATION = 200L
    private val REQUEST_IMAGE_POST_SHARE = 100
    private var isOpen:Boolean = false
    private var isTextMode:Boolean = false
    private var listArray:ArrayList<Posts> = ArrayList()
    private lateinit var adapter:PostAdapter
    private lateinit var permission:PermissionUtil

    private val db:FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage:StorageReference = FirebaseStorage.getInstance().reference
    private lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.hide()
        permission = PermissionUtil(this)

        user = RealmHelper.with(this).getUser()

        camera_preview.visibility = GONE
        cameraCaptureButton.mode = CameraButton.Mode.TAP
        initViewListeners()
        initRecyclerView()
        getAllPosts()
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
            permission.checkAndAskPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,Constants.PERMISSION.STORAGE,object:PermissionUtil.PermissionAskListener{
                override fun onPermissionGranted() {
                    captureImage()
                }

                override fun onPermissionDenied() {
                    Toast.makeText(this@MainPageActivity,"Please Grand Access to Capture Image",Toast.LENGTH_SHORT).show()
                }

            })
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

    private fun captureImage() {
        camera_preview.addCameraListener(object : CameraListener(){
            override fun onPictureTaken(jpeg: ByteArray?) {
                super.onPictureTaken(jpeg)
                camera_preview.stop()
                if (jpeg != null) {
                    var option = Tiny.BatchFileCompressOptions()
                    option.config =  Bitmap.Config.ARGB_8888
                    Tiny.getInstance().source(jpeg).asBitmap().withOptions(option).compress { isSuccess, bitmap, t ->
                        if (isSuccess) {
                            var img:ByteArray = bitmap.convertToByteArray()
                            ResultHolder.dispose()
                            ResultHolder.setImage(img)
                            ResultHolder.setNativeCaptureSize(camera_preview.captureSize)


                            val i = Intent(this@MainPageActivity,PreviewActivity::class.java)
                            startActivityForResult(i,REQUEST_IMAGE_POST_SHARE)
                            changeContentVisibility()
                        }
                    }



                }
                //ResultHolder.setTimeToCallback(callbackTime - captureStartTime)

            }
        })
        camera_preview.capturePicture()
    }

    private fun initRecyclerView() {
        adapter = PostAdapter(this,listArray)
        postList.adapter = adapter
    }

    private fun getAllPosts() {
        db.collection(Constants.FIRESTORE_NODES.POSTS)
                .get()
                .addOnCompleteListener {task->
                    if (task.isSuccessful) {
                        val list:ArrayList<Posts> = ArrayList()
                        for (document in task.result!!) {
                            Log.d(TAG, document.id + " => " + document.data)
                            val data:Posts = document.toObject(Posts::class.java)
                            data.uuid = document.id
                            list.add(data)
                        }
                        adapter.addAll(list)
                    }
                }
    }


    private fun createTextPost() {
        val post = Posts()
        post.postType = Constants.POST_TYPE.TEXT
        post.postText = postText.text.toString()
        post.postLikeCount = 0
        post.postCommentCount = 0
        post.addedBy = user.mobile

        postText.setText("")
        hideKeyboard()
        postToServer(post)
    }

    private fun postToServer(post: Posts) {
        /*db.collection(Constants.FIRESTORE_NODES.POSTS).document().collection(user.mobile)
                .add(post)
                .addOnSuccessListener { Log.d(TAG,"DocumentSnapshot successfully written!") }
                .addOnFailureListener { e->Log.w(TAG, "Error writing document", e) }*/

        db.collection(Constants.FIRESTORE_NODES.POSTS).add(post)
                .addOnSuccessListener {
                    listArray.add(0,post)
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e->Log.w(TAG, "Error writing document", e) }

        /*db.collection(Constants.FIRESTORE_NODES.POSTS).document(user.mobile)
                .set(post)
                .addOnSuccessListener {
                    Log.d(TAG,"DocumentSnapshot successfully written!")
                }.addOnFailureListener { e->Log.w(TAG, "Error writing document", e) }*/
    }

    private fun uploadImage(caption: String) {

        storage.child("/images/${user.mobile}/${UUID.randomUUID()}.png")
                .putBytes(ResultHolder.getImage()!!)
                .addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener { it ->
                        createImagePost(caption,it.toString())
                    }
                }.addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                .addOnProgressListener {
                    val progress = 100.0 * it.bytesTransferred / it.totalByteCount
                    Log.d(TAG, "$progress")
                }
    }

    private fun createImagePost(caption: String, url: String) {
        val post = Posts()
        post.postType = Constants.POST_TYPE.IMAGE
        post.postText = caption
        post.postImagePath = url
        post.postLikeCount = 0
        post.postCommentCount = 0
        post.addedBy = user.mobile

        postText.setText("")
        hideKeyboard()
        postToServer(post)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_IMAGE_POST_SHARE -> {
                if (resultCode == Activity.RESULT_OK) {
                    var caption: String? = data!!.getStringExtra("caption")
                    if (caption != null) {
                        uploadImage(caption)
                    }
                }else if (resultCode == Activity.RESULT_CANCELED){
                    ResultHolder.dispose()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}


