package com.atguigu.myapplicationtest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atguigu.myapplicationtest.R;
import com.atguigu.myapplicationtest.domain.NetAudioBean;

import org.xutils.x;

import java.util.List;

/**
 * Created by My on 2017/5/31.
 */

public class RecyclerFragmentAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<NetAudioBean.ListBean> datas;
    private static final int TYPE_VIDEO = 0;

    /**
     * 图片
     */
    private static final int TYPE_IMAGE = 1;

    /**
     * 文字
     */
    private static final int TYPE_TEXT = 2;

    /**
     * GIF图片
     */
    private static final int TYPE_GIF = 3;


    /**
     * 软件推广
     */
    private static final int TYPE_AD = 4;

    public RecyclerFragmentAdapter(Context context, List<NetAudioBean.ListBean> datas) {

        this.mContext = context;
        this.datas = datas;
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        int itemViewType = -1;
        NetAudioBean.ListBean listBean = datas.get(position);
        String type = listBean.getType();
        if ("video".equals(type)) {
            itemViewType = TYPE_VIDEO;
        } else if ("image".equals(type)) {
            itemViewType = TYPE_IMAGE;
        } else if ("text".equals(type)) {
            itemViewType = TYPE_TEXT;
        } else if ("gif".equals(type)) {
            itemViewType = TYPE_GIF;
        } else {
            itemViewType = TYPE_AD;
        }
        return itemViewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {

            case TYPE_VIDEO:

                viewHolder = new VideoHoder(View.inflate(mContext, R.layout.all_video_item, null));

                break;
            case TYPE_IMAGE:

                viewHolder = new ImageHolder(View.inflate(mContext, R.layout.all_image_item, null));

                break;
            case TYPE_TEXT:

                viewHolder = new TextHolder(View.inflate(mContext, R.layout.all_text_item, null));

                break;
            case TYPE_GIF:

                viewHolder = new GifHolder(View.inflate(mContext, R.layout.all_gif_item, null));

                break;
            case TYPE_AD:

                viewHolder = new ADHolder(View.inflate(mContext, R.layout.all_ad_item, null));

                break;
            default:

                viewHolder = new ADHolder(View.inflate(mContext, R.layout.all_ad_item, null));

                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    class BaseViewHolder extends RecyclerView.ViewHolder{
        ImageView ivHeadpic;
        TextView tvName;
        TextView tvTimeRefresh;
        ImageView ivRightMore;
        ImageView ivVideoKind;
        TextView tvVideoKindText;
        TextView tvShenheDingNumber;
        TextView tvShenheCaiNumber;
        TextView tvPostsNumber;
        LinearLayout llDownload;

        public BaseViewHolder(View convertView) {
            super(convertView);
            ivHeadpic = (ImageView) convertView.findViewById(R.id.iv_headpic);
            tvName = (TextView) convertView.findViewById(R.id.tv_name);
            tvTimeRefresh = (TextView) convertView.findViewById(R.id.tv_time_refresh);
            ivRightMore = (ImageView) convertView.findViewById(R.id.iv_right_more);
            //bottom
            ivVideoKind = (ImageView) convertView.findViewById(R.id.iv_video_kind);
            tvVideoKindText = (TextView) convertView.findViewById(R.id.tv_video_kind_text);
            tvShenheDingNumber = (TextView) convertView.findViewById(R.id.tv_shenhe_ding_number);
            tvShenheCaiNumber = (TextView) convertView.findViewById(R.id.tv_shenhe_cai_number);
            tvPostsNumber = (TextView) convertView.findViewById(R.id.tv_posts_number);
            llDownload = (LinearLayout) convertView.findViewById(R.id.ll_download);
        }

        public void setData(NetAudioBean.ListBean mediaItem) {
            if (mediaItem.getU() != null && mediaItem.getU().getHeader() != null && mediaItem.getU().getHeader().get(0) != null) {
                x.image().bind(ivHeadpic, mediaItem.getU().getHeader().get(0));
            }
            if (mediaItem.getU() != null && mediaItem.getU().getName() != null) {
                tvName.setText(mediaItem.getU().getName() + "");
            }

            tvTimeRefresh.setText(mediaItem.getPasstime());

            List<NetAudioBean.ListBean.TagsBean> tagsEntities = mediaItem.getTags();
            if (tagsEntities != null && tagsEntities.size() > 0) {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < tagsEntities.size(); i++) {
                    buffer.append(tagsEntities.get(i).getName() + " ");
                }
                tvVideoKindText.setText(buffer.toString());
            }

            tvShenheDingNumber.setText(mediaItem.getUp());
            tvShenheCaiNumber.setText(mediaItem.getDown() + "");
            tvPostsNumber.setText(mediaItem.getForward() + "");

        }
    }

    class VideoHoder extends BaseViewHolder {

        public VideoHoder(View itemView) {
            super(itemView);
        }
    }

    class ImageHolder extends BaseViewHolder {

        public ImageHolder(View itemView) {
            super(itemView);
        }
    }

    class TextHolder extends BaseViewHolder {

        public TextHolder(View itemView) {
            super(itemView);
        }
    }

    class GifHolder extends BaseViewHolder {

        public GifHolder(View itemView) {
            super(itemView);
        }
    }

    class ADHolder extends RecyclerView.ViewHolder {

        public ADHolder(View itemView) {
            super(itemView);
        }
    }

}
