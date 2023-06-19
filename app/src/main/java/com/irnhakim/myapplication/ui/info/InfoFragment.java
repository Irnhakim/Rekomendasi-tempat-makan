package com.irnhakim.myapplication.ui.info;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.irnhakim.myapplication.R;
import com.irnhakim.myapplication.activity.MainActivity;
import com.irnhakim.myapplication.adapter.InfoFragmentAdapter;

import java.util.ArrayList;
import java.util.List;


public class InfoFragment extends Fragment {
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_info, container, false);

        viewPager = root.findViewById(R.id.view_pager_info);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().hide();

        List<Fragment> list = new ArrayList<>();
        list.add(new InfoFragment1());
        list.add(new InfoFragment2());

        pagerAdapter = new InfoFragmentAdapter(requireActivity().getSupportFragmentManager(), list);

        viewPager.setAdapter(pagerAdapter);
    }
}