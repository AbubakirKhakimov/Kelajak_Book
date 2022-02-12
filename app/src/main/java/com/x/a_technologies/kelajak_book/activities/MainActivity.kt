package com.x.a_technologies.kelajak_book.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.hawk.Hawk
import com.x.a_technologies.kelajak_book.databinding.ActivityMainBinding
import com.x.a_technologies.kelajak_book.datas.ImageTracker

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && data.data != null && resultCode == RESULT_OK){
            if (ImageTracker.imageSelectedCallBack != null){
                ImageTracker.imageSelectedCallBack!!.imageSelectedListener(data.data!!)
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        Hawk.init(newBase).build();
        super.attachBaseContext(newBase)
    }

}