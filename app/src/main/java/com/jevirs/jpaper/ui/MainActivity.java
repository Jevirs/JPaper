package com.jevirs.jpaper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.jevirs.jpaper.R;
import com.jevirs.jpaper.util.MyAdapter;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity{

    private Handler handler;
    private MyAdapter myAdapter;
    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout refreshLayout;
    private static ArrayList<MyView> views = new ArrayList<>();
    private static int sort = -1;
    private static final String[] TYPE = {"ALL","Bulidings","Food","Nature","Object","People","Tech","Other","Upload"};
    private static final int BULIDINGS = 0;
    private static final int FOOD = 1;
    private static final int NATURE = 2;
    private static final int OBJECT = 3;
    private static final int PEOPLE = 4;
    private static final int TECH = 5;
    private static final int OTHER = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("OnCreate", "OnCreate");
        setContentView(R.layout.activity_main);

        AVOSCloud.initialize(this, "8f7ur6t4kved16kmlbna5x05pdqyva4v7w50sup3oqtxb6c0", "fvjkf8yk0ni8l7vj1ndd0m5amf608efzutwpogxzt3oxzpp7");

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(getApplicationContext(), 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs()    // Remove for release app
                .build();//开始构建
        ImageLoader.getInstance().init(config);

        initDrawer();
        initRefresh();
        initListView();
        handler=new Handler();
    }

    private void initRefresh() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.pull2refresh);
        refreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                views.clear();
                load(sort);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void initDrawer() {
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle=new ActionBarDrawerToggle(MainActivity.this,
                drawerLayout,
                toolbar,
                R.string.hello_world,
                R.string.hello_world);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.action_settings) {
                    Toast.makeText(getApplicationContext(), "SETTINGS", Toast.LENGTH_SHORT).show();
                }
                if (id == R.id.action_upload) {
                    Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.action_refresh) {
                    Toast.makeText(getApplicationContext(), "REFRESH", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        ListView drawer= (ListView) findViewById(R.id.drawer_list);
        drawer.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, TYPE));
        drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    sort = -1;
                    drawerLayout.closeDrawers();
                }
                if (position == 1) {
                    sort = 0;
                    drawerLayout.closeDrawers();
                }
                if (position == 2) {
                    sort = 1;
                    drawerLayout.closeDrawers();
                }
                if (position == 3) {
                    sort = 2;
                    drawerLayout.closeDrawers();
                }
                if (position == 4) {
                    sort = 3;
                    drawerLayout.closeDrawers();
                }
                if (position == 5) {
                    sort = 4;
                    drawerLayout.closeDrawers();
                }
                if (position == 6) {
                    sort = 5;
                    drawerLayout.closeDrawers();
                }
                if (position == 7) {
                    sort = 6;
                    drawerLayout.closeDrawers();
                }
                if (position == 8) {
                    Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                }
            }
        });
    }

    private void initListView(){

        ListView listView = (ListView) findViewById(R.id.listView);
        myAdapter= new MyAdapter(getApplicationContext(),R.layout.object_layout,views);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", views.get(position).getId());
                bundle.putString("title", views.get(position).getTitle());
                bundle.putString("thumb", views.get(position).getThumbUrl());
                bundle.putString("url", views.get(position).getUrl());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (view.getChildCount() == 0) {
                    enable = true;
                }
                if (view.getChildCount() > 0) {
                    boolean firstItemVisible = view.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = view.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                refreshLayout.setEnabled(enable);

                if (visibleItemCount + firstVisibleItem == totalItemCount) {
                    Log.e("log", "滑到底部");
                    Log.e("TOTAL", totalItemCount + ":items");
                }

            }
        });
    }

    private void load(int type){
        AVQuery<AVObject> avQuery=new AVQuery<>("JPic");
        switch (type){
            case -1:
                avQuery.whereExists("FILE");
                break;
            case 0:
                avQuery.whereEqualTo("FILE", BULIDINGS);
                break;
            case 1:
                avQuery.whereEqualTo("FILE",FOOD);
                break;
            case 2:
                avQuery.whereEqualTo("FILE",NATURE);
                break;
            case 3:
                avQuery.whereEqualTo("FILE",OBJECT);
                break;
            case 4:
                avQuery.whereEqualTo("FILE",PEOPLE);
                break;
            case 5:
                avQuery.whereEqualTo("FILE",TECH);
                break;
            case 6:
                avQuery.whereEqualTo("FILE",OTHER);
                break;
            default:
                avQuery.whereExists("FILE");
        }
        avQuery.setLimit(10);
        avQuery.skip(myAdapter.getCount());
        Log.e("conut", String.valueOf(myAdapter.getCount()));
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    for (AVObject avObject:avObjects){
                        String id = avObject.getObjectId();
                        String title = avObject.getAVFile("FILE").getOriginalName();
                        String thumb = avObject.getAVFile("FILE").getThumbnailUrl(true, 200, 200);
                        String uri = avObject.getAVFile("FILE").getUrl();
                        MyView myView = new MyView(id,title,thumb,uri);
                        views.add(myView);
                    }
                    myAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Find Faided" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause","onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("onRestart","onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestory", "onDestory");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onStart","onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("onStop","onStop");
    }
}









