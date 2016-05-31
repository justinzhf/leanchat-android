package com.avoscloud.leanchatlib.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.easemob.redpacketsdk.constant.RPConstant;

import java.util.Map;

import utils.RedPacketUtils;

/**
 * Created by ustc on 2016/5/30.
 */
public class ChatItemRPNotifyHolder extends ChatItemHolder {

    protected TextView contentView;

    public ChatItemRPNotifyHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft);

    }

    @Override
    public void initView() {
        super.initView();
        conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_money_message, null));
        avatarView.setVisibility(View.GONE);

        contentView = (TextView) itemView.findViewById(R.id.tv_money_msg);


    }

    @Override
    public void bindData(Object o) {
        super.bindData(o);
        nameView.setText("");
        AVIMMessage message = (AVIMMessage) o;
        if (message instanceof AVIMTextMessage) {
            final AVIMTextMessage textMessage = (AVIMTextMessage) message;
            //获取附加字段
            final Map<String, Object> attrs = textMessage.getAttrs();
            //防止崩潰，先檢查數據
            if (!RedPacketUtils.checkReceivedRPData(attrs)) return;
            String fromUser = (String) attrs.get(RedPacketUtils.EXTRA_RED_PACKET_SENDER_NAME);//红包发送者
            String toUser = (String) attrs.get(RedPacketUtils.EXTRA_RED_PACKET_RECEIVER_NAME);//红包接收者
            String senderId = (String) attrs.get(RedPacketUtils.EXTRA_RED_PACKET_SENDER_ID);
            //获取聊天类型-----1或者RPConstant.CHATTYPE_SINGLE单聊，2或者RPConstant.CHATTYPE_GROUP群聊--从附加字段里获取
            int chatType;
            try {
                chatType = (int) attrs.get(RedPacketUtils.CHAT_TYPE);
            } catch (Exception e) {
                chatType = RPConstant.CHATTYPE_SINGLE;
            }
            ChatManager chatManager = ChatManager.getInstance();
            String selfId = chatManager.getSelfId();
            if (fromMe(textMessage)) {
                if (chatType == RPConstant.CHATTYPE_GROUP) {

                    if (senderId.equals(selfId)) {
                        contentView.setText(R.string.money_msg_take_money);
                    } else {
                        contentView.setText(String.format(getContext().getResources().getString(R.string.money_msg_take_someone_money), fromUser));
                    }
                } else {
                    contentView.setText(String.format(getContext().getResources().getString(R.string.money_msg_take_someone_money), fromUser));
                }
            } else {
                if (senderId.equals(selfId)) {
                    contentView.setText(String.format(getContext().getResources().getString(R.string.money_msg_someone_take_money), toUser));

                } else {
                    contentView.setText(String.format(getContext().getResources().getString(R.string.money_msg_someone_take_money_same), toUser, fromUser));
                }


            }

        }


    }


    private boolean fromMe(AVIMTypedMessage msg) {
        ChatManager chatManager = ChatManager.getInstance();
        String selfId = chatManager.getSelfId();
        return msg.getFrom() != null && msg.getFrom().equals(selfId);
    }
}
