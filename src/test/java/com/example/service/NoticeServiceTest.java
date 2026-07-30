package com.example.service;

import com.example.entity.Notice;
import com.example.mapper.NoticeMapper;
import com.example.support.AuthenticatedTestSupport;
import com.github.pagehelper.PageHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeMapper noticeMapper;
    @Mock
    private UserService userService;
    @Mock
    private AdminService adminService;

    @InjectMocks
    private NoticeService noticeService;

    @BeforeEach
    void authenticate() {
        AuthenticatedTestSupport.authenticateResident(userService, adminService, 63);
    }

    @AfterEach
    void cleanThreadState() {
        PageHelper.clearPage();
        AuthenticatedTestSupport.clearAuthentication();
    }

    @Test
    void newAnnouncementRecordsItsAuthorAndDate() {
        Notice notice = new Notice();
        notice.setTitle("Community collection day");

        noticeService.add(notice);

        assertEquals("resident-63", notice.getUser());
        assertNotNull(notice.getTime());
        verify(noticeMapper).insert(notice);
    }

    @Test
    void announcementListSupportsAnEmptyResult() {
        Notice filter = new Notice();
        when(noticeMapper.selectAll(filter)).thenReturn(Collections.emptyList());

        assertEquals(0, noticeService.selectPage(filter, 1, 10).getList().size());
    }

    @Test
    void announcementQueriesReturnMapperResults() {
        Notice notice = new Notice();
        notice.setId(4);
        when(noticeMapper.selectById(4)).thenReturn(notice);
        when(noticeMapper.selectAll(notice)).thenReturn(Collections.singletonList(notice));

        assertSame(notice, noticeService.selectById(4));
        assertSame(notice, noticeService.selectAll(notice).get(0));
    }

    @Test
    void announcementUpdateDelegatesToTheMapper() {
        Notice notice = new Notice();
        notice.setId(4);
        notice.setContent("Updated guidance");

        noticeService.updateById(notice);

        verify(noticeMapper).updateById(notice);
    }

    @Test
    void announcementBatchDeleteRemovesEverySelectedRecord() {
        noticeService.deleteBatch(Arrays.asList(4, 5));

        verify(noticeMapper).deleteById(4);
        verify(noticeMapper).deleteById(5);
    }
}
