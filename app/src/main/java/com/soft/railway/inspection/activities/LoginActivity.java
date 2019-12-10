package com.soft.railway.inspection.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.AreaModel;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.PersonModel;
import com.soft.railway.inspection.models.LineModel;
import com.soft.railway.inspection.models.PointItemModel;
import com.soft.railway.inspection.models.StationModel;
import com.soft.railway.inspection.models.TrainTypeModel;
import com.soft.railway.inspection.models.UnitModel;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.utils.DBUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.FileUtil;
import com.soft.railway.inspection.utils.HttpUtil;
import com.soft.railway.inspection.utils.Log;
import com.soft.railway.inspection.utils.MyApplication;

import static android.Manifest.permission.READ_CONTACTS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private HttpUtil httpUtil = null;
    private DBUtil dbUtil = null;
    private DataUtil dataUtil;
    private UserModel userModel = null;
    private EditText mUserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private final String url = "/app/init";
    private final String loginUrl = "/app/login";
    private boolean isLogin = false;//false没有登录，true已经登录

    private Button settingWZ;
    private Button settingServerWZ;
    private Button settingLocalWZ;
    private TextView showTV;
    private LinearLayout settingLL;
    private Button updateBtn;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showIP();
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        dbUtil = DBUtil.getInstance();
        mUserView = (EditText) findViewById(R.id.user_id);
        mPasswordView = (EditText) findViewById(R.id.password);
        httpUtil = HttpUtil.getInstance();
        dataUtil = DataUtil.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences(MyApplication.USER_NAME, Activity.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        Button mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        SharedPreferences sharedPreferencesIP = getSharedPreferences(MyApplication.DATA_IP, Activity.MODE_PRIVATE);
        DataUtil.IS_DEBUG=sharedPreferencesIP.getBoolean("isDebug",true);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        settingWZ = findViewById(R.id.login_setting_wz);
        settingWZ.setOnClickListener(this);
        settingServerWZ = findViewById(R.id.login_setting_ip_server_btn);
        settingLocalWZ = findViewById(R.id.login_setting_ip_local_btn);
        settingServerWZ.setOnClickListener(this);
        settingLocalWZ.setOnClickListener(this);
        showTV = findViewById(R.id.login_setting_ip_local_tv);
        showIP();
        settingLL = findViewById(R.id.login_setting_ip_ll);
        if (!DataUtil.IS_SETTING_IP) {
            settingLL.setVisibility(View.GONE);
        }
        populateAutoComplete();
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        if (!TextUtils.isEmpty(userId)) {
            showProgress(true);
            mAuthTask = new UserLoginTask(userId, "");
            mAuthTask.execute((Void) null);
            isLogin = true;
        }
        updateBtn = findViewById(R.id.login_setting_update);
        if (DataUtil.IS_UPDATE_VERSION_TEST) {
            updateBtn.setVisibility(View.VISIBLE);
            updateBtn.setOnClickListener(this);
        } else {
            updateBtn.setVisibility(View.GONE);
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mUserView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        } else if (requestCode == 1) {
            Boolean result = true;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                } else {
                    result = false;
                    break;
                }
            }
            if (result) {
                Intent intent = new Intent();
                intent.setClass(this, MainActivity.class);
                startActivity(intent);
            } else {
                finish();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        hideInput();
        if (mAuthTask != null) {
            return;
        }
        mUserView.setError(null);
        mPasswordView.setError(null);
        String userId = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("密码不能为空");
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(userId)) {
            mUserView.setError("用户名不能为空");
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(userId, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserId;
        private final String mPassword;
        private String error;

        UserLoginTask(String userId, String password) {
            mUserId = userId;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            // Simulate network access.
            boolean result = true;
            if (!isLogin) {
                Map<String, String> map = new HashMap<String, String>();
                UserModel userModel = new UserModel();
                userModel.setUserId(mUserId);
                userModel.setPassword(mPassword);
                userModel.setSim(getIMEI());
                map.put("data", JSONObject.toJSONString(userModel));
                Map<String, String> responseMap = httpUtil.synch(loginUrl, httpUtil.TYPE_POST, map);
                String response = responseMap.get("result");
                String errorResponse = responseMap.get("error");
                if ("-1".equals(errorResponse)) {
                    result = false;
                    error = "服务器异常";
                } else {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null) {
                        String errorCode = jsonObject.getString("errorCode");
                        if (!TextUtils.isEmpty(errorCode)) {
                            if ("0".equals(errorCode)) {
                                initData(jsonObject);
                                result = initLocalData();
                                error = "";
                            } else if ("1".equals(errorCode)) {
                                error = "密码错误";
                                result = false;
                            } else if ("2".equals(errorCode)) {
                                error = "用户名不存在";
                                result = false;
                            }
                        }
                    }
                }
            } else {
                localData();
            }
            // TODO: register the new account here.
            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                showProgress(false);
                if (TextUtils.isEmpty(error)) {
                    Toast.makeText(LoginActivity.this, "网络有问题，请查看网络！", Toast.LENGTH_SHORT).show();
                } else {
                    mPasswordView.setError(error);
                    mPasswordView.requestFocus();
                }

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        /**
         * 登录成功，将用户信息存入数据库
         *
         * @param jsonObject
         */
        public void initData(JSONObject jsonObject) {
            DataUtil dataUtil = DataUtil.getInstance();
            String user = jsonObject.getString("user");
            UserModel userModelData = JSONObject.parseObject(user, UserModel.class);
            if (userModel == null)
                userModel = new UserModel();
            userModel.setUserId(userModelData.getUserId());
            userModel.setPassword(mPassword);
            userModel.setSim(getIMEI());
            userModel.setBand(userModelData.getBand());
            userModel.setUserName(userModelData.getUserName());
            userModel.setUnitId(userModelData.getUnitId());
            userModel.setUnitName(userModelData.getUnitName());
            userModel.setPointIds(userModelData.getPointIds());
            SharedPreferences sharedPreferences = getSharedPreferences(MyApplication.USER_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editText = sharedPreferences.edit();
            editText.putString("userId", userModel.getUserId());
            editText.putString("unitId", userModel.getUnitId());
            editText.putString("password", userModel.getPassword());
            editText.putString("userName", userModel.getUserName());
            editText.putString("unitName", userModel.getUnitName());
            editText.putString("band", userModel.getBand());
            editText.putString("pointIds", userModel.getPointIds());
            editText.commit();
            dataUtil.setUser(userModel);
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            MyApplication.finishAll();
            return true;      //调用双击退出函数
        }
        return super.dispatchKeyEvent(event);
    }

    public void localData() {
        setInitDataTemp();
    }

    /**
     * 基础数据处理
     *
     * @return
     */
    public boolean initLocalData() {
        boolean res = true;
        Map<String, String> map = init();
        String error = map.get("error");
        String request = map.get("result");
        if ("0".equals(error)) {
            if (!TextUtils.isEmpty(request)) {
                JSONObject jsonObject = JSONObject.parseObject(request);
                String errorCode = jsonObject.getString("errorCode");
                if ("0".equals(errorCode)) {
                    setData(jsonObject);
                } else {
                    res = false;
                }
            }

        } else if ("-1".equals(error)) {
            res = false;
        }
        return res;
    }

    /**
     * 发送获取基础数据请求
     *
     * @return
     */
    public Map init() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", dataUtil.getUser().getUserId());
        Map paramMap = new HashMap();
        paramMap.put("data", JSONObject.toJSONString(map));
        return httpUtil.synch(url, httpUtil.TYPE_POST, paramMap);
    }

    /**
     * 与服务器同步本地基础数据
     *
     * @param jsonObject
     */
    public void setData(JSONObject jsonObject) {
        JSONObject dataJsonObject = jsonObject;
        //todo 版本更新地址，视频文件加载地址
           /* String Url = dataJsonObject.getString("url");
            if (TextUtils.isEmpty(Url)) {
                DataUtil.VersionUpdateUrl = Url;
            }*/
        setInitData(dataJsonObject);
        List<FileModel> fileModelList = new ArrayList<>();
        Map<String, FileModel> fileModelMap = new HashMap<>();
        Cursor fileCursor = dbUtil.select("select * from " + DataUtil.TableNameEnum.SUBMITFILE.toString() + " where filestatus=? group by workid,filetime,filerank ORDER BY filetime,filerank  ", new String[]{FileUtil.FILE_STATUS_WAIT_UPLOADED});
        if (fileCursor != null) {
            while (fileCursor.moveToNext()) {
                FileModel fileModel = new FileModel(fileCursor);
                fileModelList.add(fileModel);
                fileModelMap.put(fileModel.getFileId(), fileModel);
            }
            fileCursor.close();
        }
        dataUtil.setFileModelList(fileModelList);
        dataUtil.setFileModelMap(fileModelMap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void setInitData(JSONObject dataJsonObject) {
        SharedPreferences versionPreferences = getSharedPreferences(MyApplication.DATA_VERSION, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = versionPreferences.edit();
        String personVersion = dataJsonObject.getString("personVersion");
        String routeVersion = dataJsonObject.getString("routeVersion");
        String areaVersion = dataJsonObject.getString("areaVersion");
        String unitVersion = dataJsonObject.getString("unitVersion");
        String pointVersion = dataJsonObject.getString("pointVersion");
        String trainTypeVersion = dataJsonObject.getString("trainTypeVersion");
        dbUtil.createTable();
        Map<String, UnitModel> unitMap = new HashMap<String, UnitModel>();
        List<UnitModel> unitList = new ArrayList<UnitModel>();
        JSONArray jsonArray = dataJsonObject.getJSONArray("units");
        if (jsonArray != null) {
            int num = jsonArray.size();
            if (num > 0) {
                String goup = DataUtil.TableNameEnum.UNIT.toString();
                dbUtil.deleteAll(goup);
                dbUtil.db.beginTransaction();
                for (int i = 0; i < num; i++) {
                    UnitModel unitModel = jsonArray.getObject(i, UnitModel.class);
                    String unitId = unitModel.getgId();
                    unitMap.put(unitId, unitModel);
                    ContentValues contentValues = unitModel.getContentValues(unitModel);
                    dbUtil.db.insert(goup, null, contentValues);
                    unitList.add(unitModel);
                }
                dbUtil.db.setTransactionSuccessful();
                dbUtil.db.endTransaction();
            }
        }
        dataUtil.setUnitMap(unitMap);
        dataUtil.setUnitList(unitList);
        Map<String, LineModel> lineMap = new HashMap<String, LineModel>();
        List<LineModel> lineList = new ArrayList<LineModel>();
        JSONArray lineJsonArray = dataJsonObject.getJSONArray("lines");
        if (lineJsonArray != null) {
            int num = lineJsonArray.size();
            if (num > 0) {
                String line = DataUtil.TableNameEnum.LINE.toString();
                dbUtil.deleteAll(line);
                dbUtil.db.beginTransaction();
                for (int i = 0; i < num; i++) {
                    LineModel lineModel = lineJsonArray.getObject(i, LineModel.class);
                    String Id = lineModel.getLineId();
                    lineMap.put(Id, lineModel);
                    dbUtil.db.insert(line, null, lineModel.getContentValues(lineModel));
                    lineList.add(lineModel);
                }
                dbUtil.db.setTransactionSuccessful();
                dbUtil.db.endTransaction();
            }
        }
        dataUtil.setLineMap(lineMap);
        dataUtil.setLineList(lineList);
        Map<String, StationModel> stationMap = new HashMap<String, StationModel>();
        List<StationModel> stationList = new ArrayList<StationModel>();
        JSONArray stationJsonArray = dataJsonObject.getJSONArray("stations");
        if (stationJsonArray != null) {
            int num = stationJsonArray.size();
            if (num > 0) {
                String station = DataUtil.TableNameEnum.STATION.toString();
                dbUtil.deleteAll(station);
                dbUtil.db.beginTransaction();
                for (int i = 0; i < num; i++) {
                    StationModel stationModel = stationJsonArray.getObject(i, StationModel.class);
                    String Id = stationModel.getStationId();
                    stationMap.put(Id, stationModel);
                    dbUtil.db.insert(station, null, stationModel.getContentValues(stationModel));
                    stationList.add(stationModel);
                }
                dbUtil.db.setTransactionSuccessful();
                dbUtil.db.endTransaction();
            }
        }
        dataUtil.setStationMap(stationMap);
        dataUtil.setStationList(stationList);
        Map<String, PersonModel> driverMap = new HashMap<String, PersonModel>();
        List<PersonModel> driverList = new ArrayList<PersonModel>();
        JSONArray driverJsonArray = dataJsonObject.getJSONArray("persons");
        if (driverJsonArray != null) {
            int num = driverJsonArray.size();
            if (num > 0) {
                String driver = DataUtil.TableNameEnum.PERSON.toString();
                dbUtil.deleteAll(driver);
                dbUtil.db.beginTransaction();
                for (int i = 0; i < num; i++) {
                    PersonModel personModel = driverJsonArray.getObject(i, PersonModel.class);
                    String Id = personModel.getPersonId();
                    driverMap.put(Id, personModel);
                    driverList.add(personModel);
                    dbUtil.db.insert(driver, null, personModel.getContentValues(personModel));
                }
                dbUtil.db.setTransactionSuccessful();
                dbUtil.db.endTransaction();
            }
        }
        dataUtil.setDriverMap(driverMap);
        dataUtil.setDriverList(driverList);
        Map<String, PointItemModel> pointMap = new HashMap<>();
        List<PointItemModel> pointList = new ArrayList<>();
        JSONArray pointJsonArray = dataJsonObject.getJSONArray("points");
        if (pointJsonArray != null) {
            int num = pointJsonArray.size();
            if (num > 0) {
                String point = DataUtil.TableNameEnum.POINTITEM.toString();
                dbUtil.deleteAll(point);
                dbUtil.db.beginTransaction();
                for (int i = 0; i < num; i++) {
                    PointItemModel pointModel = pointJsonArray.getObject(i, PointItemModel.class);
                    String Id = pointModel.getItemTypeId();
                    pointMap.put(Id, pointModel);
                    pointList.add(pointModel);
                    dbUtil.db.insert(point, null, pointModel.getContentValues(pointModel));
                }
                dbUtil.db.setTransactionSuccessful();
                dbUtil.db.endTransaction();
            }
        }

        dataUtil.setPointMap(pointMap);
        dataUtil.setPointList(pointList);
        Map<String, TrainTypeModel> trainTypeMap = new HashMap<String, TrainTypeModel>();
        List<TrainTypeModel> trainTypeList = new ArrayList<TrainTypeModel>();
        JSONArray trainTypeJsonArray = dataJsonObject.getJSONArray("trainTypes");
        if (trainTypeJsonArray != null) {
            int num = trainTypeJsonArray.size();
            if (num > 0) {
                String trainType = DataUtil.TableNameEnum.TRAIN_TYPE.toString();
                dbUtil.deleteAll(trainType);
                dbUtil.db.beginTransaction();
                for (int i = 0; i < num; i++) {
                    TrainTypeModel trainTypeModel = trainTypeJsonArray.getObject(i, TrainTypeModel.class);
                    String Id = trainTypeModel.getTrainTypeId();
                    trainTypeMap.put(Id, trainTypeModel);
                    trainTypeList.add(trainTypeModel);
                    dbUtil.db.insert(trainType, null, trainTypeModel.getContentValues(trainTypeModel));
                }
                dbUtil.db.setTransactionSuccessful();
                dbUtil.db.endTransaction();
            }

        }
        dataUtil.setTrainTypeMap(trainTypeMap);
        dataUtil.setTrainTypeList(trainTypeList);
        List<AreaModel> areaList = new ArrayList<>();
        Map<String, AreaModel> areaMap = new HashMap<>();
        JSONArray areaJSONArray = dataJsonObject.getJSONArray("areas");
        if (areaJSONArray != null) {
            int area = areaJSONArray.size();
            if (area > 0) {
                String areaTableName = DataUtil.TableNameEnum.AREA.toString();
                dbUtil.deleteAll(areaTableName);
                dbUtil.db.beginTransaction();
                for (int i = 0; i < area; i++) {
                    AreaModel areaModel = areaJSONArray.getObject(i, AreaModel.class);
                    areaList.add(areaModel);
                    areaMap.put(areaModel.getAreaId(), areaModel);
                    dbUtil.db.insert(areaTableName, null, areaModel.getContentValues(areaModel));
                }
                dbUtil.db.setTransactionSuccessful();
                dbUtil.db.endTransaction();
            }
        }
        dataUtil.setAreaList(areaList);
        dataUtil.setAreaMap(areaMap);
        editor.putString("personVersion", personVersion);
        editor.putString("routeVersion", routeVersion);
        editor.putString("areaVersion", areaVersion);
        editor.putString("unitVersion", unitVersion);
        editor.putString("pointVersion", pointVersion);
        editor.putString("trainTypeVersion", trainTypeVersion);
        editor.commit();
        dataUtil.setVersionMap(versionPreferences);
    }

    public void setInitDataTemp() {
        dbUtil.createTable();
        Map<String, UnitModel> unitMap = new HashMap<String, UnitModel>();
        List<UnitModel> unitList = new ArrayList<UnitModel>();
        Cursor unitCursor = dbUtil.selectALL(DataUtil.TableNameEnum.UNIT.toString());
        if (unitCursor != null) {
            while (unitCursor.moveToNext()) {
                UnitModel unitModel = new UnitModel(unitCursor);
                unitList.add(unitModel);
                unitMap.put(unitModel.getgId(), unitModel);
            }
            unitCursor.close();
        }
        dataUtil.setUnitMap(unitMap);
        dataUtil.setUnitList(unitList);
        Map<String, LineModel> lineMap = new HashMap<String, LineModel>();
        List<LineModel> lineList = new ArrayList<LineModel>();
        Cursor lineCursor = dbUtil.selectALL(DataUtil.TableNameEnum.LINE.toString());
        if (lineCursor != null) {
            while (lineCursor.moveToNext()) {
                LineModel lineModel = new LineModel(lineCursor);
                lineList.add(lineModel);
                lineMap.put(lineModel.getLineId(), lineModel);
            }
            lineCursor.close();
        }
        dataUtil.setLineMap(lineMap);
        dataUtil.setLineList(lineList);
        Map<String, StationModel> stationMap = new HashMap<String, StationModel>();
        List<StationModel> stationList = new ArrayList<StationModel>();
        Cursor stationCursor = dbUtil.selectALL(DataUtil.TableNameEnum.STATION.toString());
        if (stationCursor != null) {
            while (stationCursor.moveToNext()) {
                StationModel stationModel = new StationModel(stationCursor);
                stationList.add(stationModel);
                stationMap.put(stationModel.getStationId(), stationModel);
            }
            stationCursor.close();
        }
        dataUtil.setStationMap(stationMap);
        dataUtil.setStationList(stationList);
        Map<String, PersonModel> driverMap = new HashMap<String, PersonModel>();
        List<PersonModel> driverList = new ArrayList<PersonModel>();
        Cursor personCursor = dbUtil.selectALL(DataUtil.TableNameEnum.PERSON.toString());
        if (personCursor != null) {
            while (personCursor.moveToNext()) {
                PersonModel personModel = new PersonModel(personCursor);
                driverList.add(personModel);
                driverMap.put(personModel.getPersonId(), personModel);
            }
            personCursor.close();
        }
        dataUtil.setDriverMap(driverMap);
        dataUtil.setDriverList(driverList);
        Map<String, PointItemModel> pointMap = new HashMap<>();
        List<PointItemModel> pointList = new ArrayList<>();
        Cursor pointItemCursor = dbUtil.selectALL(DataUtil.TableNameEnum.POINTITEM.toString());
        if (pointItemCursor != null) {
            while (pointItemCursor.moveToNext()) {
                PointItemModel pointItemModel = new PointItemModel(pointItemCursor);
                pointList.add(pointItemModel);
                pointMap.put(pointItemModel.getItemTypeId(), pointItemModel);
            }
            pointItemCursor.close();
        }
        dataUtil.setPointMap(pointMap);
        dataUtil.setPointList(pointList);
        Map<String, TrainTypeModel> trainTypeMap = new HashMap<String, TrainTypeModel>();
        List<TrainTypeModel> trainTypeList = new ArrayList<TrainTypeModel>();
        Cursor cursor = dbUtil.selectALL(DataUtil.TableNameEnum.TRAIN_TYPE.toString());
        if (cursor != null) {
            while (cursor.moveToNext()) {
                TrainTypeModel trainTypeModel = new TrainTypeModel(cursor);
                trainTypeList.add(trainTypeModel);
                trainTypeMap.put(trainTypeModel.getTrainTypeId(), trainTypeModel);
            }
            cursor.close();
        }
        dataUtil.setTrainTypeMap(trainTypeMap);
        dataUtil.setTrainTypeList(trainTypeList);
        List<AreaModel> areaList = new ArrayList<>();
        Map<String, AreaModel> areaMap = new HashMap<>();
        Cursor areaCursor = dbUtil.selectALL(DataUtil.TableNameEnum.AREA.toString());
        if (areaCursor != null) {
            while (areaCursor.moveToNext()) {
                AreaModel areaModel = new AreaModel(areaCursor);
                areaList.add(areaModel);
                areaMap.put(areaModel.getAreaId(), areaModel);
            }
            areaCursor.close();
        }
        dataUtil.setAreaList(areaList);
        dataUtil.setAreaMap(areaMap);
        List<FileModel> fileModelList = new ArrayList<>();
        Map<String, FileModel> fileModelMap = new HashMap<>();
        Cursor fileCursor = dbUtil.select("select * from " + DataUtil.TableNameEnum.SUBMITFILE.toString() + " where filestatus=? group by workid,filetime,filerank ORDER BY filetime,filerank  ", new String[]{FileUtil.FILE_STATUS_WAIT_UPLOADED});
        if (fileCursor != null) {
            while (fileCursor.moveToNext()) {
                FileModel fileModel = new FileModel(fileCursor);
                fileModelList.add(fileModel);
                fileModelMap.put(fileModel.getFileId(), fileModel);
            }
            fileCursor.close();
        }
        dataUtil.setFileModelList(fileModelList);
        dataUtil.setFileModelMap(fileModelMap);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNoDoubleClick(View v) {
        switch (v.getId()) {
            case R.id.login_setting_wz:
                SettingWZ();
                break;
            case R.id.login_setting_update:
                GoUpdate();
                break;
            case R.id.login_setting_ip_local_btn:
                changeIP(false);
                break;
            case R.id.login_setting_ip_server_btn:
                changeIP(true);
                break;
        }
    }

    public String getIMEI() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
        }
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();
        return szImei;
    }

    public void SettingWZ() {
        final EditText editText = new EditText(LoginActivity.this);
        editText.setText(HttpUtil.BASE_PATH_OTHER);
        editText.setSelection(HttpUtil.BASE_PATH_OTHER.length());
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("设置IP地址")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (DataUtil.DEBUG) {
                            HttpUtil.BASE_PATH_OTHER = editText.getText().toString();
                            SharedPreferences sharedPreferences = getSharedPreferences(MyApplication.DATA_IP, Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editText = sharedPreferences.edit();
                            editText.putString("ip", HttpUtil.BASE_PATH_OTHER);
                            Log.d("HttpUtil.BASE_PATH_OTHER   " + HttpUtil.BASE_PATH_OTHER);
                            editText.commit();
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void GoUpdate() {
        Intent intent = new Intent(LoginActivity.this, VersionUpdateActivity.class);
        startActivity(intent);
    }

    private void changeIP(boolean isLocal) {
        DataUtil.IS_DEBUG = isLocal;
        SharedPreferences sharedPreferences = getSharedPreferences(MyApplication.DATA_IP, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editText = sharedPreferences.edit();
        editText.putBoolean("isDebug", DataUtil.IS_DEBUG);
        editText.commit();
        handler.sendEmptyMessage(0);
    }

    public void showIP() {
        if (DataUtil.DEBUG) {
            //todo 本地地址
            HttpUtil.BASE_PATH = HttpUtil.BASE_PATH_OTHER;
            if (DataUtil.IS_DEBUG) {
                HttpUtil.BASE_PATH = HttpUtil.BASE_PATH_SERVER;
            }
        } else {
            HttpUtil.BASE_PATH = HttpUtil.BASE_PATH_MYSELF;
        }
        showTV.setText(HttpUtil.BASE_PATH);
    }

}

