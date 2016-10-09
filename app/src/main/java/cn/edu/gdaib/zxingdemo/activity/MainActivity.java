package cn.edu.gdaib.zxingdemo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import cn.edu.gdaib.zxingdemo.R;
import cn.edu.gdaib.zxingdemo.utils.BitmapUtil;
import cn.edu.gdaib.zxingdemo.utils.CommonUtil;
import cn.edu.gdaib.zxingdemo.view.ClearEditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ClearEditText et_scanResult;
    private ClearEditText et_makeText;
    private TextView tv_currentPosition;
    private TextView tv_activityName;
    private TextView tv_signTime;
    private Button btn_scanQRCode;
    private Button btn_makeQRCode;
    private ImageView iv_qrcode_img;
    protected int mScreenWidth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_scanResult = (ClearEditText)this.findViewById(R.id.et_qrcodeText);
        et_makeText=(ClearEditText)this.findViewById(R.id.et_qrcodeString);
        btn_scanQRCode=(Button)this.findViewById(R.id.btn_qrcodeScan);
        btn_makeQRCode=(Button)this.findViewById(R.id.btn_qrcodeMake);
        tv_currentPosition = (TextView)this.findViewById(R.id.tv_qrcodeLocation);
        tv_activityName = (TextView)this.findViewById(R.id.tv_qrcodeActivity);
        tv_signTime = (TextView)this.findViewById(R.id.tv_qrcodeTime);
        iv_qrcode_img = (ImageView)this.findViewById(R.id.iv_qrcodeImage);
        btn_scanQRCode.setOnClickListener(this);
        btn_makeQRCode.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;

        //设置ImageView长按监听事件，弹出保存对话框，保存图片至本地
        iv_qrcode_img.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setItems(new String[]{getResources().getString(R.string.qrcode_save_picture)},new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        iv_qrcode_img.setDrawingCacheEnabled(true);
                        //将ImageView中的图片转换成Bitmap
                        Bitmap imageBitmap=iv_qrcode_img.getDrawingCache();
                        if(imageBitmap!=null){
                            new SaveImageTask().execute(imageBitmap);
                        }
                    }
                });
                builder.show();
                return true;//不加短按
            }
        });
    }

    @Override
    public void onClick(View view) {
        String tempText=getResources().getString(R.string.qrcode_activity_name)+":"+
                et_scanResult.getText().toString().trim();
        switch (view.getId()){
            case R.id.btn_qrcodeScan:
                if(TextUtils.isEmpty(et_scanResult.getText().toString().trim())){
                    Snackbar.make(view,"请输入要签到的活动名称",Snackbar.LENGTH_SHORT)
                            .setAction("Action",null).show();
                }else{
                    // 调用ZXIng开源项目源码  扫描二维码
                    Intent openCameraIntent = new Intent(MainActivity.this,
                            CaptureActivity.class);
                    startActivityForResult(openCameraIntent, 0);
                    //设置TextView的文本信息
                    tv_activityName.setText(tempText);

                }
                break;
            case R.id.btn_qrcodeMake:
                //生成二维码
                if(TextUtils.isEmpty(et_makeText.getText().toString().trim())){
                    Snackbar.make(view,"生成二维码的字符串不能为空",Snackbar.LENGTH_SHORT)
                            .setAction("Action",null).show();
                }else{
                    CreateQRCode(iv_qrcode_img,et_makeText);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * 获取扫描成功后返回的信息
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String tempText=getResources().getString(R.string.qrcode_sign_time);
        // 取得返回信息
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            tempText+=":"+CommonUtil.getSystemTime();
            tv_signTime.setText(tempText);
            et_scanResult.setText(scanResult);
        }
    }

    /**
     * 生成二维码
     * @param iv 显示二维码的ImageView控件
     * @param et 获取EditText生成二维码的字符串
     */
    private void CreateQRCode(ImageView iv,EditText et){
        String uri = et.getText().toString();
        Bitmap bitmap;
        try {
            bitmap = BitmapUtil.createQRCode(uri, mScreenWidth);
            if(bitmap != null){
                iv.setImageBitmap(bitmap);
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存ImageView图片至本地
     * @author Administrator
     * 异步处理
     */
    private class SaveImageTask extends AsyncTask<Bitmap,Void,String> {

        //此方法在后台线程执行，完成图片的保存
        @Override
        protected String doInBackground(Bitmap... params) {

            String result=getResources().getString(R.string.qrcode_save_picture_failed);
            try {
                String sdCardDire= Environment.getExternalStorageDirectory().toString();
                File file=new File(sdCardDire+"/FingertipCampus");
                if(!file.exists()){
                    file.mkdirs();
                }
                File imageFile=new File(file.getAbsolutePath(),"qrcode_"+new Date().getTime()+".jpg");
                FileOutputStream outputStream=null;
                outputStream=new FileOutputStream(imageFile);
                Bitmap image=params[0];
                image.compress(Bitmap.CompressFormat.JPEG,100,outputStream);//压缩图片
                outputStream.flush();
                outputStream.close();
                result=getResources().getString(R.string.qrcode_save_picture_success,file.getAbsolutePath());
                //用广播通知相册更新
                Intent intentNotify=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri=Uri.fromFile(imageFile);
                intentNotify.setData(uri);
                MainActivity.this.sendBroadcast(intentNotify);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();
            //清空画图缓冲区
            iv_qrcode_img.setDrawingCacheEnabled(false);
        }

    }

}
