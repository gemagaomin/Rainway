package com.soft.railway.inspection.activities;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.FileUtil;
import com.soft.railway.inspection.utils.Log;
import com.soft.railway.inspection.utils.MyException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import pl.droidsonroids.gif.GifImageView;

public class ShowPhotoVideoActivity extends BaseActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private FileModel fileModel;
    private boolean isFinish=true;
    private ImageView downloadIV;
    private ImageView goBackIV;
    private int width=1080;
    private int height=1920;
    private LinearLayout videoLL;
    private Uri uri;
    private VideoView vv;
    private ImageView startBtn;
    private GifImageView refreshIV;
    private LinearLayout photoLL;
    private SimpleDraweeView sdv;
    private static final int SHOW_REFRESH=0;
    private static final int HIDE_SHOW_REFRESH=1;
    private static final int HIDE_BTN=2;
    private static final int SHOW_BTN=3;
    private static final int FILE_TYPE=4;
    private static final int HIDE_DOWN=5;
    private static final int SHOW_DOWN_REFRESH=7;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what=msg.what;
            switch (what){
                case SHOW_REFRESH:
                    refreshIV.setVisibility(View.VISIBLE);
                    startBtn.setVisibility(View.GONE);
                    break;
                case HIDE_SHOW_REFRESH:
                    refreshIV.setVisibility(View.GONE);
                    startBtn.setVisibility(View.VISIBLE);
                    break;
                case SHOW_BTN:
                    startBtn.setVisibility(View.VISIBLE);
                    break;
                case HIDE_BTN:
                    startBtn.setVisibility(View.GONE);
                    break;
                case FILE_TYPE:
                    setViewShow();
                    break;
                case HIDE_DOWN:
                    downloadIV.setVisibility(View.GONE);
                    refreshIV.setVisibility(View.GONE);
                    break;
                case SHOW_DOWN_REFRESH:
                    refreshIV.setVisibility(View.VISIBLE);
                    break;
            }

        }

    };

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo_video);
        initData();
        initView();
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.edit_show_photo_video_download_iv:
                handler.sendEmptyMessage(SHOW_DOWN_REFRESH);
                Uri uri=Uri.parse(fileModel.getFilePath());
                downLoadImg(uri);
                break;
            case R.id.edit_show_goback:
                finish();
                break;
            case R.id.edit_show_start_video:
                handler.sendEmptyMessage(HIDE_BTN);
                vv.seekTo(0);
                vv.requestFocus();
                vv.start();
                break;
            case R.id.edit_show_photo_linear_layout:
                finish();
                break;
        }
    }

    public void initData(){
        Intent intent = getIntent();
        isFinish= intent.getBooleanExtra("isfinish",false);
        fileModel=(FileModel)intent.getSerializableExtra("data");
        if(fileModel!=null){
            initList();
        }
    }

    public void initView(){
        videoLL=(LinearLayout)findViewById(R.id.edit_show_video_linear_layout);
        photoLL=(LinearLayout)findViewById(R.id.edit_show_photo_linear_layout);
        goBackIV=findViewById(R.id.edit_show_goback);
        refreshIV=(GifImageView)findViewById(R.id.edit_show_refresh);
        downloadIV=findViewById(R.id.edit_show_photo_video_download_iv);
        downloadIV.setOnClickListener(this);
        goBackIV.setOnClickListener(this);
        setViewShow();
        if(FileUtil.FILE_TYPE_VIDEO.equals(fileModel.getFileType())){
            videoLL.setVisibility(View.VISIBLE);
            photoLL.setVisibility(View.GONE);
            vv=(VideoView)findViewById(R.id.edit_show_video);
            String url=fileModel.getFilePath();
            startBtn=(ImageView)findViewById(R.id.edit_show_start_video);
            handler.sendEmptyMessage(SHOW_REFRESH);
            if(isFinish){
                uri=Uri.parse(url);
            }else{
                String path=fileModel.getFilePath();
                File file=new File(path);
                uri= FileProvider.getUriForFile(this,"com.soft.railway.inspection.fileProvider",file);
            }
            vv.setVideoURI(uri);
            vv.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    // handler.sendEmptyMessage(HIDE_SHOW_REFRESH);
                    Log.d("setOnInfoListener");
                    return false;
                }
            });
            vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d("setOnCompletionListener");
                    handler.sendEmptyMessage(SHOW_BTN);
                }
            });
            vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d("setOnPreparedListener");
                    handler.sendEmptyMessage(HIDE_SHOW_REFRESH);
                }
            });
            final MediaController mc = new MediaController(this);
            vv.setMediaController(mc);
            startBtn.setOnClickListener(this);
        }else{
            refreshIV.setVisibility(View.GONE);
            videoLL.setVisibility(View.GONE);
            photoLL.setVisibility(View.VISIBLE);
            sdv=(SimpleDraweeView)findViewById(R.id.edit_show_photo_video_sdv);
            String url=fileModel.getFilePath();
            if(isFinish){
                String path= DataUtil.PHOTO_PATH+File.separator+fileModel.getFileName();
                File file=new File(path);
                if(file.exists()){
                    uri=FileProvider.getUriForFile(this,"com.soft.railway.inspection.fileProvider",file);
                }else{
                    uri=Uri.parse(url);
                }
            }else{
                File file=new File(fileModel.getFilePath());
                if(file.exists()){
                    uri=Uri.fromFile(file);
                }
            }
            sdv.setMaxWidth(width);
            sdv.setMaxHeight(height);
            final GenericDraweeHierarchyBuilder hierarchy= GenericDraweeHierarchyBuilder.newInstance(getResources());
            hierarchy.setPlaceholderImage(R.drawable.ic_fresco_img)
                    .setProgressBarImage(R.mipmap.ic_fresco_progress)
                    .setFailureImage(R.drawable.ic_fresco_faile);
            PipelineDraweeControllerBuilder pipelineDraweeControllerBuilder= Fresco.newDraweeControllerBuilder();
            pipelineDraweeControllerBuilder.setUri(uri)
                    .setOldController(sdv.getController());
            sdv.setHierarchy(hierarchy.build());
            sdv.setController(pipelineDraweeControllerBuilder.build());
            photoLL.setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_photo_video, menu);
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

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_show_photo_video, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public void initList(){
            String path;
            if(isFinish){
                path=DataUtil.SERVER_FILE_PATH+fileModel.getFilePath()+fileModel.getFileName();
            }else{
                path=fileModel.getFilePath();
                if(FileUtil.FILE_TYPE_VIDEO.equals(fileModel.getFileType())){
                    path=path.substring(DataUtil.PHOTO_PATH.length(),path.length()-4);
                    path=DataUtil.VIDEO_PATH+path+".mp4";
                }
            }
            fileModel.setFilePath(path);
    }

    private void setViewShow(){
        String fileType=fileModel.getFileType();
        if(FileUtil.FILE_TYPE_VIDEO.equals(fileType)){
            goBackIV.setVisibility(View.VISIBLE);
            downloadIV.setVisibility(View.GONE);
        }else{
            goBackIV.setVisibility(View.GONE);
            if(isFinish){
                String path=DataUtil.PHOTO_PATH+File.separator+fileModel.getFileName();
                File file=new File(path);
                if(file.exists()){
                    downloadIV.setVisibility(View.GONE);
                }else{
                    downloadIV.setVisibility(View.VISIBLE);
                }
            }else{
                downloadIV.setVisibility(View.GONE);
            }
        }
    }

    private void downLoadImg(Uri uri) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).setProgressiveRenderingEnabled(true).build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                // bitmap即为下载所得图片
                String path=DataUtil.PHOTO_PATH+File.separator+fileModel.getFileName();
                File file=new File(path);
                FileOutputStream out=null;
                try{
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    out=new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                    handler.sendEmptyMessage(HIDE_DOWN);
                }catch (IOException e){
                    MyException myException=new MyException();
                    myException.buildException(e,getBaseContext());
                }finally {
                    try{
                        if(out!=null){
                            out.flush();
                            out.close();
                        }
                    }catch (  IOException e){
                        MyException myException=new MyException();
                        myException.buildException(e,getBaseContext());
                    }
                    handler.sendEmptyMessage(HIDE_DOWN);
                }
                handler.sendEmptyMessage(HIDE_DOWN);
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {

            }
        }, CallerThreadExecutor.getInstance());

        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setFadeDuration(300)
                .setProgressBarImage(new ProgressBarDrawable())
                .build();
        DraweeHolder<GenericDraweeHierarchy> draweeHolder = DraweeHolder.create(hierarchy,this);

        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(draweeHolder.getController())
                .setImageRequest(imageRequest)
                .build();
        controller.onClick();
    }
}
