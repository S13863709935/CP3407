package com.example.service;

import com.example.entity.Comment;
import com.example.mapper.CommentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @AfterEach
    void clearPageHelper() {
        PageHelper.clearPage();
    }

    @Test
    void rootMessageReceivesItsGeneratedIdAsRootId() {
        Comment message = new Comment();
        message.setContent("Is this still available?");
        doAnswer(invocation -> {
            Comment inserted = invocation.getArgument(0);
            inserted.setId(12);
            return 1;
        }).when(commentMapper).insert(message);

        commentService.add(message);

        assertEquals(12, message.getRootId());
        assertNotNull(message.getTime());
        verify(commentMapper).insert(message);
        verify(commentMapper).updateById(message);
    }

    @Test
    void replyInheritsTheParentsRootConversation() {
        Comment parent = new Comment();
        parent.setId(20);
        parent.setRootId(10);
        Comment reply = new Comment();
        reply.setPid(20);
        when(commentMapper.selectById(20)).thenReturn(parent);
        doAnswer(invocation -> {
            Comment inserted = invocation.getArgument(0);
            inserted.setId(21);
            return 1;
        }).when(commentMapper).insert(reply);

        commentService.add(reply);

        assertEquals(10, reply.getRootId());
        assertNotNull(reply.getTime());
        verify(commentMapper).updateById(reply);
    }

    @Test
    void treeQueryAttachesRepliesToEveryRootMessage() {
        Comment firstRoot = comment(1, 1);
        Comment secondRoot = comment(2, 2);
        Comment firstReply = comment(3, 1);
        Comment secondReply = comment(4, 2);
        when(commentMapper.selectRoot(7, "goods"))
                .thenReturn(Arrays.asList(firstRoot, secondRoot));
        when(commentMapper.selectByRootId(1))
                .thenReturn(Collections.singletonList(firstReply));
        when(commentMapper.selectByRootId(2))
                .thenReturn(Collections.singletonList(secondReply));

        PageInfo<Comment> page = commentService.selectTree(7, "goods", 1, 5);

        assertEquals(2, page.getList().size());
        assertSame(firstReply, firstRoot.getChildren().get(0));
        assertSame(secondReply, secondRoot.getChildren().get(0));
    }

    @Test
    void deepDeleteRemovesAllDescendants() {
        Comment child = comment(2, 1);
        Comment grandchild = comment(3, 1);
        when(commentMapper.selectByPid(1)).thenReturn(Collections.singletonList(child));
        when(commentMapper.selectByPid(2)).thenReturn(Collections.singletonList(grandchild));
        when(commentMapper.selectByPid(3)).thenReturn(Collections.emptyList());

        commentService.deleteDeep(1);

        verify(commentMapper).deleteById(1);
        verify(commentMapper).deleteById(2);
        verify(commentMapper).deleteById(3);
    }

    @Test
    void batchDeleteRemovesEverySelectedMessage() {
        commentService.deleteBatch(Arrays.asList(4, 5, 6));

        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(commentMapper, org.mockito.Mockito.times(3)).deleteById(idCaptor.capture());
        assertEquals(Arrays.asList(4, 5, 6), idCaptor.getAllValues());
    }

    @Test
    void countAndSimpleQueriesDelegateToTheMapper() {
        Comment filter = new Comment();
        List<Comment> matches = Collections.singletonList(comment(8, 8));
        when(commentMapper.selectCount(9, "goods")).thenReturn(3);
        when(commentMapper.selectAll(filter)).thenReturn(matches);

        assertEquals(3, commentService.selectCount(9, "goods"));
        assertFalse(commentService.selectPage(filter, 1, 10).getList().isEmpty());
    }

    private Comment comment(int id, int rootId) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setRootId(rootId);
        return comment;
    }
}
