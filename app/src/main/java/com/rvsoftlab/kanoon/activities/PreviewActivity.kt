package com.rvsoftlab.kanoon.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.rvsoftlab.kanoon.R
import com.rvsoftlab.kanoon.models.ResultHolder
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "New Post"
        populateFields()
    }

    private fun populateFields() {
        val jpeg:ByteArray? = ResultHolder.getImage()
        if (jpeg != null) {
            val bitmap:Bitmap =BitmapFactory.decodeByteArray(jpeg,0,jpeg.size)
            preview_image.setImageBitmap(bitmap)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_share,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            R.id.action_share -> {
                val i = Intent()
                i.putExtra("caption", preview_caption.text.toString())
                setResult(Activity.RESULT_OK, i)
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
