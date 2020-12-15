package com.example.BaiTuanTong_Frontend.search_result.ui.post_search_result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BaiTuanTong_Frontend.R;

import java.util.ArrayList;
import java.util.List;

public class PostSearchResultFragment extends Fragment {

    private View mView;
    private RecyclerView mRecyclerView;
    private List<String> mList;
    private PostSearchResultAdapter mPostSearchResultAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_post_search_result, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById((R.id.post_search_result_recyclerView));

        mList = getList(getArguments().getString("searchText"));

        mPostSearchResultAdapter = new PostSearchResultAdapter(getActivity(), mList);
        mRecyclerView.setAdapter(mPostSearchResultAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        return mView;
    }

    public static PostSearchResultFragment newInstance(String text, int item) {
        Bundle bundle = new Bundle();
        bundle.putString("searchText", text);
        bundle.putInt("item", item);

        PostSearchResultFragment fragment = new PostSearchResultFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private List<String> getList(String text) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            list.add("动态" + i + "：“" + text + "”的搜索结果");
        }
        return list;
    }
}