package com.aravind.workoutapp

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aravind.workoutapp.databinding.ActivityExcerciseBinding
import com.aravind.workoutapp.databinding.DialogCustomBackBinding
import java.util.*

class ExcerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding : ActivityExcerciseBinding? = null

    // Sample CountDown Timer
    private var countDownTimer : CountDownTimer? = null
    private var timeDuration : Long = 60000
    private var pauseOffset : Long = 0

    // CountDown Timer
    private var restTimer : CountDownTimer? = null
    private var restProgress = 0

    // Exercise CountDown Timer
    private var exerciseRestTimer : CountDownTimer? = null
    private var exerciseRestProgress = 0

    //List
    var exerciseList = arrayListOf<ExerciseModel>()
    var currentPosition = -1

    //TextToSpeech
    var textToSpeech : TextToSpeech? = null

    //Start Sound
    var player : MediaPlayer? = null

    //Recyclerview Adapter
    private var adapter : ExerciseAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExcerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        exerciseList = Constants.defaultExerciseList()
        setSupportActionBar(binding?.toolbarExercise)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener{
//            onBackPressed()
            customBackDialog()
        }
        setupResetView()

        textToSpeech = TextToSpeech(this,this)

        setRecyclerviewExercise()
    }

    private fun setRecyclerviewExercise(){
        adapter = ExerciseAdapter(exerciseList)
        binding?.exerciseRecyclerview?.layoutManager =
            LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding?.exerciseRecyclerview?.adapter = adapter
    }
    private fun setupResetView(){

        binding?.flExerciseFrameLayout?.visibility = View.INVISIBLE
        binding?.imageView?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flRestview?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseLabel?.visibility = View.VISIBLE

        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }
        binding?.tvUpcomingExerciseName?.text = exerciseList[currentPosition + 1].name

        try {
            val soundUri = Uri.parse("android.resource:" +
                    "//com.aravind.workoutapp/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, soundUri)
            player?.isLooping = false
            player?.start()
        }catch (e : Exception){
            e.printStackTrace()
        }
        setProgressBar()
    }

    private fun setProgressBar(){
        binding?.progressBar?.progress = restProgress
        currentPosition++
        restTimer = object : CountDownTimer(10000,1000){
            override fun onTick(milliSecond: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()

            }
            override fun onFinish() {

                exerciseList[currentPosition]?.isSelected = true
                adapter?.notifyDataSetChanged()

               setupExerciseView()
            }

        }.start()
    }

    private fun setupExerciseView(){
        binding?.flExerciseFrameLayout?.visibility = View.VISIBLE
        binding?.imageView?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flRestview?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseLabel?.visibility = View.INVISIBLE

        if (exerciseRestTimer != null){
            exerciseRestTimer?.cancel()
            exerciseRestProgress = 0
        }

        binding?.tvExerciseName?.text = exerciseList[currentPosition]?.name
        binding?.imageView?.setImageResource(exerciseList[currentPosition].image)
        speakOut(exerciseList[currentPosition].name)
        setExerciseProgressBar()
    }

    private fun setExerciseProgressBar(){
        binding?.exerciseProgressBar?.progress = exerciseRestProgress


        exerciseRestTimer = object : CountDownTimer(30000,1000){
            override fun onTick(milliSecond: Long) {
                exerciseRestProgress++
                binding?.exerciseProgressBar?.progress = 30 - exerciseRestProgress
                binding?.exerciseTvTimer?.text = (30 - exerciseRestProgress).toString()
            }

            override fun onFinish() {

                if (currentPosition < exerciseList?.size -1 ){
                    setupResetView()
                    exerciseList[currentPosition]?.isSelected = false
                    exerciseList[currentPosition]?.isCompleted = true
                    adapter?.notifyDataSetChanged()

                }else{
                    speakOut("Congrats You Completed all exercise")
                    finish()
                    val intent = Intent(this@ExcerciseActivity,FinishActivity::class.java)
                    startActivity(intent)
                }
            }

        }.start()
    }


    fun startTimer(view : View){
        countDownTimer = object : CountDownTimer(timeDuration - pauseOffset,1000){
            override fun onTick(timemillisUntilFinished: Long) {
                pauseOffset = timeDuration - timemillisUntilFinished
                binding?.tvTimer?.text = (timemillisUntilFinished/1000).toString()
            }

            override fun onFinish() {
                binding?.tvTimer?.text = "Finished"
            }
        }.start()
    }
    fun stopTimer(view : View){
        if (countDownTimer != null){
            countDownTimer?.cancel()
        }
    }
    fun resetTimer(view : View) {
        if (countDownTimer != null){
            countDownTimer?.cancel()
            binding?.tvTimer?.text = "${timeDuration/1000}"
            pauseOffset = 0
            countDownTimer = null
        }
    }


    private fun customBackDialog(){
        val dialog = Dialog(this)
        val dialogBinding = DialogCustomBackBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.tvYes.setOnClickListener {
            this.finish()
            dialog.dismiss()
        }
        dialogBinding.tvNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = textToSpeech?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("NOO","LANG NOT SUPPORT")
            }
        }else{
            Log.e("NOO","LANG INIT ERROR")
        }
    }

    private fun speakOut(text : String){
        textToSpeech?.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        customBackDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }
        if (textToSpeech != null){
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
        if (player != null){
            player?.stop()
        }
        binding = null
    }
}