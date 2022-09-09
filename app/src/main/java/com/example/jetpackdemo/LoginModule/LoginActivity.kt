package com.example.jetpackdemo.LoginModule

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.jetpackdemo.APIState
import com.example.jetpackdemo.BaseActivity
import com.example.jetpackdemo.HomePage.HomePageActivity
import com.example.jetpackdemo.LoginModule.DataModel.RequestEntity.LoginRequestEntity
import com.example.jetpackdemo.LoginModule.Repository.LoginRepository
import com.example.jetpackdemo.LoginModule.ViewModel.LoginViewModel
import com.example.jetpackdemo.LoginModule.ViewmodelFactory.LoginViewModelFactory
import com.example.jetpackdemo.RetrofitHelper
import com.example.jetpackdemo.RoomDemo.Database.DemoDatabase
import com.example.jetpackdemo.Utility.Constant
import com.example.jetpackdemo.Utility.NetworkUtils
import com.example.jetpackdemo.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar


class LoginActivity : BaseActivity() {


    lateinit var binding : ActivityLoginBinding
    lateinit var viewModel: LoginViewModel
    lateinit var viewParent : CoordinatorLayout

    private  lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isCameraPermissionGranted = false
    private var isLocationPermissionGranted = false
    private var isRecordPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewParent = binding.lyparent

        init()

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permission ->

            isReadPermissionGranted = permission[Manifest.permission.READ_EXTERNAL_STORAGE ] ?: isReadPermissionGranted
            isCameraPermissionGranted = permission[Manifest.permission.CAMERA ] ?: isCameraPermissionGranted
            isLocationPermissionGranted = permission[Manifest.permission.ACCESS_FINE_LOCATION ] ?: isLocationPermissionGranted
            isRecordPermissionGranted = permission[Manifest.permission.RECORD_AUDIO ] ?: isRecordPermissionGranted

            if (isReadPermissionGranted && isCameraPermissionGranted && isLocationPermissionGranted && isRecordPermissionGranted){

                showSnackBar(binding.lyparent,"All Permission Granted")
            }else{

                showSnackBar(binding.lyparent,"Need Permission For this Action")

            }

        }



       // requestPermission()

        //region Observing Live and Flow Data

          // region Flow Observing
       lifecycleScope.launchWhenStarted {

           viewModel.LoginStateFlow.collect{

               when(it){

                   is APIState.Loading -> {
                       showDialog()
                   }

                   is APIState.Success -> {
                       cancelDialog()
                      it.data?.let {

                         // Log.d(Constant.TAG_Coroutine, it.MasterData.EmailID)

                        //  Log.d(Constant.TAG_Coroutine, it.MasterData.toString())

                         // showSnackBar(viewParent, "Dear ${it.MasterData.FullName}, You Login Successfully!!")

                          startActivity(Intent(this@LoginActivity, HomePageActivity::class.java))
                      }




                   }
                   is APIState.Failure -> {

                       cancelDialog()

                       showSnackBar(viewParent,it.errorMessage?: Constant.ErrorMessage)
                       Log.d(Constant.TAG_Coroutine, it.errorMessage.toString())
                   }
                   is APIState.Empty -> {
                       cancelDialog()
                       Log.d(Constant.TAG_Coroutine,"Using FlowData" + it.errorMessage.toString())

                   }

               }
           }
       }

        //endregion

        // region When WE Used Live Data , we have to observer

        viewModel.loginLiveData.observe( this,{

            when(it){

                is APIState.Loading -> {
                    showDialog()
                }

                is APIState.Success -> {
                    cancelDialog()
                    it.data?.let {

                        Log.d(Constant.TAG_Coroutine,"Using LiveData"+ it.MasterData.EmailID)

                        Log.d(Constant.TAG_Coroutine, "Using LiveData" +it.MasterData.toString())


                        showSnackBar(viewParent, "Dear ${it.MasterData.FullName}, You Login Successfully!!")
                    }
                    startActivity(Intent(this, HomePageActivity::class.java))


                }
                is APIState.Failure -> {

                    cancelDialog()

                    showSnackBar(viewParent,"Using LiveData" +it.errorMessage?: Constant.ErrorMessage)
                    Log.d(Constant.TAG_Coroutine,"Using LiveData" + it.errorMessage.toString())


                }
                is APIState.Empty -> {
                    cancelDialog()

                }

            }


        })

        // endregion

        //endregion

        binding.includeLogin.btnSignIn.setOnClickListener {




            if (!NetworkUtils.isNetworkAvailable(this@LoginActivity)){

                showSnackBar(viewParent,Constant.NetworkError)
                return@setOnClickListener
            }

            if(validate()){

            var loginRequestEntity  =    LoginRequestEntity(
                                        AppID = "",
                                        AppPASSWORD = "",
                                        AppUSERID = "",
                                        DeviceId = "eb3b33f11a6c28b9",
                                        DeviceName	= "",
                                        DeviceOS	= "",
                                        EmailId	= "",
                                        FBAId	=	0,
                                        IpAdd	= "",
                                        IsChildLogin	= "",
                                        LastLog	= "",
                                        MobileNo	= "",
                                        OldPassword	= "",
                                        Password	=	binding.includeLogin.etPassword.text.toString(),
                                        TokenId	=	"",
                                        UserId	= 0,
                                        UserName	=	binding.includeLogin.etEmail.text.toString(),
                                        UserType	= "",
                                        VersionNo	= "",

                )

            /*************************************************************************************************/
                                    //  Flow Demo  //
            /*************************************************************************************************/

               viewModel.getLoginUsingFlow(loginRequestEntity)


            /************************************************************************************************************/
                                //  Flow with LiveData   // { Repository is called by flow and viewModel used Live data
                                                         //  so tha Observer is triggered at activity
            /************************************************************************************************************/

             // viewModel.getLoginUsingLiveData(loginRequestEntity)


            }



        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //region All  method()

    private fun init(){

        var demoDatabase = DemoDatabase.getDatabase(applicationContext)
        var loiginRepository = LoginRepository(RetrofitHelper.retrofitLoginApi,demoDatabase)
        var viewModelFactory = LoginViewModelFactory(loiginRepository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(LoginViewModel::class.java)


    }
    private fun requestPermission(){
        isReadPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

        isCameraPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        isLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        isRecordPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED


        val permissionRequest : MutableList<String> = ArrayList()

        if(!isReadPermissionGranted){
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if(!isCameraPermissionGranted){
            permissionRequest.add(Manifest.permission.CAMERA)
        }
        if(!isLocationPermissionGranted){
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if(!isRecordPermissionGranted){
            permissionRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        /************ Check If Request Required or Not **************/

        if(permissionRequest.isNotEmpty()){

            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    private  fun validate() : Boolean {


        var blnCheck : Boolean = true

        if(binding.includeLogin.etEmail.text.isNullOrBlank()){

            Snackbar.make(viewParent, "Required Email ID!!", Snackbar.LENGTH_SHORT).show()
            blnCheck = false
        }else if(binding.includeLogin.etPassword.text.isNullOrEmpty()){

            Snackbar.make(viewParent, "Required Password!!", Snackbar.LENGTH_SHORT).show()
            blnCheck = false
        }


        return blnCheck
    }

    //endregion
}