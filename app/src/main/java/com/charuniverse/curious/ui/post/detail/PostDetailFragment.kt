package com.charuniverse.curious.ui.post.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostDetailBinding
import com.charuniverse.curious.util.Constant

class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    private lateinit var binding: FragmentPostDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostDetailBinding.inflate(inflater, container, false)
            .also { it.post = Constant.post }

        return binding.root
    }

}