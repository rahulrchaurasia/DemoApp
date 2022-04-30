package com.example.jetpackdemo.KotlinDemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.jetpackdemo.R
import com.example.jetpackdemo.Utility.Constant
import com.example.jetpackdemo.databinding.ActivityKotlinBasicDemoBinding

class KotlinBasicDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKotlinBasicDemoBinding

    val name : String? = null
    val lname : String? = null

    var arryName : ArrayList<String>? = null

    var list : MutableList<String> = ArrayList<String>()

    val mutableList = mutableListOf<String>()

    val arrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityKotlinBasicDemoBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // safe call

        Log.d(Constant.TAG1, name?.length.toString())

        // safe call with let

        name?.let {
            Log.d(Constant.TAG1, "length is ${name.length} ")
        }

        // Elvis Operator

        val len = lname?.length ?: "1"
        Log.d(Constant.TAG1, len.toString())


      // var myData = arryName?.size

        list.size



    }
}