package com.xy.xbar

import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity(), BaseFragment.OnFragmentInteractionListener{
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (findViewById<FrameLayout>(R.id.content) != null) {
            if (savedInstanceState != null) {
                return;
            }

            val aFragment = AFragment()

            supportFragmentManager.beginTransaction().add(R.id.content, aFragment).commit();
        }
    }


}
