package com.soft.railway.inspection.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DBUtil {
    private static DBUtil dbUtil;
    public SQLiteDatabase db;
    public static final String DB_NULL = "";
    private static final String title = "CREATE TABLE ";
    public static Map<String, String> tableNameMap;


    public boolean updateDatabase(String tableName){
        // TODO Auto-generated method stub
        HaveData();
        createTable();
        boolean result=true;
        db.beginTransaction();
        try{
            //改名数据库表
            db.execSQL("alter table "+tableName+" rename to "+tableName+2);
            //新建表单
            db.execSQL("create table "+tableName+"(" +
                    "  `workid` varchar(60) ," +
                    "  `worktype` varchar(5) NOT NULL," +
                    "  `workstatus` varchar(5) NOT NULL," +
                    "  `starttime` varchar(50) ," +
                    "  `endtime` varchar(50) ," +
                    "  `userid` varchar(50)  NOT NULL ," +
                    "  `unitid` varchar(50)  NOT NULL ," +
                    "  `worksuggest` varchar(255)  ," +
                    "  `areas` varchar(255) ," +
                    "  `routestations` varchar(255) ," +
                    "  `gpsfilename` varchar(255) ," +
                    "  `capturephotos` varchar(255) ," +
                    "  `keyman` varchar(255) ," +
                    "  `traininfo` varchar(255) ," +
                    "  `missiontrain` varchar(10), " +
                    "  `teach`  varchar(100)," +
                    "  `workexplain` varchar(6),"+
                    " `recorders` varchar(500), "+
                    " `planworkitems` varchar(500) "
                    + ")");

            //插入原有的数据
            db.execSQL("insert into "+tableName+ " select *,'','' from "+tableName+2);
            //如果增加了列属性，则使用双引号”” 来补充原来不存在的数据
            //删除临时表单
            db.execSQL("drop table "+tableName+2);

        }catch (Exception e){
            db.execSQL("drop table "+tableName);
            db.execSQL("alter table "+tableName+2+" rename to "+tableName);
            result=false;
        }finally {
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        return result;
    }


    private Map<String, String> hadTableNameMap = new HashMap();

    private DBUtil() {
        if (db == null) {
            String path = DataUtil.DB_PATH;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            db = SQLiteDatabase.openOrCreateDatabase(path + "/jzgk.db", null);
            HaveData();
        }
    }

    public static DBUtil getInstance() {
        if (dbUtil == null) {
            synchronized (DBUtil.class) {
                if (dbUtil == null) {
                    dbUtil = new DBUtil();
                    if(tableNameMap==null||tableNameMap.size()<=0){
                        tableNameMap = new HashMap<String, String>() {
                        };
                        tableNameMap.put(DataUtil.TableNameEnum.TRAIN_TYPE.toString(), title + DataUtil.TableNameEnum.TRAIN_TYPE.toString() + "(" +
                                "traintypeid char(3) ," +
                                "traintypename varchar(16) NOT NULL )");
                        tableNameMap.put(DataUtil.TableNameEnum.POINTITEM.toString(), title + DataUtil.TableNameEnum.POINTITEM.toString() + "("
                                + "`itemtypeid`   varchar(60)  ," +
                                "`itemtypename`  varchar(255) NOT NULL," +
                                "`itemtypecategory` varchar(255) NOT NULL," +
                                "`itemnumber` varchar(255) NOT NULL," +
                                " `isself` varchar(5) NOT NULL"
                                + ")");
                        tableNameMap.put(DataUtil.TableNameEnum.STATION.toString(), title + DataUtil.TableNameEnum.STATION.toString() + "(" +
                                "`stationid`  varchar(60)  ," +
                                "`stationname`  varchar(255) ," +
                                "`lineid`  varchar(20)  NOT NULL" + ")");
                        tableNameMap.put(DataUtil.TableNameEnum.UNIT.toString(), title + DataUtil.TableNameEnum.UNIT.toString() + "(" +
                                "`gid`  varchar(100)  ," +
                                "`gname`  varchar(40)  NOT NULL ," +
                                "`parentid` varchar(100) ," +
                                "`level` varchar(30)"
                                + ")");
                        tableNameMap.put(DataUtil.TableNameEnum.AREA.toString(), title + DataUtil.TableNameEnum.AREA.toString() + "(" +
                                "`areaid`  varchar(60)  ," +
                                "`areaname`  varchar(255)"
                                + ")");
                        tableNameMap.put(DataUtil.TableNameEnum.LINE.toString(), title + DataUtil.TableNameEnum.LINE.toString() + "(" +
                                "`lineid`  varchar(60)  ," +
                                "`linename`  varchar(255)   " + ")");

                        tableNameMap.put(DataUtil.TableNameEnum.PERSON.toString(), title + DataUtil.TableNameEnum.PERSON.toString() + "(" +
                                "`personid`  varchar(20) ," +
                                "`personname`  varchar(200)   ," +
                                "`band`  varchar(20)   ," +
                                "`depname`  varchar(20)   ," +
                                "`unitid`  varchar(20)   ," +
                                "`unitname`  varchar(20)"
                                + ")");
                        tableNameMap.put(DataUtil.TableNameEnum.WORK.toString(), title + DataUtil.TableNameEnum.WORK.toString() + "(" +
                                "  `workid` varchar(60) ," +
                                "  `worktype` varchar(5) NOT NULL," +
                                "  `workstatus` varchar(5) NOT NULL," +
                                "  `starttime` varchar(50) ," +
                                "  `endtime` varchar(50) ," +
                                "  `userid` varchar(50)  NOT NULL ," +
                                "  `unitid` varchar(50)  NOT NULL ," +
                                "  `worksuggest` varchar(255)  ," +
                                "  `areas` varchar(255) ," +
                                "  `routestations` varchar(255) ," +
                                "  `gpsfilename` varchar(255) ," +
                                "  `capturephotos` varchar(255) ," +
                                "  `keyman` varchar(255) ," +
                                "  `traininfo` varchar(255) ," +
                                "  `missiontrain` varchar(10), " +
                                "  `teach`  varchar(100)," +
                                "  `workexplain` varchar(6),"+
                                " `recorders` varchar(500), "+
                                " `planworkitems` varchar(500) " +
                                ")");
                        tableNameMap.put(DataUtil.TableNameEnum.ITEM.toString(), title + DataUtil.TableNameEnum.ITEM.toString() + "(" +
                                "`itemid` varchar(60)  ," +
                                "`pointid`  int(11) NOT NULL ," +
                                "`pointcontent`  varchar(255)   ," +
                                "`inserttime`  varchar(255) ," +
                                "`photos`  varchar(255)   ," +
                                "`unitid` varchar(50) ," +
                                "`unitname` varchar(50) ," +
                                "`peoples`  varchar(255)   ," +
                                "`userid`  varchar(50)  NOT NULL ," +
                                "`videos`  varchar(255)   ," +
                                "`remark`  varchar(255)   ," +
                                "`workid`  varchar(50)   ," +
                                "`isself`  varchar(50)   ," +
                                "`itemtraininfo`  varchar(500)   ," +
                                "`card`  varchar(255) " +
                                ")");
                        tableNameMap.put(DataUtil.TableNameEnum.SUBMITFILE.toString(), title + DataUtil.TableNameEnum.SUBMITFILE.toString() +
                                "(" +
                                "'fileid' varchar(60)  PRIMARY KEY ," +
                                "'filename'  varchar(255) NOT NULL ," +
                                "'filepath'  varchar(255) NOT NULL ," +
                                "'filetime'  varchar(255) NOT NULL ," +
                                "'filestatus'  varchar(30) NOT NULL ," +
                                "'userid'  varchar(30) NOT NULL ," +
                                "'filetype'  varchar(30) NOT NULL ," +
                                "'workid'  varchar(30) NOT NULL ," +
                                "'itemid'  varchar(30) NOT NULL ," +
                                "'filerank' varchar(30) NOT NULL"
                                + ")");
                    }
                }
            }
        }
        return dbUtil;
    }

    public boolean delete(String table, String whereClause, String[] whereArgs) {
        int i = db.delete(table, whereClause, whereArgs);
        if (i >= 0) {
            return true;
        }
        return false;
    }

    public void deleteAll(String table) {
        db.execSQL("delete from " + table);
    }

    public boolean insert(String table, ContentValues values) {
        long ret = db.insert(table, DBUtil.DB_NULL, values);
        if (ret != -1) {
            return true;
        }
        return false;
    }

    public boolean update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        int i = db.update(table, values, whereClause, whereArgs);
        if (i > 0) {
            return true;
        }
        return false;
    }

    public Cursor select(String sql, String[] selectionArgs) {
        return db.rawQuery(sql, selectionArgs);
    }

    public Cursor selectALL(String tableName) {
        return db.rawQuery("select * from " + tableName, null);
    }

    public void HaveData() {
        Cursor cursor;
        cursor = db.rawQuery("select name from sqlite_master where type='table' ", null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            hadTableNameMap.put(name, name);
        }
    }

    public void createTable() {
        for (Map.Entry<String, String> o : tableNameMap.entrySet()
        ) {
            if (TextUtils.isEmpty(hadTableNameMap.get(o.getKey()))) {
                db.execSQL(o.getValue());
            }
        }
    }
}
