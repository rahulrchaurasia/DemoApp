package com.example.jetpackdemo

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.jetpackdemo.databinding.ActivityBaseBinding
import com.example.jetpackdemo.databinding.DialogLoadingBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

open class BaseActivity : AppCompatActivity() {

       private lateinit var bindingMain : ActivityBaseBinding

       private lateinit var dialog : Dialog

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            bindingMain = ActivityBaseBinding.inflate(layoutInflater)
            setContentView(bindingMain.root)

             dialog = Dialog(this)


        }

        open fun toast( text: String) = Toast.makeText( this, text, Toast.LENGTH_SHORT).show()

        open fun showDialog(msg: String = "Loading Please Wait!!"){

            if(!dialog.isShowing) {
                val dialogLoadingBinding = DialogLoadingBinding.inflate(layoutInflater)
                dialog.setContentView(dialogLoadingBinding.root)
                if (dialog.window != null) {

                    dialog.window!!.setBackgroundDrawable(ColorDrawable(0))

                }
                if(msg.isNotEmpty()){
                    dialogLoadingBinding.txtMessage.text = msg

                }
                dialog.setCancelable(false)
                dialog.show()
            }
        }

          open fun cancelDialog(){

                if(dialog.isShowing){

                    dialog.dismiss()
                }


        }


        fun showAlert(msg : String){

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Alert")
            builder.setMessage(msg)
    //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton(android.R.string.ok) { dialog, which ->

            }


            builder.show()
        }

       open fun showAlert(title : String ,msg : String,

                          action: (strType: String,dialog : DialogInterface) -> Unit) {
           val alertDialog = AlertDialog.Builder(this)

           alertDialog.apply {
               setIcon(R.drawable.ic_email_24)
               setTitle(title)
               setMessage(msg)
               setCancelable(false)
               setPositiveButton("OK") {dialog, whichButton ->

                //dialog.dismiss()
                action("Y",dialog)

               }

                setNegativeButton("Cancel") { dialog, whichButton ->
                   // dialog.dismiss()
                    action("N", dialog)
                }
//        setNeutralButton("Neutral") { _, _ ->
//            toast("clicked neutral button")
//        }
           }.create().show()
       }



    open fun showSnackBar(view : View, strMessage: String){

        Snackbar.make(view, strMessage, Snackbar.LENGTH_SHORT).show()

     }

    open fun showToast(strMessage: String){
        Toast.makeText(this,strMessage,Toast.LENGTH_SHORT).show()
    }




}