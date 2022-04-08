package com.aravind.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.aravind.workoutapp.databinding.ActivityExcerciseBinding

class ExcerciseActivity : AppCompatActivity() {

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
            onBackPressed()
        }
        setupResetView()
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

        setExerciseProgressBar()
    }

    private fun setExerciseProgressBar(){
        binding?.exerciseProgressBar?.progress = exerciseRestProgress

        binding?.tvExerciseName?.text = exerciseList[currentPosition]?.name
        binding?.imageView?.setImageResource(exerciseList[currentPosition].image)

        exerciseRestTimer = object : CountDownTimer(30000,1000){
            override fun onTick(milliSecond: Long) {
                exerciseRestProgress++
                binding?.exerciseProgressBar?.progress = 30 - exerciseRestProgress
                binding?.exerciseTvTimer?.text = (30 - exerciseRestProgress).toString()
            }

            override fun onFinish() {
                if (currentPosition < exerciseList?.size -1 ){
                    setupResetView()
                }else{
                    Toast.makeText(this@ExcerciseActivity,"Congrats!!! \nCompleted all exercise",Toast.LENGTH_LONG).show()
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

    override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }
        binding = null
    }
}