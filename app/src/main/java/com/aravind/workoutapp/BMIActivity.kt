package com.aravind.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aravind.workoutapp.databinding.ActivityBmiactivityBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    companion object{
        val METRIC_UNIT = "METRIC_UNIT"
        val US_UNIT = "US_UNIT"
    }

    private var metricSelectedUnit = METRIC_UNIT

    private var binding: ActivityBmiactivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiactivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarBmiActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Calculate BMI"

        binding?.toolbarBmiActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        metricVisibleView()

        binding?.radioGroup?.setOnCheckedChangeListener { _, checked : Int ->
            if (checked == R.id.rbMetricUnits){
                metricVisibleView()
            }else{
                usMetricVisibleView()
            }
        }

        binding?.btnCalculateBmi?.setOnClickListener {

            calculateUnits()
        }
    }

    private fun metricVisibleView(){
        metricSelectedUnit = METRIC_UNIT
        binding?.llDiplayBMIResult?.visibility = View.VISIBLE
        binding?.etWeightPound?.visibility = View.INVISIBLE
        binding?.etWeight?.visibility = View.VISIBLE
        binding?.etFeet?.visibility = View.INVISIBLE
        binding?.etInch?.visibility = View.INVISIBLE

        binding?.weightEditText?.text?.clear()
        binding?.poundEditText?.text?.clear()
        binding?.heightEditText?.text?.clear()
        binding?.feetEditText?.text?.clear()
        binding?.inchEditText?.text?.clear()
    }

    private fun usMetricVisibleView(){
        metricSelectedUnit = US_UNIT
        binding?.llDiplayBMIResult?.visibility = View.INVISIBLE
        binding?.etWeight?.visibility = View.INVISIBLE
        binding?.etFeet?.visibility = View.VISIBLE
        binding?.etInch?.visibility = View.VISIBLE
        binding?.etWeightPound?.visibility = View.VISIBLE

        binding?.weightEditText?.text?.clear()
        binding?.poundEditText?.text?.clear()
        binding?.heightEditText?.text?.clear()
        binding?.feetEditText?.text?.clear()
        binding?.inchEditText?.text?.clear()
    }

    private fun calculateUnits(){
        //TODO(Step 2 : Handling the current visible view and calculating US UNITS view input values if they are valid.)
        // START
        if (metricSelectedUnit == METRIC_UNIT) {
            if (validMetrics()) {

                val heightValue: Float = binding?.heightEditText?.text.toString().toFloat() / 100

                val weightValue: Float = binding?.weightEditText?.text.toString().toFloat()

                val bmi = weightValue / (heightValue * heightValue)

                displayBMIResult(bmi)
            } else {
                Toast.makeText(
                    this@BMIActivity,
                    "Please enter valid values.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } else {

            // The values are validated.
            if (validateUsUnits()) {

                val usUnitHeightValueFeet: String =
                    binding?.feetEditText?.text.toString()
                val usUnitHeightValueInch: String =
                    binding?.inchEditText?.text.toString()
                val usUnitWeightValue: Float = binding?.poundEditText?.text.toString()
                    .toFloat()

                val heightValue =
                    usUnitHeightValueInch.toFloat() + usUnitHeightValueFeet.toFloat() * 12

                val bmi = 703 * (usUnitWeightValue / (heightValue * heightValue))

                displayBMIResult(bmi)
            } else {
                Toast.makeText(
                    this@BMIActivity,
                    "Please enter valid values.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
    private fun validateUsUnits() : Boolean{
        var isValid: Boolean = true

        if (binding?.feetEditText?.text.toString().isEmpty()) {
            isValid = false
        } else if (binding?.inchEditText?.text.toString().isEmpty()) {
            isValid = false
        }else if (binding?.heightEditText?.text.toString().isEmpty()) {
            isValid = false
        }
        return isValid
    }
    private fun validMetrics() : Boolean{
        var isValid: Boolean = true

        if (binding?.weightEditText?.text.toString().isEmpty()) {
            isValid = false
        } else if (binding?.heightEditText?.text.toString().isEmpty()) {
            isValid = false
        }
        return isValid
    }
    private fun displayBMIResult(bmi: Float) {

        val bmiLabel: String
        val bmiDescription: String


        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0
        ) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops!You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0
        ) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0
        ) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (java.lang.Float.compare(bmi, 25f) > 0 && java.lang.Float.compare(
                bmi,
                30f
            ) <= 0
        ) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0
        ) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0
        ) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        } else {
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        //Use to set the result layout visible
        binding?.llDiplayBMIResult?.visibility = View.VISIBLE

        // This is used to round the result value to 2 decimal values after "."
        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()

        binding?.tvBMIValue?.text = bmiValue // Value is set to TextView
        binding?.tvBMIType?.text = bmiLabel // Label is set to TextView
        binding?.tvBMIDescription?.text = bmiDescription // Description is set to TextView

        binding?.llDiplayBMIResult?.visibility = View.VISIBLE
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}