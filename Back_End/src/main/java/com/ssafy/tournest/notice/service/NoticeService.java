package com.ssafy.tournest.notice.service;

import com.ssafy.tournest.api.service.UserService;
import com.ssafy.tournest.notice.domain.Notice;
import com.ssafy.tournest.notice.domain.dto.NoticeDto;
import com.ssafy.tournest.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserService userService; // 사용자 정보를 가져오기 위한 서비스

    // 공지사항 목록 조회
    @Transactional(readOnly = true)
    public Page<NoticeDto> getNoticeList(String key, String word, Pageable pageable) {
        Page<Notice> noticePage = noticeRepository.findBySearchCondition(key, word, pageable);
        return noticePage.map(notice -> {
            String username = userService.getUsernameById(notice.getUserId());
            return NoticeDto.from(notice, username);
        });
    }

    // 공지사항 상세 조회
    public NoticeDto getNotice(Long noticeNo) {
        Notice notice = noticeRepository.findById(noticeNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));
        notice.increaseHit();
        String username = userService.getUsernameById(notice.getUserId());
        return NoticeDto.from(notice, username);
    }

    // 공지사항 등록
    public NoticeDto createNotice(Notice notice) {
        Notice savedNotice = noticeRepository.save(notice);
        String username = userService.getUsernameById(savedNotice.getUserId());
        System.out.println("username: "+username);
        return NoticeDto.from(savedNotice, username);
    }

    // 공지사항 수정
    public NoticeDto updateNotice(Long noticeNo, Notice noticeRequest) {
        Notice notice = noticeRepository.findById(noticeNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));

        notice.update(noticeRequest.getSubject(), noticeRequest.getContent());
        String username = userService.getUsernameById(notice.getUserId());
        return NoticeDto.from(notice, username);
    }

    // 공지사항 삭제
    public void deleteNotice(Long noticeNo) {
        Notice notice = noticeRepository.findById(noticeNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));
        noticeRepository.delete(notice);
    }
}