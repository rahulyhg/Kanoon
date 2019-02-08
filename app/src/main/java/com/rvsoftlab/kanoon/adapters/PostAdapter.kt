package com.rvsoftlab.kanoon.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.rvsoftlab.kanoon.R
import com.rvsoftlab.kanoon.helper.Constants
import com.rvsoftlab.kanoon.helper.Helper
import com.rvsoftlab.kanoon.models.Posts
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val context: Context, private val mList: ArrayList<Posts>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class TextViewHolder(view:View): RecyclerView.ViewHolder(view){
        val postUserName:TextView = view.findViewById(R.id.postUserName)
        val postUserImage: CircleImageView = view.findViewById(R.id.postUserImage)
        val postCaption:TextView = view.findViewById(R.id.postCaption)
        val postTime:TextView = view.findViewById(R.id.postTimeStamp)
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val postUserName:TextView = view.findViewById(R.id.postUserName)
        val postUserImage: CircleImageView = view.findViewById(R.id.postUserImage)
        val postCaption:TextView = view.findViewById(R.id.postCaption)
        val postTime:TextView = view.findViewById(R.id.postTimeStamp)
        val postImage:ImageView = view.findViewById(R.id.postImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            Constants.POST_TYPE.TEXT->{
                val view:View = LayoutInflater.from(context).inflate(R.layout.list_item_post_text_layout,parent,false)
                TextViewHolder(view)
            }
            Constants.POST_TYPE.IMAGE->{
                val view:View = LayoutInflater.from(context).inflate(R.layout.list_item_post_image_layout,parent,false)
                ImageViewHolder(view)
            }
            else->{
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            Constants.POST_TYPE.TEXT->bindTextPost(holder,position)
            Constants.POST_TYPE.IMAGE->bindImagePost(holder,position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mList[position].postType
    }


    private fun bindTextPost(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as TextViewHolder
        vh.postUserName.text = "Anonymous"
        vh.postCaption.text = mList[position].postText
        //vh.postTime.text = mList[position].addedDateTime
        if (mList[position].isAnonymous) {
            vh.postUserImage.setImageDrawable(context.getDrawable(R.mipmap.ic_incognito))
        }else if (Helper.isNullOrEmpty(mList[position].userImage)){
            vh.postUserImage.setImageDrawable(context.getDrawable(R.mipmap.ic_user))
        }
    }

    private fun bindImagePost(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as ImageViewHolder
        vh.postUserName.text = "Anonymous"
        vh.postCaption.text = mList[position].postText
        Glide.with(context)
                .load(mList[position].postImagePath)
                .into(vh.postImage)

    }

    public fun addAll(list:ArrayList<Posts>) {
        val initialSize = mList.size
        mList.addAll(0,list)
        notifyItemRangeChanged(initialSize,mList.size)
    }
}