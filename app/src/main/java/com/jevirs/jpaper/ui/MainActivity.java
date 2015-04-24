package com.jevirs.jpaper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.jevirs.jpaper.util.Data;
import com.jevirs.jpaper.util.MyAdapter;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    private Handler handler;
    private MyAdapter myAdapter;
    private static int sort=-1;
    private static final String AVOBJECT="JPaperPic";
    private static final String APPLICATIONID="xv5kgkb854owr84dvhoibxsoaowkimwhnk0vou1rshi3z4ne";
    private static final String CLIENTID="yjvf05xb80ifsizybsx363whvw21je0gopolgh0tt0swyldj";
    private static final String[] TYPE = {"Bulidings","Food","Nature","Object","People","Tech","Other","Upload"};
    private static final int BULIDINGS=0;
    private static final int FOOD=1;
    private static final int NATURE=2;
    private static final int OBJECT=3;
    private static final int PEOPLE=4;
    private static final int TECH=5;
    private static final int OTHER=6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("OnCreate", "OnCreate");
        setContentView(R.layout.activity_main);

        AVOSCloud.initialize(getApplicationContext(), APPLICATIONID, CLIENTID);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                //.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it carefully (Better don't use it)/设置缓存的详细信息，最好不要设置这个
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
               // .discCacheSize(50 * 1024 * 1024)
                //.discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                //.discCacheFileCount(100) //缓存的文件数量
                //.discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(getApplicationContext(), 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();//开始构建
        ImageLoader.getInstance().init(config);

        handler=new Handler();
        initView();
    }

    private void initView(){
        final DrawerLayout drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        android.support.v7.widget.Toolbar toolbar= (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ActionBarDrawerToggle drawerToggle=new ActionBarDrawerToggle(MainActivity.this,
                drawerLayout,
                toolbar,
                R.string.hello_world,
                R.string.hello_world);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);


        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_settings) {
                    Toast.makeText(getApplicationContext(),"SETTINGS",Toast.LENGTH_SHORT).show();
                }
                if (id == R.id.action_upload) {
                    Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.action_refresh) {
                    Toast.makeText(getApplicationContext(),"REFRESH",Toast.LENGTH_SHORT).show();
            }
                return true;
            }

        });

        final SwipeRefreshLayout refreshLayout= (SwipeRefreshLayout) findViewById(R.id.pull2refresh);
        refreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light);
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query(sort);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        ListView drawer= (ListView) findViewById(R.id.drawer_list);
        drawer.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, TYPE));
        drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    sort = 0;
                    drawerLayout.closeDrawers();
                }
                if (position == 1) {
                    sort = 1;
                    drawerLayout.closeDrawers();
                }
                if (position == 2) {
                    sort = 2;
                    drawerLayout.closeDrawers();
                }
                if (position == 3) {
                    sort = 3;
                    drawerLayout.closeDrawers();
                }
                if (position == 4) {
                    sort = 4;
                    drawerLayout.closeDrawers();
                }
                if (position == 5) {
                    sort = 5;
                    drawerLayout.closeDrawers();
                }
                if (position == 6) {
                    sort = 6;
                    drawerLayout.closeDrawers();
                }
                if (position == 7) {
                    Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                }
            }
        });


        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), DetailActivity.class);
                intent.putExtra("url",Data.items.get(position).getUrl());
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
                ListView ls = (ListView) findViewById(R.id.listView);
                if (ls.getChildCount() == 0) {
                    enable = true;
                }
                if (ls.getChildCount() > 0) {
                    boolean firstItemVisible = ls.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = ls.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                refreshLayout.setEnabled(enable);
            }
        });

        myAdapter= new MyAdapter(getApplicationContext(),R.layout.object_layout,Data.items);
        listView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }


    private void query(int type) {
        AVQuery<AVObject> query = new AVQuery<>(AVOBJECT);
        query.setLimit(20);
        switch (type){
            case -1:
                query.whereExists("FILE");
                break;
            case 0:
                query.whereEqualTo("TYPE",BULIDINGS);
                break;
            case 1:
                query.whereEqualTo("TYPE",FOOD);
                break;
            case 2:
                query.whereEqualTo("TYPE",NATURE);
                break;
            case 3:
                query.whereEqualTo("TYPE",OBJECT);
                break;
            case 4:
                query.whereEqualTo("TYPE",PEOPLE);
                break;
            case 5:
                query.whereEqualTo("TYPE",TECH);
                break;
            case 6:
                query.whereEqualTo("TYPE",OTHER);
                break;
            default:
                query.whereExists("FILE");
        }
        query.findInBackground(new FindCallback<AVObject>() {
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    for (AVObject avObject : avObjects) {
                        String thumbUrl = avObject.getAVFile("FILE").getThumbnailUrl(true,200,200);
                        String url = avObject.getAVFile("FILE").getUrl();
                        String id = avObject.getAVFile("FILE").getObjectId();
                        String title = avObject.getAVFile("FILE").getOriginalName();
                        MyView myView = new MyView(id,title,thumbUrl,url);
                        Data.items.add(myView);
                        myAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "error:" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("OnPause","OnPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("OnRestart","OnRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("OnDestory", "OnDestory");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("OnResume", "OnResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("start","start");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Stop","stop");
    }
}









