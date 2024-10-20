package com.example.iot_chatbot_practice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 聊天消息的列表
    private List<ChatMessage> chatMessages;

    // 建構函式，用於初始化ChatAdapter的屬性
    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    // 設定每個項目的類型，0表示用戶消息，1表示AI助手消息
    @Override
    public int getItemViewType(int position) {
        return chatMessages.get(position).isUser() ? 0 : 1;
    }

    // 創建ViewHolder並設置佈局
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            // 創建用戶消息的ViewHolder並設置佈局
            View userMessageView = inflater.inflate(R.layout.item_chat_bubble_user, parent, false);
            return new UserMessageViewHolder(userMessageView);
        } else {
            // 創建AI助手消息的ViewHolder並設置佈局
            View aiMessageView = inflater.inflate(R.layout.item_chat_bubble_ai, parent, false);
            return new AiMessageViewHolder(aiMessageView);
        }
    }

    // 定義用戶消息的ViewHolder
    private static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTextView;

        // 建構函式，初始化用戶消息的ViewHolder
        UserMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.text_view_message);
        }

        // 綁定用戶消息到ViewHolder
        void bind(ChatMessage message) {
            messageTextView.setText(message.getMessage());
        }
    }

    // 定義AI助手消息的ViewHolder
    private static class AiMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTextView;

        // 建構函式，初始化AI助手消息的ViewHolder
        AiMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.text_view_message);
        }

        // 綁定AI助手消息到ViewHolder
        void bind(ChatMessage message) {
            messageTextView.setText(message.getMessage());
        }
    }

    // 綁定數據到ViewHolder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (holder.getItemViewType() == 0) {
            ((UserMessageViewHolder) holder).bind(chatMessage);
        } else {
            ((AiMessageViewHolder) holder).bind(chatMessage);
        }
    }

    // 返回聊天消息的數量
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    // 添加新的聊天消息到列表並通知Adapter更新
    public void addMessage(ChatMessage message) {
        chatMessages.add(message);
        notifyItemInserted(chatMessages.size() - 1);
    }
}
