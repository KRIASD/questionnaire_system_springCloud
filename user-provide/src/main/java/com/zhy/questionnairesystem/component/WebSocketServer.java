package com.zhy.questionnairesystem.component;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhy.domain.*;
import com.zhy.questionnairesystem.mapper.*;
import com.zhy.vo.ChatHistoryWithUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/websocket/{username}")
@Component
@Slf4j
public class WebSocketServer {

    private static final CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();
    @Autowired
    private FriendMapper friendMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GroupMemberMapper groupMemberMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private Session session;
    private String username;
    private User user;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        this.session = session;
        this.username = username;
        this.user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        webSocketSet.add(this);
        log.info("有新连接加入！当前在线人数为" + webSocketSet.size());
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        log.info("有一连接关闭！当前在线人数为" + webSocketSet.size());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息:" + message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        String type = jsonObject.getString("type");
        JSONObject data = jsonObject.getJSONObject("data");

        // 根据消息类型调用相应的方法
        switch (type) {
            case "addFriend":
                addFriend(data.getInteger("friendId"));
                break;
            case "removeFriend":
                removeFriend(data.getInteger("friendId"));
                break;
            case "createGroup":
                createGroup(data.getString("groupName"));
                break;
            case "joinGroup":
                joinGroup(data.getInteger("groupId"));
                break;
            case "leaveGroup":
                leaveGroup(data.getInteger("groupId"));
                break;
            case "sendPrivateMessage":
                sendPrivateMessage(data.getInteger("receiverId"), data.getString("content"));
                break;
            case "sendGroupMessage":
                sendGroupMessage(data.getInteger("groupId"), data.getString("content"));
                break;
            case "getHistoryMessage":
                getChatHistory(data.getInteger("receiverId"), data.getInteger("groupId"));
                break;
            case "getFriends":
                getFriends();
                break;
            case "getGroups":
                getGroups();
                break;
            case "getGroupMembers":
                getGroupMembers(data.getInteger("groupId"));
                break;
            default:
                log.error("未知的消息类型: " + type);
                break;
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");

    }

    public void addFriend(Integer friendId) {
        Friend friend = new Friend();
        friend.setUserId(this.user.getId());
        friend.setFriendId(friendId);
        friendMapper.insert(friend);
    }

    public void removeFriend(Integer friendId) {
        friendMapper.delete(new LambdaQueryWrapper<>(new Friend()).eq(Friend::getUserId, this.user.getId()).eq(Friend::getFriendId, friendId));
    }

    public void createGroup(String groupName) {
        Group group = new Group();
        group.setGroupName(groupName);
        groupMapper.insert(group);
    }

    public void joinGroup(Integer groupId) {
        GroupMember groupMember = new GroupMember();
        groupMember.setGroupId(groupId);
        groupMember.setUserId(this.user.getId());
        groupMemberMapper.insert(groupMember);
    }

    public void leaveGroup(Integer groupId) {
        groupMemberMapper.delete(new LambdaQueryWrapper<>(new GroupMember()).eq(GroupMember::getGroupId, groupId).eq(GroupMember::getUserId, this.user.getId()));
    }

    public void sendPrivateMessage(Integer receiverId, String content) {
        Message message = new Message();
        message.setSenderId(this.user.getId());
        message.setReceiverId(receiverId);
        message.setContent(content);
        messageMapper.insert(message);
        //存入redis
        redisTemplate.opsForList().leftPush("messagePrivate:" + this.user.getId() + "to" + receiverId, String.valueOf(message));
        //找到接受者的WebSocketServer会话发送消息
        for (WebSocketServer item : webSocketSet) {
            if (item.user.getId().equals(receiverId)) {
                try {
                    // 异步发送消息
                    item.session.getAsyncRemote().sendText(JSONObject.toJSONString(message));
                } catch (Exception e) {
                    log.error("发送消息失败");
                }
            }
        }
    }

    public void sendGroupMessage(Integer groupId, String content) {
        Message message = new Message();
        message.setSenderId(this.user.getId());
        message.setGroupId(groupId);
        message.setContent(content);
        messageMapper.insert(message);
        //找到群组中的所有用户的WebSocketServer会话发送消息
        List<GroupMember> groupMembers = groupMemberMapper.selectList(new LambdaQueryWrapper<>(new GroupMember()).eq(GroupMember::getGroupId, groupId));
        redisTemplate.opsForList().leftPush("messageGroup:" + this.user.getId() + "to" + groupId, String.valueOf(message));
        for (GroupMember groupMember : groupMembers) {
            for (WebSocketServer item : webSocketSet) {
                if (item.user.getId().equals(groupMember.getUserId())) {
                    try {
                        // 异步发送消息
                        item.session.getAsyncRemote().sendText(JSONObject.toJSONString(message));
                    } catch (Exception e) {
                        log.error("发送消息失败");
                    }
                    break;
                }
            }
        }
    }

