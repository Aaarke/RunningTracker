package com.roshi.runningtrackerapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.roshi.runningtrackerapp.R
import com.roshi.runningtrackerapp.other.Constant.KEY_FIRST_TIME_TOGGLE
import com.roshi.runningtrackerapp.other.Constant.KEY_NAME
import com.roshi.runningtrackerapp.other.Constant.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @set:Inject
    var isFirstAppOpen=true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isFirstAppOpen){
            val navOptions=NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment,true)
                .build()
            findNavController().navigate(R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions)


        }
        tvContinue.setOnClickListener {
            val success=writePersonalDataToSharedPref()
            if (success){
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }else{
                Snackbar.make(requireView(),getString(R.string.please_enter_all_field),
                    Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    private fun writePersonalDataToSharedPref():Boolean{
        val name=etName.text.toString()
        val weight=etWeight.text.toString()
        if (name.isEmpty()||weight.isEmpty()){
            return false
        }
    sharedPreferences.edit().putString(KEY_NAME,name)
        .putFloat(KEY_WEIGHT,weight.toFloat())
        .putBoolean(KEY_FIRST_TIME_TOGGLE,false)
        .apply()
        val toolbarText="Let's go,$name "
        requireActivity().tvToolbarTitle.text=toolbarText
        return true
    }
}