package com.example.service;

import com.example.entity.ChatGroup;
import com.example.entity.ChatInfo;
import com.example.entity.User;
import com.example.mapper.ChatGroupMapper;
import com.example.mapper.ChatInfoMapper;
import com.example.support.AuthenticatedTestSupport;
import com.github.pagehelper.PageHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ChatGroupMapper chatGroupMapper;
    @Mock
    private ChatInfoMapper chatInfoMapper;
    @Mock
    private UserService userService;
    @Mock
    private AdminService adminService;

    private ChatInfoService chatInfoService;
    private ChatGroupService chatGroupService;

    @BeforeEach
    void setUp() {
        chatInfoService = new ChatInfoService();
        ReflectionTestUtils.setField(chatInfoService, "chatInfoMapper", chatInfoMapper);

        chatGroupService = new ChatGroupService();
        ReflectionTestUtils.setField(chatGroupService, "chatGroupMapper", chatGroupMapper);
        ReflectionTestUtils.setField(chatGroupService, "chatInfoService", chatInfoService);
        ReflectionTestUtils.setField(chatGroupService, "userService", userService);

        AuthenticatedTestSupport.authenticateResident(userService, adminService, 52);
    }

    @AfterEach
    void cleanThreadState() {
        PageHelper.clearPage();
        AuthenticatedTestSupport.clearAuthentication();
    }

    @Test
    void startingAConversationCreatesBothParticipantsViews() {
        ChatGroup request = group(52, 77);
        when(chatGroupMapper.selectByChatUserIdAndUserId(77, 52)).thenReturn(null);
        when(chatGroupMapper.selectByChatUserIdAndUserId(52, 77)).thenReturn(null);

        chatGroupService.add(request);

        ArgumentCaptor<ChatGroup> captor = ArgumentCaptor.forClass(ChatGroup.class);
        verify(chatGroupMapper, org.mockito.Mockito.times(2)).insert(captor.capture());
        List<ChatGroup> inserted = captor.getAllValues();
        assertSame(request, inserted.get(0));
        assertEquals(77, inserted.get(1).getUserId());
        assertEquals(52, inserted.get(1).getChatUserId());
    }

    @Test
    void startingAnExistingConversationDoesNotCreateDuplicates() {
        ChatGroup request = group(52, 77);
        when(chatGroupMapper.selectByChatUserIdAndUserId(77, 52))
                .thenReturn(group(52, 77));
        when(chatGroupMapper.selectByChatUserIdAndUserId(52, 77))
                .thenReturn(group(77, 52));

        chatGroupService.add(request);

        verify(chatGroupMapper, never()).insert(org.mockito.ArgumentMatchers.any(ChatGroup.class));
    }

    @Test
    void conversationListIncludesNeighbourIdentityAndUnreadCount() {
        ChatGroup neighbourGroup = group(52, 77);
        User neighbour = new User();
        neighbour.setId(77);
        neighbour.setName("Alex");
        neighbour.setAvatar("/files/alex.png");
        when(chatGroupMapper.selectByUserId(52))
                .thenReturn(Collections.singletonList(neighbourGroup));
        when(chatInfoMapper.selectUnReadChatNum(52, 77)).thenReturn(4);
        when(userService.selectById(77)).thenReturn(neighbour);

        List<ChatGroup> result = chatGroupService.selectUserGroup();

        assertEquals(1, result.size());
        assertEquals("Alex", result.get(0).getChatUserName());
        assertEquals("/files/alex.png", result.get(0).getChatUserAvatar());
        assertEquals(4, result.get(0).getChatNum());
    }

    @Test
    void newChatMessageReceivesATimestamp() {
        ChatInfo message = new ChatInfo();
        message.setText("Can I collect tomorrow?");

        chatInfoService.add(message);

        assertNotNull(message.getTime());
        verify(chatInfoMapper).insert(message);
    }

    @Test
    void chatHistoryIsAlwaysScopedToTheAuthenticatedResident() {
        ChatInfo message = new ChatInfo();
        when(chatInfoMapper.selectUserChat(52, 77))
                .thenReturn(Collections.singletonList(message));

        List<ChatInfo> result = chatInfoService.selectUserChat(77);

        assertEquals(1, result.size());
        verify(chatInfoMapper).selectUserChat(52, 77);
    }

    @Test
    void markingConversationReadUsesAuthenticatedResident() {
        chatInfoService.updateRead(77);

        verify(chatInfoMapper).updateRead(52, 77);
    }

    @Test
    void batchChatDeletionRemovesEverySelectedMessage() {
        chatInfoService.deleteBatch(Arrays.asList(11, 12));

        verify(chatInfoMapper).deleteById(11);
        verify(chatInfoMapper).deleteById(12);
    }

    @Test
    void conversationGroupManagementDelegatesToTheMapper() {
        ChatGroup group = group(52, 77);
        group.setId(6);
        when(chatGroupMapper.selectById(6)).thenReturn(group);
        when(chatGroupMapper.selectAll(group)).thenReturn(Collections.singletonList(group));

        assertSame(group, chatGroupService.selectById(6));
        assertSame(group, chatGroupService.selectAll(group).get(0));
        assertEquals(1, chatGroupService.selectPage(group, 1, 10).getList().size());

        chatGroupService.updateById(group);
        chatGroupService.deleteBatch(Arrays.asList(6, 7));

        verify(chatGroupMapper).updateById(group);
        verify(chatGroupMapper).deleteById(6);
        verify(chatGroupMapper).deleteById(7);
    }

    @Test
    void chatMessageQueriesAndUpdateDelegateToTheMapper() {
        ChatInfo message = new ChatInfo();
        message.setId(11);
        when(chatInfoMapper.selectById(11)).thenReturn(message);
        when(chatInfoMapper.selectAll(message)).thenReturn(Collections.singletonList(message));
        when(chatInfoMapper.selectUnReadChatNum(52, 77)).thenReturn(2);

        assertSame(message, chatInfoService.selectById(11));
        assertSame(message, chatInfoService.selectAll(message).get(0));
        assertEquals(1, chatInfoService.selectPage(message, 1, 10).getList().size());
        assertEquals(2, chatInfoService.selectUnReadChatNum(52, 77));

        chatInfoService.updateById(message);

        verify(chatInfoMapper).updateById(message);
    }

    private ChatGroup group(int userId, int chatUserId) {
        ChatGroup group = new ChatGroup();
        group.setUserId(userId);
        group.setChatUserId(chatUserId);
        return group;
    }
}