    public void getChatHistory(Integer receiverId, Integer groupId) {
        if (receiverId != null) {
            String key = "messagePrivate:" + this.user.getId() + "to" + receiverId;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                List<String> messages = redisTemplate.opsForList().range(key, 0, -1);
                try {
                    // 异步发送消息
                    this.session.getAsyncRemote().sendText(JSONObject.toJSONString(messages));
                } catch (Exception e) {
                    log.error("发送消息失败");
                }
            } else {
                List<Message> messages = messageMapper.selectList(new LambdaQueryWrapper<>(new Message()).eq(Message::getSenderId, this.user.getId()).eq(Message::getReceiverId, receiverId));
                User user = userMapper.selectOne(new LambdaQueryWrapper<>(new User()).eq(User::getId, receiverId));
                ChatHistoryWithUser chatHistoryWithUser = new ChatHistoryWithUser(user, messages, null);
                try {
                    // 异步发送消息
                    this.session.getAsyncRemote().sendText(JSONObject.toJSONString(chatHistoryWithUser));
                } catch (Exception e) {
                    log.error("发送消息失败");
                }
            }
        } else if (groupId != null) {
            String key = "messageGroup:" + this.user.getId() + "to" + groupId;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                List<String> messages = redisTemplate.opsForList().range(key, 0, -1);
                try {
                    // 异步发送消息
                    this.session.getAsyncRemote().sendText(JSONObject.toJSONString(messages));
                } catch (Exception e) {
                    log.error("发送消息失败");
                }
            } else {
                List<Message> messages = messageMapper.selectList(new LambdaQueryWrapper<>(new Message()).eq(Message::getGroupId, groupId));
                Group group = groupMapper.selectOne(new LambdaQueryWrapper<>(new Group()).eq(Group::getId, groupId));
                ChatHistoryWithUser chatHistoryWithUser = new ChatHistoryWithUser(null, messages, group);
                try {
                    // 异步发送消息
                    this.session.getAsyncRemote().sendText(JSONObject.toJSONString(chatHistoryWithUser));
                } catch (Exception e) {
                    log.error("发送消息失败");
                }
            }
        } else {
            log.error("receiverId和groupId不能同时为空");
        }

    }

    //查看好友列表
    public void getFriends() {
        List<Friend> friends = friendMapper.selectList(new LambdaQueryWrapper<>(new Friend()).eq(Friend::getUserId, this.user.getId()));
        for (Friend friend : friends) {
            for (WebSocketServer item : webSocketSet) {
                if (item.user.getId().equals(friend.getFriendId())) {
                    try {
                        // 异步发送消息
                        item.session.getAsyncRemote().sendText(JSONObject.toJSONString(this.user));
                    } catch (Exception e) {
                        log.error("发送消息失败");
                    }
                    break;
                }
            }
        }
    }

    //查看群组列表
    public void getGroups() {
        List<GroupMember> groupMembers = groupMemberMapper.selectList(new LambdaQueryWrapper<>(new GroupMember()).eq(GroupMember::getUserId, this.user.getId()));
        for (GroupMember groupMember : groupMembers) {
            for (WebSocketServer item : webSocketSet) {
                if (item.user.getId().equals(groupMember.getGroupId())) {
                    try {
                        // 异步发送消息
                        item.session.getAsyncRemote().sendText(JSONObject.toJSONString(this.user));
                    } catch (Exception e) {
                        log.error("发送消息失败");
                    }
                    break;
                }
            }
        }
    }

    //查看群组中的成员列表
    public void getGroupMembers(Integer groupId) {
        List<GroupMember> groupMembers = groupMemberMapper.selectList(new LambdaQueryWrapper<>(new GroupMember()).eq(GroupMember::getGroupId, groupId));
        List<User> users = new ArrayList<>();
        if (groupMembers != null) {
            for (GroupMember groupMember : groupMembers) {
                User user = userMapper.selectOne(new LambdaQueryWrapper<>(new User()).eq(User::getId, groupMember.getUserId()));
                users.add(user);
            }
            String userJon = JSONObject.toJSONString(users);
            for (WebSocketServer item : webSocketSet) {
                if (item.user.getId().equals(this.user.getId())) {
                    try {
                        item.session.getAsyncRemote().sendText(userJon);
                    } catch (Exception e) {
                        log.error("发送消息失败");
                    }
                    break;
                }
            }
        } else {
            log.error("群组中没有成员");
        }
    }
//   //  创建一个WebSocket连接
//    var socket = new WebSocket('ws://localhost:8080/websocket/username');
//
//// 当WebSocket连接打开时，发送一条消息
//    socket.onopen = function(event) {
//        socket.send('Hello, Server!');
//    };
//
//// 当从服务器接收到一条消息时，打印出来
//    socket.onmessage = function(event) {
//        console.log('Received: ' + event.data);
//    };
//
//// 当WebSocket连接关闭或发生错误时，打印一条消息
//    socket.onclose = function(event) {
//        console.log('WebSocket closed');
//    };
//    socket.onerror = function(event) {
//        console.log('WebSocket error: ' + event);
//    };
}
