package com.jevirs.jpaper.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.jevirs.jpaper.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class UploadActivity extends ActionBarActivity {

        private static RadioGroup radioGroup;
        private static RadioGroup radioGroup_;
        private static String filePath;
        private static final String TYPE_IMAGE="image/*";
        private static final String AVOBJECT="JPaperPic";
        private static final int REQUEST_CODE = 90 ;
        private static final int BUILDINGS=0;
        private static final int FOOD=1;
        private static final int NATURE=2;
        private static final int OBJECT=3;
        private static final int PEOPLE=4;
        private static final int TECH=5;
        private static final int OTHER=6;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_upload);

            Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            Button btn_pic_add= (Button) findViewById(R.id.button_add);
            Button btn_pic_send= (Button) findViewById(R.id.button_send);
            Button btn_pic_get= (Button) findViewById(R.id.button_get);
            final EditText editText= (EditText) findViewById(R.id.pic_name);
            radioGroup= (RadioGroup) findViewById(R.id.radioGroup);
            radioGroup_= (RadioGroup) findViewById(R.id.radioGroup_);
            btn_pic_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_PICK);
                    intent.setType(TYPE_IMAGE);
                    startActivityForResult(intent,REQUEST_CODE);
                }
            });

            btn_pic_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AVObject avObject=new AVObject(AVOBJECT);
                    try {
                        AVFile avFile=AVFile.withAbsoluteLocalPath(editText.getText().toString(),filePath);
                        avObject.put("FILE",avFile);
                        avObject.add("TYPE",String.valueOf(getPicType()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    avObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    ImageView imageView = (ImageView) findViewById(R.id.image_send);
                    BitmapDrawable bitmapDrawable= (BitmapDrawable) imageView.getDrawable();
                    if (!bitmapDrawable.getBitmap().isRecycled()){
                        bitmapDrawable.getBitmap().recycle();
                    }
                    imageView.setImageBitmap(null);
                    editText.setText("");
                    editText.clearFocus();
                    radioGroup.clearCheck();
                    radioGroup.clearFocus();
                }
            });
            btn_pic_get.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AVQuery<AVObject> avQuery=new AVQuery<>(AVOBJECT);
                    avQuery.whereEqualTo("TYPE",String.valueOf(getSelType()));
                    avQuery.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> avObjects, AVException e) {
                            if (e==null){
                                Toast.makeText(getApplicationContext(),"找到"+avObjects.size()+"条记录",Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    radioGroup_.clearCheck();
                    radioGroup_.clearFocus();
                }
            });
        }

        public int getPicType(){
            int id=radioGroup.getCheckedRadioButtonId();
            if (id == R.id.radio1) {
                return BUILDINGS;
            }
            if (id == R.id.radio2) {
                return FOOD;
            }
            if (id == R.id.radio3) {
                return NATURE;
            }
            if (id == R.id.radio4) {
                return OBJECT;
            }
            if (id == R.id.radio5) {
                return PEOPLE;
            }
            if (id == R.id.radio6) {
                return TECH;
            }else {
                return OTHER;
            }
        }

        public int getSelType(){
            int id=radioGroup_.getCheckedRadioButtonId();
            if (id == R.id.radio1_) {
                return BUILDINGS;
            }
            if (id == R.id.radio2_) {
                return FOOD;
            }
            if (id == R.id.radio3_) {
                return NATURE;
            }
            if (id == R.id.radio4_) {
                return OBJECT;
            }
            if (id == R.id.radio5_) {
                return PEOPLE;
            }
            if (id == R.id.radio6_) {
                return TECH;
            }else {
                return OTHER;
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode==REQUEST_CODE) {
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    ContentResolver contentResolver = this.getContentResolver();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 10;
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    ImageView imageView = (ImageView) findViewById(R.id.image_send);
                    imageView.setImageBitmap(bitmap);
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    filePath = cursor.getString(column_index);
                    cursor.close();
                }
            }else {
                Toast.makeText(getApplicationContext(),"并没有图片",Toast.LENGTH_SHORT).show();
            }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
