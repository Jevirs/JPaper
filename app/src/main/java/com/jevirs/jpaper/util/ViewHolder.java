package com.jevirs.jpaper.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jevirs.jpaper.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ViewHolder {
    private SparseArray<View> mViews;
    private int mPostion;
    private View mConvertView;

    public ViewHolder(Context context,ViewGroup parent,int redId,int postion){
        this.mPostion=postion;
        this.mViews=new SparseArray<>();
        this.mConvertView= LayoutInflater.from(context).inflate(redId,parent,false);
        mConvertView.setTag(this);
    }

    public static ViewHolder getInstance(Context context,View convertview,ViewGroup parent,int resID,int postion){
        if (convertview==null){
            return new ViewHolder(context,parent,resID,postion);
        }
        else {
            ViewHolder viewHolder= (ViewHolder) convertview.getTag();
            viewHolder.mPostion=postion;
            return  viewHolder;
        }
    }

    public View getView(int viewId){
        View view=mViews.get(viewId);

        if(view==null){
            view=mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public void setText(int id,String text){
        ((TextView)getView(id)) . setText(text);
    }

    public void setText(int id,int res){
        ((TextView)getView(id)) . setText(res);
    }

    public void setImage(int id,BitmapDrawable bitmapDrawable){
        ((ImageView) getView(id)) . setImageDrawable(bitmapDrawable);
    }

    public void setImage(int id,int res){
        ((ImageView) getView(id)) . setImageResource(res);
    }

    public void setImage(int id,String url){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.pic3) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.pic9)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.pic3)  //设置图片加载/解码过程中错误时候显示的图片
                .considerExifParams(false)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .displayer(new RoundedBitmapDisplayer(100))//是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                .build();
        ImageView imageView = (ImageView) getView(id);
        ImageLoader.getInstance().displayImage(url,imageView,options);
    }

    //not in main thread
    /*
    public void setImage(int id,URL url){
        try {
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedInputStream bis=new BufferedInputStream(is);
            ((ImageView) getView(id)) . setImageDrawable(BitmapDrawable.createFromStream(bis,null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}