package com.aravind.workoutapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aravind.workoutapp.databinding.ExerciseStatusItemBinding

class ExerciseAdapter(private val listItems : List<ExerciseModel>) :
 RecyclerView.Adapter<ExerciseAdapter.ViewHolder>(){

    inner class ViewHolder(binding : ExerciseStatusItemBinding) :
        RecyclerView.ViewHolder(binding.root){
            val tvItem : TextView = binding.tvItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseAdapter.ViewHolder {
        return ViewHolder(ExerciseStatusItemBinding.inflate(LayoutInflater.from(parent.context),
                    parent,false))
    }

    override fun onBindViewHolder(holder: ExerciseAdapter.ViewHolder, position: Int) {
        val model : ExerciseModel = listItems[position]
        holder.tvItem.text = model.id.toString()

        when{
            model.isSelected -> {
                holder.tvItem.background = ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.item_circular_color_white_background
                )
                holder.tvItem.setTextColor(Color.parseColor("#212121"))
            }
            model.isCompleted -> {
                holder.tvItem.background = ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.item_circular_color_accent_background
                )
                holder.tvItem.setTextColor(Color.parseColor("#FFFFFF"))
            }
            else -> {
                holder.tvItem.background = ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.text_circular_background
                )
                holder.tvItem.setTextColor(Color.parseColor("#212121"))
            }
        }
    }

    override fun getItemCount(): Int = listItems.size
}