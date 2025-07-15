package com.example.myapplication.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.entity.CommentInfo;
import com.example.myapplication.entity.UserInfo;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    
    private List<CommentInfo> commentList;
    private OnCommentActionListener listener;
    
    // 定义评论操作接口
    public interface OnCommentActionListener {
        void onDeleteComment(int position, CommentInfo comment);
        void onCommentClick(CommentInfo comment);
    }
    
    public CommentAdapter(List<CommentInfo> commentList) {
        this.commentList = commentList;
    }
    
    public void setOnCommentActionListener(OnCommentActionListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentInfo comment = commentList.get(position);
        
        holder.tvNickname.setText(comment.getNickname());
        holder.tvTime.setText(comment.getFormattedTime());
        holder.tvContent.setText(comment.getContent());
        
        // 判断是否是当前登录用户的评论，只有自己的评论才能删除
        String currentUsername = "";
        UserInfo currentUser = UserInfo.getUserInfo();
        if (currentUser != null) {
            currentUsername = currentUser.getUsername();
        }
        
        if (currentUsername.equals(comment.getUsername())) {
            holder.tvDelete.setVisibility(View.VISIBLE);
            holder.tvDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteComment(position, comment);
                }
            });
        } else {
            holder.tvDelete.setVisibility(View.GONE);
        }

        // 添加整个评论项的点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCommentClick(comment);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return commentList == null ? 0 : commentList.size();
    }
    
    // 更新评论列表
    public void updateComments(List<CommentInfo> commentList) {
        this.commentList = commentList;
        notifyDataSetChanged();
    }
    
    // 添加一条评论
    public void addComment(CommentInfo comment) {
        commentList.add(0, comment); // 添加到列表开头
        notifyItemInserted(0);
    }
    
    // 删除一条评论
    public void removeComment(int position) {
        commentList.remove(position);
        notifyItemRemoved(position);
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNickname, tvTime, tvContent, tvDelete;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvDelete = itemView.findViewById(R.id.tv_delete);
        }
    }
} 