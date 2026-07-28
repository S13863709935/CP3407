package com.example.service;

import com.example.entity.Collect;
import com.example.entity.Feedback;
import com.example.mapper.CollectMapper;
import com.example.mapper.FeedbackMapper;
import com.example.support.AuthenticatedTestSupport;
import com.github.pagehelper.PageHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResidentScopedServiceTest {

    @Mock
    private CollectMapper collectMapper;
    @Mock
    private FeedbackMapper feedbackMapper;
    @Mock
    private UserService userService;
    @Mock
    private AdminService adminService;

    @InjectMocks
    private CollectService collectService;
    @InjectMocks
    private FeedbackService feedbackService;

    @BeforeEach
    void authenticate() {
        AuthenticatedTestSupport.authenticateResident(userService, adminService, 41);
    }

    @AfterEach
    void cleanThreadState() {
        PageHelper.clearPage();
        AuthenticatedTestSupport.clearAuthentication();
    }

    @Test
    void favouriteToggleAddsANewResidentOwnedRecord() {
        Collect collect = new Collect();
        collect.setFid(3);
        when(collectMapper.selectByUserIdAndFid(41, 3)).thenReturn(null);

        collectService.add(collect);

        assertEquals(41, collect.getUserId());
        verify(collectMapper).insert(collect);
    }

    @Test
    void favouriteToggleDeletesAnExistingRecord() {
        Collect request = new Collect();
        request.setFid(3);
        Collect existing = new Collect();
        existing.setId(77);
        when(collectMapper.selectByUserIdAndFid(41, 3)).thenReturn(existing);

        collectService.add(request);

        verify(collectMapper).deleteById(77);
        verify(collectMapper, never()).insert(request);
    }

    @Test
    void favouritePageUsesAuthenticatedResidentId() {
        when(collectMapper.selectAll(41)).thenReturn(Collections.emptyList());

        assertEquals(0, collectService.selectPage(1, 10).getList().size());

        verify(collectMapper).selectAll(41);
    }

    @Test
    void feedbackSubmissionAndHistoryAreResidentScoped() {
        Feedback feedback = new Feedback();
        feedback.setTitle("Accessibility");
        when(feedbackMapper.selectAll(feedback)).thenReturn(Collections.singletonList(feedback));

        feedbackService.add(feedback);
        assertEquals(41, feedback.getUserId());
        assertNotNull(feedback.getCreatetime());
        assertEquals(1, feedbackService.selectPage(feedback, 1, 10).getList().size());

        verify(feedbackMapper).insert(feedback);
        assertEquals(41, feedback.getUserId());
    }
}
