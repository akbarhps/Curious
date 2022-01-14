package com.charuniverse.curious.ui.post.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostCreateBinding

class PostCreateFragment : Fragment(R.layout.fragment_post_create) {

    private lateinit var binding: FragmentPostCreateBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPostCreateBinding.bind(view)

    }
}