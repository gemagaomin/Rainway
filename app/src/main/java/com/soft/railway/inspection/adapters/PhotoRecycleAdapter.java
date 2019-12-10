package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScaleTypeDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.activities.BaseActivity;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.FileUtil;
import com.soft.railway.inspection.utils.Log;

import java.io.File;
import java.util.List;

public class PhotoRecycleAdapter extends RecyclerView.Adapter<PhotoRecycleAdapter.MyViewHolder>  {
    private List<FileModel> list;
    private OnRecycleViewItemClickListener OnItemClickListener;
    private Context context;
    private int resourceId;
    private final int TYPE_VIDEO=4;
    private final int TYPE_PHOTO=3;
    private boolean isFinish=true;//true 从服务器获取的文件数据；false是本地的文件
    public PhotoRecycleAdapter(List<FileModel> list, Context context, int resourceId) {
        this.list = list;
        this.context = context;
        this.resourceId = resourceId;
    }

    public OnRecycleViewItemClickListener getOnItemClickListener() {
        return OnItemClickListener;
    }

    public void setOnItemClickListener(OnRecycleViewItemClickListener mOnItemClickListener) {
        this.OnItemClickListener = mOnItemClickListener;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(resourceId,null);
        view.setOnClickListener(new BaseActivity() {
            @Override
            public void onNoDoubleClick(View v) {
                if (OnItemClickListener!=null)
                {
                    OnItemClickListener.OnItemClick(v,(int)v.getTag());
                }
            }
        });
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        FileModel fileModel=list.get(position);
        Uri uri=null;
        if(isFinish){
            String url=DataUtil.SERVER_FILE_PATH+fileModel.getFilePath()+fileModel.getFileName();
            holder.deleteIV.setVisibility(View.GONE);
            String path=url.substring(0,url.length()-4)+".jpg";
            uri=Uri.parse(path);
        }else {
            holder.deleteIV.setVisibility(View.VISIBLE);
            holder.deleteIV.setOnClickListener(new BaseActivity() {
                @Override
                public void onNoDoubleClick(View v) {
                    OnItemClickListener.DeleteItemClick(v,(int)v.getTag());
                }
            });
            holder.deleteIV.setTag(position);
            File file=new File(fileModel.getFilePath());
            if(file.exists()){
                uri= FileProvider.getUriForFile(context,"com.soft.railway.inspection.fileProvider",file);
            }
        }
        final GenericDraweeHierarchy hierarchy= GenericDraweeHierarchyBuilder.newInstance(context.getResources())
                .setPlaceholderImage(R.drawable.ic_fresco_img)
                .setProgressBarImage(R.mipmap.ic_fresco_progress)
                .setFailureImage(R.drawable.ic_fresco_faile).build();
        final ControllerListener listener=new BaseControllerListener<ImageInfo>(){
            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
            }

            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo,  Animatable animatable) {
                if(TYPE_VIDEO==getItemViewType(position)){
                    Log.d("FinalImageSet");
                    Drawable drawable = context.getResources().getDrawable(R.drawable.ic_play_video);
                    ScaleTypeDrawable overlayImage = new ScaleTypeDrawable(drawable, ScalingUtils.ScaleType.CENTER);
                    hierarchy.setOverlayImage(overlayImage);
                }
                super.onFinalImageSet(id, imageInfo, animatable);
            }

            @Override
            public void onIntermediateImageSet(String id,  ImageInfo imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
            }
        };
        PipelineDraweeControllerBuilder pipelineDraweeControllerBuilder= Fresco.newDraweeControllerBuilder();
        pipelineDraweeControllerBuilder.setUri(uri)
                .setOldController(holder.sdv.getController())
                .setControllerListener(listener);
        holder.sdv.setHierarchy(hierarchy);
        holder.sdv.setController(pipelineDraweeControllerBuilder.build());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return list!=null?list.size():0;
    }

    @Override
    public int getItemViewType(int position) {
        FileModel fileModel=list.get(position);
        if(FileUtil.FILE_TYPE_VIDEO.equals(fileModel.getFileType())){
            return TYPE_VIDEO;
        }
        return TYPE_PHOTO;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final SimpleDraweeView sdv;
        private final ImageView deleteIV;
        public MyViewHolder(View itemView) {
            super(itemView);
            sdv=(SimpleDraweeView) itemView.findViewById(R.id.my_image_view);
            deleteIV=(ImageView)itemView.findViewById(R.id.my_image_view_delete);
        }
    }

    public interface OnRecycleViewItemClickListener{
        void OnItemClick(View view, int position);
        void DeleteItemClick(View view, int position);
    }
}
