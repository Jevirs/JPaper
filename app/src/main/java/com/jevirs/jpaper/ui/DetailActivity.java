package com.jevirs.jpaper.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.jevirs.jpaper.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView imageView = (ImageView) findViewById(R.id.detail);
        TextView titleTv = (TextView) findViewById(R.id.title);

        String url = getIntent().getExtras().getString("url");
        String title = getIntent().getExtras().getString("title");
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.loading) //设置图片在下载期间显示的图片
                //.showImageForEmptyUri(R.drawable.loading)//设置图片Uri为空或是错误的时候显示的图片
                //.showImageOnFail(R.drawable.loading)  //设置图片加载/解码过程中错误时候显示的图片
                .considerExifParams(false)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .displayer(new RoundedBitmapDisplayer(100))//是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(500))//是否图片加载好后渐入的动画时间
                .build();
        ImageLoader.getInstance().displayImage(url, imageView,options);
        titleTv.setText(title);
        }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
