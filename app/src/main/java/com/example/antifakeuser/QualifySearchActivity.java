package com.example.antifakeuser;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.peersafe.chainsql.core.Chainsql;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.logging.Level;

public class QualifySearchActivity extends AppCompatActivity {
    private Button btnSearch=null;
    private EditText editTextName=null;
    private String name=null;
    private Chainsql cClient = new Chainsql();
    //查询结果，定义全局变量
    private String searchAddress=null;  //商家账户
    private String textAccount=null;    //交易账户
    private int ledgerNum=0;    //区块高度
    private String textHash=null;   //交易HASH
    private String textTime=null;   //交易时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qualify_search);
        editTextName=findViewById(R.id.editText_name);
        btnSearch=findViewById(R.id.button_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name= editTextName.getText().toString();
                if(name.equals(null)) {
                    Toast.makeText(QualifySearchActivity.this, "输入为空！", Toast.LENGTH_LONG).show();
                } else{
                    Handler handler= new Handler() {
                        public void handleMessage(Message msg){
                            if(msg.what==0) {
                                //查询失败弹窗
                                ImageView img = new ImageView(QualifySearchActivity.this);
                                img.setImageResource(R.drawable.ic_error);
                                AlertDialog.Builder builder = new AlertDialog.Builder(QualifySearchActivity.this);
                                builder.setIcon(R.drawable.ic_search)
                                        .setTitle("查询结果")
                                        .setView(img)
                                        .setMessage("\n          该商家无资质！\n")
                                        .setNegativeButton("确定", null);
                                builder.create().show();
                            }else if(msg.what==1) {
                                ImageView img = new ImageView(QualifySearchActivity.this);
                                img.setImageResource(R.drawable.ic_correct);
                                AlertDialog.Builder builder = new AlertDialog.Builder(QualifySearchActivity.this);
                                builder.setIcon(R.drawable.ic_search)
                                        .setTitle("查询结果")
                                        .setView(img)
                                        .setMessage("\n                       查询成功！\n")
                                        .setNegativeButton("返回", null)
                                        .setPositiveButton("查看资质信息", new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //显示查询结果相关信息
                                                String result="商家名称："+name
                                                        +"\n商家账户："+searchAddress
                                                        +"\n------------------------------------------"
                                                        +"\n授权交易区块信息\n"
                                                        +"\n交易账户："+textAccount
                                                        +"\n区块高度："+ledgerNum
                                                        +"\n交易HASH："+textHash
                                                        +"\n交易时间："+textTime
                                                        +"\n";
                                                //溯源结果
                                                AlertDialog.Builder builder = new AlertDialog.Builder(QualifySearchActivity.this);
                                                builder.setIcon(R.drawable.ic_search)
                                                        .setTitle("商家资质信息")
                                                        .setMessage(result)
                                                        .setNegativeButton("确定", null);
                                                builder.create().show();


                                            }
                                        });
                                builder.create().show();
                            }

                        };

                    };
                    search(handler,name);
                }

            }
        });

    }

    private void search(final Handler handler, final String name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                cClient.connect(getString(R.string.severIP_1));
                cClient.connection.client.logger.setLevel(Level.SEVERE);
                cClient.as(getString(R.string.client_address),getString(R.string.client_secret));
                String tableCom = "address_list";
                String str1 = "{'AccountName':'" + name + "'}";
                cClient.use("zEX33AirGeFUyY4H56viye5hp5J9WwKUv3");
                JSONObject obj = cClient.table(tableCom).get(cClient.array(str1)).submit();
                try {
                    if (obj.getString("lines").equals("[]")) {
                        handler.sendEmptyMessage(0);
                    } else {
                        searchLedgerInfor(obj);
                        //!!!待完善
                        handler.sendEmptyMessage(1);

                    }
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }

            }
        }).start();
    }

    private void searchLedgerInfor(final JSONObject obj){
        //从address_list表中获取查询商家对应区块号
        int accountLed= 0;
        String t=null;
        try {
            searchAddress=obj.getJSONArray("lines")
                    .getJSONObject(0).getString("AccountAdd");
            accountLed = Integer.parseInt(obj.getJSONArray("lines")
                    .getJSONObject(0).getString("AccountLedger"));
            t=cClient.getLedger(accountLed).getJSONObject("ledger")
                    .getJSONArray("transactions").get(0).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject tranInfor=cClient.getTransaction(t);
        int timeStamp=0;
        try {
            textAccount=tranInfor.getString("Account");     //交易账户
            textHash=tranInfor.getString("hash");        //交易hash
            ledgerNum=tranInfor.getInt("ledger_index");   //区块高度
            timeStamp = tranInfor.getInt("date");   //时间戳
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //时间戳转日期
        long time= timeStamp+946684800;
        textTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time*1000);
    }


}