package com.example.antifakeuser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.peersafe.chainsql.core.Chainsql;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;

public class ComSearchActivity extends AppCompatActivity {

    private EditText editTextId=null;
    private ImageButton btnScan=null;
    private Button btnSearch=null;
    private Integer id=null;
    public Chainsql cClient = new Chainsql();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_search);
        editTextId=findViewById(R.id.editText_id);
        btnScan=findViewById(R.id.imageButton_scan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(ComSearchActivity.this);
                intentIntegrator.setBeepEnabled(true);
                /*设置启动我们自定义的扫描活动，若不设置，将启动默认活动*/
                intentIntegrator.setCaptureActivity(ScanActivity.class);
                intentIntegrator.initiateScan();
            }
        });

        btnSearch=findViewById(R.id.button_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id= Integer.valueOf(editTextId.getText().toString());
                if(null==id) {
                    Toast.makeText(ComSearchActivity.this, "输入为空！", Toast.LENGTH_LONG).show();
                } else{
                    Handler handler= new Handler() {
                        public void handleMessage(Message msg){
                            if(msg.what==0) {
                                //查询失败弹窗
                                ImageView img = new ImageView(ComSearchActivity.this);
                                img.setImageResource(R.drawable.ic_error);
                                AlertDialog.Builder builder = new AlertDialog.Builder(ComSearchActivity.this);
                                builder.setIcon(R.drawable.ic_search)
                                        .setTitle("查询结果")
                                        .setView(img)
                                        .setMessage("\n          查询失败,该商品ID不存在！\n")
                                        .setNegativeButton("确定", null);
                                builder.create().show();
                            }else if(msg.what==1) {
                                ImageView img = new ImageView(ComSearchActivity.this);
                                img.setImageResource(R.drawable.ic_correct);
                                AlertDialog.Builder builder = new AlertDialog.Builder(ComSearchActivity.this);
                                builder.setIcon(R.drawable.ic_search)
                                        .setTitle("查询结果")
                                        .setView(img)
                                        .setMessage("\n                       查询成功！\n")
                                        .setNegativeButton("返回", null)
                                        .setPositiveButton("查看防伪溯源结果", new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //溯源结果

                                                Intent intent=new Intent(ComSearchActivity.this, ComSearchResultActivity.class);
                                                Bundle bundle=new Bundle();
                                                bundle.putString("id", String.valueOf(id));
                                                intent.putExtras(bundle);
                                                startActivity(intent);

/*
                                                AlertDialog.Builder builder = new AlertDialog.Builder(SearchInfomationActivity.this);
                                                builder.setIcon(R.drawable.ic_search)
                                                        .setTitle("防伪溯源结果")
                                                        .setMessage(result)
                                                        .setNegativeButton("确定", null);
                                                builder.create().show();


 */
                                            }
                                        });
                                builder.create().show();
                            }

                        };

                    };
                    search(handler,id);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                editTextId.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void search(final Handler handler, final int id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                cClient.connect(getString(R.string.severIP_2));
                cClient.connection.client.logger.setLevel(Level.SEVERE);
                cClient.as(getString(R.string.client_address),getString(R.string.client_secret));
                String tableCom = "com_infor";
                String str1 = "{'id':" + id + "}";
                cClient.use("zEX33AirGeFUyY4H56viye5hp5J9WwKUv3");
                JSONObject obj = cClient.table(tableCom).get(cClient.array(str1)).submit();
                try {
                    if (obj.getString("lines").equals("[]")) {
                        handler.sendEmptyMessage(0);
                    } else {
                        handler.sendEmptyMessage(1);

                    }
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }

            }
        }).start();
    }



}