package com.example.jetpackdemo.RoomDemo.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jetpackdemo.RoomDemo.Database.DemoDatabase
import com.example.jetpackdemo.RoomDemo.UI.DisplayRoomData.DisplayRoomAdapter
import com.example.jetpackdemo.RoomDemo.UI.ViewModelRoom.RoomDemo1ViewModel
import com.example.jetpackdemo.RoomDemo.UI.ViewModelRoom.RoomDemo1ViewModelFactory
import com.example.jetpackdemo.RoomDemo.Entity.Contact
import com.example.jetpackdemo.databinding.ActivityRoomDemoBinding
import com.google.android.material.snackbar.Snackbar
import java.util.*

/**************************************************************************************************************

 1> Room Insert Operation : Using Live Data
 2> Display Room Data in Recyclerview

 Note : Recyclerview is Used with Nested Scrollview For common Scroll
   for this apply
        <androidx.recyclerview.widget.RecyclerView
        app:layout_behavior="@string/appbar_scrolling_view_behavior"
         ...........
        </androidx.recyclerview.widget.RecyclerView>


https://medium.com/android-news/recyclerview-within-nestedscrollview-scrolling-issue-3180b5ad2542
 ************************************************************************************************************/
class RoomDemoActivity : AppCompatActivity() {


    lateinit var binding : ActivityRoomDemoBinding
    lateinit var  database: DemoDatabase
    lateinit var viewModel: RoomDemo1ViewModel
    lateinit var viewModelFactory: RoomDemo1ViewModelFactory

    lateinit var displayRoomAdapter: DisplayRoomAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoomDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        supportActionBar!!.apply {

            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle("Room Insert Demo ")
        }

        database = DemoDatabase.getDatabase(this@RoomDemoActivity)
        viewModelFactory = RoomDemo1ViewModelFactory(database)
        viewModel = ViewModelProvider(this,viewModelFactory).get(RoomDemo1ViewModel::class.java)


        initData()

        viewModel.getContactList().observe(this, androidx.lifecycle.Observer {


//                if(it.size > 0){
//
//                    var  count = it.size - 1
//
//                        Log.d("COROUTINE", it.get(count).name)
//
//
//                    Log.d("COROUTINE", it.toString())
//
//                    binding.includeRoomDemo1.txtResult.text = it.toString()
//
//                }

            it?.let {

                displayRoomAdapter.setData(it)

//                displayRoomAdapter = DisplayRoomAdapter(it)      // Set Data to Empty
//
//                binding.includeRoomDemo1.rvDisplay.adapter = displayRoomAdapter
//
//
//                binding.includeRoomDemo1.rvDisplay.scrollToPosition(displayRoomAdapter.itemCount -1)

                    // binding.includeRoomDemo1.svParent.s(displayRoomAdapter.itemCount -1)

              //  binding.includeRoomDemo1.svParent.scrollTo(0, 5)
               // binding.includeRoomDemo1.svParent.fullScroll(View.FOCUS_DOWN)

               // binding.includeRoomDemo1.rvDisplay.smoothScrollToPosition(displayRoomAdapter.itemCount -1)

//                binding.includeRoomDemo1.svParent.getParent().requestChildFocus(binding.includeRoomDemo1.svParent,
//                    binding.includeRoomDemo1.svParent);

                binding.includeRoomDemo1.svParent.postDelayed(Runnable {

                    binding.includeRoomDemo1.svParent.fullScroll(View.FOCUS_DOWN)
                   // binding.includeRoomDemo1.rvDisplay.scrollToPosition(displayRoomAdapter.itemCount -1)
                }, 1000)
            }




        })

        binding.includeRoomDemo1.btnSubmit.setOnClickListener {

                if(validate()){

                    //region commented
                    //Note : We Don't have to use GlobalScope , because its use application context and exist untill app is exist
                    // hence if Activity  is destroy it is still running.Memory leakage issue occour
//                   GlobalScope.launch {
//
//                       val contact = Contact(id = 0, name = binding.includeRoomDemo1.etName.text.toString(),
//                                             mobile = binding.includeRoomDemo1.etMobile.text.toString(),
//                                            address = binding.includeRoomDemo1.etAddress.text.toString() ,
//                                            createDate = Date()
//                       )
//                       database.contactDao().insertContact(contact)
//
//                       Snackbar.make(binding.includeRoomDemo1.btnSubmit, "Data Save Successfully !!", Snackbar.LENGTH_SHORT).show()
//                       clearData()
//                   }
                    //endregion

                    val contact = Contact(id = 0, name = binding.includeRoomDemo1.etName.text.toString(),
                                             mobile = binding.includeRoomDemo1.etMobile.text.toString(),
                                            address = binding.includeRoomDemo1.etAddress.text.toString() ,
                                            createDate = Date()
                       )

                    viewModel.insertContact(contact)
                    Snackbar.make(binding.includeRoomDemo1.btnSubmit, "Data Save Successfully !!", Snackbar.LENGTH_SHORT).show()
                    clearData()

                }

        }





        binding.fabView.setOnClickListener{

            /****************************************************************
              Here getContactData() is suspend function to execute this function
              we either required suspend function or coroutine Scope.

             The best approach is execute in viewModel using viewModel Scope .
             but if we want to execute any suspend fun in Activity than we have to used "lifecycleScope".
             *****************************************************************/
//            lifecycleScope.launch {
//
//                val data  = viewModel
//
//               // var contactList  : LiveData<List<Contact>>
//              var  contactList = viewModel.getContactData()
//                Log.d("COROUTINE", contactList.get(0).name)
//            }


            var  contactList = viewModel.getContactList()
           var  count = contactList.value!!.size - 1
                Log.d("COROUTINE", contactList.value!!.get(count).name)


        }

    }

    fun initData(){

        displayRoomAdapter = DisplayRoomAdapter(ArrayList())      // Set Data to Empty

        binding.includeRoomDemo1.rvDisplay.apply {

            setHasFixedSize(true)
            layoutManager=  LinearLayoutManager(this@RoomDemoActivity)
            adapter = displayRoomAdapter
           // isNestedScrollingEnabled = false

        }

    }
    fun clearData(){

        binding.includeRoomDemo1.apply {
            etName.setText("")
            etAddress.setText("")
            etMobile.setText("")
        }

    }

    fun validate() : Boolean {

       var viewBtn = binding.includeRoomDemo1.btnSubmit
       var blnCheck : Boolean = true

       if(binding.includeRoomDemo1.etName.text.isNullOrEmpty()){

           Snackbar.make(viewBtn, "Required Name!!", Snackbar.LENGTH_SHORT).show()
           blnCheck = false
       }else if(binding.includeRoomDemo1.etMobile.text.isNullOrEmpty()){

            Snackbar.make(viewBtn, "Required Mobile No!!", Snackbar.LENGTH_SHORT).show()
            blnCheck = false
        }else if(binding.includeRoomDemo1.etMobile.text.length < 10){

           Snackbar.make(viewBtn, "Mobile No must be 10 digit!!", Snackbar.LENGTH_SHORT).show()
           blnCheck = false
       }else if(binding.includeRoomDemo1.etAddress.text.isNullOrEmpty()){

           Snackbar.make(viewBtn, "Required Address !!", Snackbar.LENGTH_SHORT).show()
           blnCheck = false
       }


       return blnCheck
    }


}