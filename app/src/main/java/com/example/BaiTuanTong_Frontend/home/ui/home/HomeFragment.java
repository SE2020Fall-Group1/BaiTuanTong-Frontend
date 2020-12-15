package com.example.BaiTuanTong_Frontend.home.ui.home;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.BaiTuanTong_Frontend.MainActivity;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.home.HomePageActivity;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Context mContext;
    private View mView;
    //private HomePageActivity homePageActivity;
    private MyListener ac;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        initRecyclerLinear();
        return mView;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        ac = (MyListener)getActivity();
    }

    public interface MyListener{
        public void sendContent(String info);
    }
    private void initSearchView(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView)menuItem.getActionView();
        // 设置搜索框默认自动缩小为图标
        searchView.setIconifiedByDefault(true);
        // 让键盘的回车键设置成搜索
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        // 设置是否显示搜索按钮
        searchView.setSubmitButtonEnabled(true);
        //设置提示词
        searchView.setQueryHint("搜索社团或动态");
        /*
        SearchManager sm = (SearchManager)mContext.getSystemService(Context.SEARCH_SERVICE);
        ComponentName cn = new ComponentName(homePageActivity, SearchResultActivity.class);
        SearchableInfo info = sm.getSearchableInfo(cn);
        searchView.setSearchableInfo(info);
        */
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 搜索关键词完成输入
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("asdas",query);
                ac.sendContent(query);
                return false;
            }
            // 搜索关键词发生变化
            @Override
            public boolean onQueryTextChange(String newText) {
                // 提示框，未实现
                doSearch(newText);
                return false;
            }
        });
    }
    // 前端调试用，实现一个简易的list
    private List<String> mList;
    // List初始化，随意实现的
    private List<String> getList(){
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            list.add("首页测试动态专用\n"+"这是一条无聊的动态"+"");
        }
        return list;
    }
    // 初始化线性布局的循环视图
    private void initRecyclerLinear() {
        RecyclerView rv_post_list = mView.findViewById(R.id.rv_post_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rv_post_list.setLayoutManager(manager);
        mList = getList();
        PostAdapter adapter = new PostAdapter(getContext(), mList);
        rv_post_list.setAdapter(adapter);
        // 下面是为点击事件添加的代码
        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onInternalViewClick(View view, PostAdapter.ViewName viewName, int position) {
                if (viewName == PostAdapter.ViewName.USR_IMG) {
                    Toast.makeText(getActivity().getBaseContext(), "拍了拍头像", Toast.LENGTH_SHORT).show();
                }
                else if(viewName == PostAdapter.ViewName.POST_TEXT) {
                    Toast.makeText(getActivity().getBaseContext(), "点击了文本", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getBaseContext(), mList.get(position), Toast.LENGTH_SHORT).show();
                //sendMessage(position);
            }
        });
        rv_post_list.setItemAnimator(new DefaultItemAnimator());
    }
    // 提示框，未实现
    private void doSearch(String text) {
       /* if(text.indexOf("北")==0){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.search_list_auto,hintArray);
            sac_key.setAdapter(adapter);
            sac_key.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    sac_key.setText(((TextView)view).getText());
                }
            });
        }*/
    }

    // 创建menu时调用，实现搜索框
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_search,menu);
        initSearchView(menu);
        //return true;
    }
}