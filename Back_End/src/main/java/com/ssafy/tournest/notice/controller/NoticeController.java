package com.ssafy.tournest.notice.controller;

import com.ssafy.tournest.notice.domain.Notice;
import com.ssafy.tournest.notice.domain.dto.NoticeDto;
import com.ssafy.tournest.notice.service.NoticeService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNoticeList(
            @RequestParam(defaultValue = "1") int pgno,
            @RequestParam(defaultValue = "10") int spp,
            @RequestParam(defaultValue = "") String key,
            @RequestParam(defaultValue = "") String word) {

        PageRequest pageRequest = PageRequest.of(pgno - 1, spp, Sort.by("noticeNo").descending());
        Page<NoticeDto> noticePage = noticeService.getNoticeList(key, word, pageRequest);

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> body = new HashMap<>();

        body.put("notices", noticePage.getContent());
        body.put("currentPage", pgno);
        body.put("totalPageCount", noticePage.getTotalPages());

        response.put("body", body);
        System.out.println("body"+body);

        return ResponseEntity.ok(response);
    }

    // 공지사항 상세 조회
    @GetMapping("/{noticeNo}")
    public ResponseEntity<NoticeDto> getNotice(@PathVariable Long noticeNo) {
        return ResponseEntity.ok(noticeService.getNotice(noticeNo));
    }

    // 공지사항 등록
    @PostMapping
    public ResponseEntity<NoticeDto> createNotice(@RequestBody Notice notice) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeService.createNotice(notice));
    }

    // 공지사항 수정을 위한 조회
    @GetMapping("/modify/{noticeNo}")
    public ResponseEntity<NoticeDto> getModifyNotice(@PathVariable Long noticeNo) {
        return ResponseEntity.ok(noticeService.getNotice(noticeNo));
    }

    // 공지사항 수정
    @PutMapping
    public ResponseEntity<NoticeDto> updateNotice(@RequestBody Notice notice) {
        return ResponseEntity.ok(noticeService.updateNotice(notice.getNoticeNo(), notice));
    }

    // 공지사항 삭제
    @DeleteMapping("/{noticeNo}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeNo) {
        noticeService.deleteNotice(noticeNo);
        return ResponseEntity.ok().build();
    }
}