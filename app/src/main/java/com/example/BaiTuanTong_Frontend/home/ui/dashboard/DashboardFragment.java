package com.example.BaiTuanTong_Frontend.home.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BaiTuanTong_Frontend.PostContentActivity;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.club.ClubHomeActivity;
import com.example.BaiTuanTong_Frontend.home.ui.home.HomeFragment;
import com.example.BaiTuanTong_Frontend.home.ui.home.PostAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DashboardFragment extends Fragment {
    private Context mContext;
    private View mView;
    // recyclerview
    private RecyclerView rv_post_list;
    private PostAdapter myAdapter;
    private HomeFragment.MyListener ac;
    // 与后端通信
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final MediaType STRING
            = MediaType.get("text/plain; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private static final int GET = 1;
    private static final int POST = 2;
    private static final String SERVERURL = "http://47.92.233.174:5000/";
    private static final String LOCALURL = "http://10.0.2.2:5000/";
    // 从后端拿来的数据
    public List<Integer> postId = new ArrayList<>();        // 动态ID
    public List<String> title = new ArrayList<>();          // 动态标题
    public List<String> clubName = new ArrayList<>();       // 社团名字
    public List<String> text = new ArrayList<>();           // 动态内容
    public List<Integer> likeCnt = new ArrayList<>();        // 动态点赞数
    public List<Integer> commentCnt = new ArrayList<>();     // 动态评论数
    public List<Integer> clubId = new ArrayList<>();        // 社团ID


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        // 初始RecyclerView
        initRecyclerLinear();
        return mView;
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        ac = (HomeFragment.MyListener)getActivity();
    }
    // 实现一个接口，搜索框给SearchResultActivity传输搜索字符串info
    public interface MyListener{
        public void sendContent(String info);//发送给SearchResultActivity
    }
    /*
    // 初始搜索框
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
    */
    // 跳转到社团主页，传递参数position（该动态再列表中的位置）
    private void startClubHomeActivity(Integer position) {
        Intent intent = new Intent(getActivity(), ClubHomeActivity.class);
        intent.putExtra("clubId", clubId.get(position));
        startActivity(intent);
    }
    // 跳转到动态内容界面，传递参数post_id
    private void startPostContentActivity(Integer post_id){
        Intent intent = new Intent(getActivity(),PostContentActivity.class);
        intent.putExtra("postId", post_id);
        startActivity(intent);
    }
    // 初始化线性布局的循环视图
    private void initRecyclerLinear() {
        rv_post_list = (RecyclerView)mView.findViewById(R.id.rv_post_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rv_post_list.setLayoutManager(manager);
        Log.e("init rv","1");
        //myAdapter = new PostAdapter(getActivity(), title, clubName, text, likeCnt, commentCnt);
        //rv_post_list.setAdapter(myAdapter);
        //rv_post_list.setItemAnimator(new DefaultItemAnimator());
        // 通过url传输数据
        getDataFromGet(SERVERURL + "post/homepage");
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
    /*
    // 创建menu时调用，实现搜索框
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_search,menu);
        initSearchView(menu);
        //return true;
    }
    */
    // 在获得GET请求返回的数据后更新UI
    private void updateView() {
        myAdapter = new PostAdapter(getActivity(), title, clubName, text, likeCnt, commentCnt);
        rv_post_list.setAdapter(myAdapter);

        // 下面是为点击事件添加的代码
        myAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onInternalViewClick(View view, PostAdapter.ViewName viewName, int position) {
                if (viewName == PostAdapter.ViewName.CLUB_IMG) {
                    Toast.makeText(getActivity().getBaseContext(), "拍了拍头像", Toast.LENGTH_SHORT).show();
                }
                else if (viewName == PostAdapter.ViewName.CLUB_NAME) {
                    startClubHomeActivity(position);
                }
                else if (viewName == PostAdapter.ViewName.POST_CONTENT) {
                    startPostContentActivity(postId.get(position));
                }
            }
        });
        mView.invalidate();
    }
    // 处理get请求与post请求的回调函数
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Log.e("TAG", (String)msg.obj);
            switch (msg.what){
                case GET:
                    try {
                        parseJsonPacket((String)msg.obj);
                        updateView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case POST:
                    break;
            }
            return true;
        }
    });

    /**
     * 解析get返回的json包
     * @param json get返回的json包
     * @throws JSONException 解析出错
     */
    private void parseJsonPacket(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        JSONArray postList = jsonObject.getJSONArray("postSummary");
        for (int i = 0; i < postList.length(); i++) {
            postId.add(postList.getJSONObject(i).getInt("postId"));
            title.add(postList.getJSONObject(i).getString("title"));
            clubName.add(postList.getJSONObject(i).getString("clubName"));
            text.add(postList.getJSONObject(i).getString("text"));
            likeCnt.add(postList.getJSONObject(i).getInt("likeCnt"));
            commentCnt.add(postList.getJSONObject(i).getInt("commentCnt"));
            clubId.add(postList.getJSONObject(i).getInt("clubId"));
            Log.e("text", ""+ postId.get(i));
        }
    }

    // 使用get获取数据
    private void getDataFromGet(String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Log.e("URL", url);
                    String result = get(url);
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.what = GET;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "get failed.");
                }
            }
        }.start();
    }

    // 使用post获取数据
    private void getDataFromPost(String url, String json) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String result = post(url, json); //jason用于上传数据，目前不需要
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.what = POST;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
                }
            }
        }.start();
    }

    /**
     * Okhttp的get请求
     * @param url 向服务器请求的url
     * @return 服务器返回的字符串
     * @throws IOException 请求出错
     */
    private String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Okhttp的post请求
     * @param url 向服务器请求的url
     * @param json 向服务器发送的json包
     * @return 服务器返回的字符串
     * @throws IOException 请求出错
     */
    private String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}