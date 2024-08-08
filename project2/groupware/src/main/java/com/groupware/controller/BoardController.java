package com.groupware.controller;

import com.groupware.common.Util;
import com.groupware.dto.*;
import com.groupware.service.BoardService;
import com.groupware.service.NotificationService;
import com.groupware.ui.ThePager;
import com.groupware.view.BoardDownloadView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = {"/board"})
public class BoardController {

    @Setter(onMethod_ = {@Autowired})
    private BoardService boardService;

    @Setter(onMethod_ = {@Autowired})
    private NotificationService notificationService;

    // 모든 /mailbox URL에 대해 총 메일 수를 모델에 추가
    @ModelAttribute
    public void getEmployeePosition(Model model, HttpSession session) {
        EmployeeDto employee = (EmployeeDto)session.getAttribute("loginUser");
        // 로그인 유저 직급 불러오기
        PositionDto employeePosition = boardService.getPositionNameByPositionNo(employee.getPositionNo());
        // 모델에 추가
        model.addAttribute("employeePosition", employeePosition);
    }
    @GetMapping(path = {"/list"})
    public  String all (){
        return "board/list";
    }

    @GetMapping(path = { "/board-list" })
    public String listRange(@RequestParam(name="pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(name="searchText", required = false) String searchText,
                            HttpServletRequest req, Model model) {
        int pageSize = 10;
        int pagerSize = 3;
        if (searchText != null && searchText.isEmpty()) {
            searchText = null;
        }
        int dataCount = boardService.getBoardCount(searchText);

        String uri = req.getRequestURI();
        String linkUrl = uri.substring(uri.lastIndexOf("/") + 1);
        String queryString = req.getQueryString();

        int start = pageSize * (pageNo - 1) ;
        ThePager pager = new ThePager(dataCount, pageNo, pageSize, pagerSize, linkUrl, queryString);

        List<BoardDto> boards =  boardService.findBoardByRange(start, pageSize, searchText);

        model.addAttribute("boardList", boards);
        model.addAttribute("pager", pager);
        model.addAttribute("pageNo", pageNo);
        // HTML 템플릿 반환
        return "/board/board-list";

    }

    @GetMapping(path = { "/detail" })
    public String detail(@RequestParam(value="boardNo", required = false) Integer boardNo,
                        Model model) {

        if (boardNo == null) { // 요청 데이터의 값이 없는 경우
            return "redirect:list";
        }
        BoardDto board = boardService.findBoardByBoardNo(boardNo);
        model.addAttribute("board", board);

        return "board/detail";
    }

    @PostMapping(path= { "/update" })
    public ResponseEntity<?> updateBoard(@ModelAttribute BoardDto board, HttpSession session) {
        EmployeeDto employee = (EmployeeDto)session.getAttribute("loginUser");
        // 로그인 사용자와 글 작성자 확인
        if (board.getEmpId() == employee.getEmpId()) {
            if (board.getBoardTitle() == null || board.getBoardTitle().isEmpty() ||
                board.getBoardContent() == null || board.getBoardContent().isEmpty()) {
                return ResponseEntity.badRequest().body("제목과 내용을 입력해야 합니다.");
            }else{
            // 수정 로직 처리
            boardService.updateBoard(board);
            return ResponseEntity.ok("수정 성공");
            }
        } else {
            // 권한 없음 처리
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteBoard(@RequestParam Long empId, @RequestParam Long boardNo, HttpSession session) {
        EmployeeDto employee = (EmployeeDto)session.getAttribute("loginUser");
        if (empId == employee.getEmpId()) {
            // 삭제 로직 처리
            boolean isDeleted = boardService.deleteBoard(boardNo); // 적절한 서비스 메서드 호출

            if (isDeleted) {
                return ResponseEntity.ok("삭제 성공");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
            }
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
        }
    }

    @GetMapping(path = { "/write" })
    public String writeForm(@RequestParam(value = "positionNo", required = false) Integer positionNo, RedirectAttributes attributes) {
        if (positionNo == null || positionNo < 1) {
            attributes.addFlashAttribute("alertMessage", "권한 등급이 낮습니다");
            return "redirect:/board/list"; // 4보다 작으면 /board/list로 리다이렉트
        }
        return "/board/write"; // 조건을 만족하지 않으면 write 페이지로 이동
    }

    @PostMapping(path = { "/write" })
    public String write(@ModelAttribute("board") BoardDto board,
                        @RequestParam(value = "attach[]", required = false) MultipartFile[] attach,
                        @RequestParam(value = "positionName", required = false) String positionName,
                        HttpServletRequest req, HttpSession session
    ) {
        EmployeeDto employee = (EmployeeDto)session.getAttribute("loginUser");
        ArrayList<BoardAttachDto> attachments = new ArrayList<>();
        // 첨부파일 처리
        if (attach != null && attach.length > 0) { // 첨부파일이 존재할 경우
            try {
                String dir = req.getServletContext().getRealPath("/board-attachments");

                for (MultipartFile file : attach) { // 각 파일에 대해 반복 처리
                    if (!file.isEmpty()) { // 파일이 비어있지 않은 경우
                        String boardUserFileName = file.getOriginalFilename();
                        String boardSavedFileName = Util.makeUniqueFileName(boardUserFileName);
                        file.transferTo(new File(dir, boardSavedFileName)); // 파일 저장

                        BoardAttachDto attachment = new BoardAttachDto();
                        attachment.setBoardUserFileName(boardUserFileName);
                        attachment.setBoardSavedFileName(boardSavedFileName);
                        attachments.add(attachment);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(); // 예외 발생 시 로그 출력
            }
        }
        boardService.writeBoard(board, attachments); // 각 수신자에게 메일 전송
        List<String> emailAddresses = boardService.getAllEmail(board.getEmpId());


        for (String recipient : emailAddresses) {
            // 알림 DTO 생성 및 저장
            NotificationDto notificationDto = new NotificationDto(recipient,  positionName +'-'+ employee.getEmpName() + " 님이 공지사항을 작성하였습니다", 2);
            notificationService.saveBoardNotification(notificationDto); // 알림 저장
        }

        // 나머지 처리 로직
        return "redirect:/board/list"; // 성공 시 리다이렉트
    }

    @GetMapping(path = { "/download" })
    public View downloadWithQueryString(@RequestParam("attachno") int attachNo, Model model) {

        BoardAttachDto boardAttach = boardService.findBoardAttachByAttachNo(attachNo);

        model.addAttribute("attach", boardAttach); // View에서 사용하도록 데이터 전달

        // 다운로드 처리 -> 사용자 정의 View 사용
        return new BoardDownloadView();

    }


}
