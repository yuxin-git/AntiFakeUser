package com.example.antifakeuser.funClass;

import com.peersafe.chainsql.core.Chainsql;
import com.peersafe.chainsql.core.Submit;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;

public class HashOperation {
    public String address=null;
    public String secret=null;
    private Chainsql c = new Chainsql();

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
    //将交易所在区块高度添加到com_infor表中的对应Ledger项
    //type=1登记生产信息区块，type=2登记销售信息区块，type=3登记质检信息区块
    public void record(int id,int type,int ledger){
        c.connect("ws://192.168.190.129:6006");
        c.connection.client.logger.setLevel(Level.SEVERE);
        c.as(address,secret);
        c.use("zEX33AirGeFUyY4H56viye5hp5J9WwKUv3");    //品牌商账户地址
        String str="{ID:"+ id +"}";
        switch (type){
            case 1: //生产信息
                String str1="{'ManuLedger':'"+ledger+"'}";
                String table="com_infor";
                c.table(table).get(c.array(str))
                        .update(str1).submit(Submit.SyncCond.db_success);
                break;
            case 2: //销售信息
                String str2="{'DealerLedger':'"+ledger+"'}";
                c.table("com_infor").get(c.array(str))
                        .update(str2).submit(Submit.SyncCond.db_success);
                break;
            case 3: //质检信息
                String str3="{'ReguLedger':'"+ledger+"'}";
                c.table("com_infor").get(c.array(str))
                        .update(str3).submit(Submit.SyncCond.db_success);
                break;
        }

    }

    //获取生产信息区块
    public JSONObject getManuLedgerInfor(int id){
        c.connect("ws://192.168.190.129:6006");
        c.connection.client.logger.setLevel(Level.SEVERE);
        c.as("zpncip6ZUTrfqz3nSHpix4eQyMS4F1KM83","xnaUqqt6Nb6ysdyXWewu7u2UxBAAD");
        c.use("zEX33AirGeFUyY4H56viye5hp5J9WwKUv3");
        String str="{ID:"+ id +"}";
        JSONObject result = null;
        JSONObject obj = c.table("com_infor").get(c.array(str)).submit();
        try {
            int manuLed= Integer.parseInt(obj.getJSONArray("lines")
                    .getJSONObject(0).getString("ManuLedger"));
            result=c.getLedger(manuLed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;

    }
    //获取销售信息区块
    public JSONObject getDealerLedgerInfor(int id){
        c.connect("ws://192.168.190.129:6006");
        c.connection.client.logger.setLevel(Level.SEVERE);
        c.as("zpncip6ZUTrfqz3nSHpix4eQyMS4F1KM83","xnaUqqt6Nb6ysdyXWewu7u2UxBAAD");
        c.use("zEX33AirGeFUyY4H56viye5hp5J9WwKUv3");
        String str="{ID:"+ id +"}";
        JSONObject result = null;
        JSONObject obj = c.table("com_infor").get(c.array(str)).submit();
        try {
            int dealerLed= Integer.parseInt(obj.getJSONArray("lines")
                    .getJSONObject(0).getString("DealerLedger"));
            result=c.getLedger(dealerLed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    //获取质检信息区块
    public JSONObject getReguLedgerInfor(int id){
        c.connect("ws://192.168.190.129:6006");
        c.connection.client.logger.setLevel(Level.SEVERE);
        c.as("zpncip6ZUTrfqz3nSHpix4eQyMS4F1KM83","xnaUqqt6Nb6ysdyXWewu7u2UxBAAD");
        c.use("zEX33AirGeFUyY4H56viye5hp5J9WwKUv3");
        String str="{ID:"+ id +"}";
        JSONObject result = null;
        JSONObject obj = c.table("com_infor").get(c.array(str)).submit();
        try {
            int reguLed= Integer.parseInt(obj.getJSONArray("lines")
                    .getJSONObject(0).getString("ReguLedger"));
            result=c.getLedger(reguLed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
