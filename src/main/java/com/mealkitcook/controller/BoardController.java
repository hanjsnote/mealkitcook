package com.mealkitcook.controller;

import com.mealkitcook.dto.BoardDto;
import com.mealkitcook.entity.Board;
import com.mealkitcook.repository.BoardRepository;
import com.mealkitcook.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    private BoardService boardService;
    //Service를 통해 boardDtoList를 boardList로 넘겨받아서 뷰에 전달
    public BoardController(BoardService boardService){
        this.boardService = boardService;
    }

    /*//Qna리스트 목록을 보여주는 경로
    @GetMapping(value = "/board_list")
    public String list(Model model){
        List<BoardDto> boardDtoList = boardService.getBoardList();
        model.addAttribute("boardList", boardDtoList);
        return "board/board_list.html";
    }*/

    /*@GetMapping(value = "/board/board_list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "1") Integer pageNum){
        List<BoardDto> boardDtoList = boardService.getBoardList(pageNum);
        Integer[] pageList = boardService.getPageList(pageNum);

        model.addAttribute("boardList", boardDtoList);
        model.addAttribute("pageList", pageList);
        return "board/board_list.html";
    }*/

    @GetMapping(value = "/board/board_list")
    public String list(Model model, @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                       @RequestParam(required = false, defaultValue = "") String searchText){
        //Page<Board> boardDtoList = boardRepository.findAll(pageable);
        Page<Board> boardDtoList = boardRepository.findByTitleContainingOrContentContaining(searchText, searchText, pageable);
       int startPage = Math.max(1, boardDtoList.getPageable().getPageNumber() - 4);
        int endPage = Math.min(boardDtoList.getTotalPages(), boardDtoList.getPageable().getPageNumber() + 4);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("boardList", boardDtoList);

        return "board/board_list.html";
    }




    //board_write.html 매핑
    @GetMapping("/post")
    public String board_write(){
        return "board/board_write.html";
    }
    //board_writer.html에서 등록버튼을 누루면 다시 게시판 목록으로 돌아간다

    @PostMapping("/board/write")
    public String board_write(@Valid Board board, Authentication authentication) {

        String email = authentication.getName();
        boardService.save(email, board);
       /* boardService.savePost(dto);*/
        return "redirect:board_list";
    }
    //detail.html 경로 추가
    @GetMapping("/board/{no}")
    public String detail(@PathVariable("no") Long id, Model model){
        BoardDto boardDto = boardService.getPost(id);
        model.addAttribute("boardDto", boardDto);
        return "board/board_detail.html";
    }

    //update.html 경로 추가
    @GetMapping("/board/edit/{no}")
    public String edit(@PathVariable("no") Long id, Model model){
        BoardDto boardDto = boardService.getPost(id);
        model.addAttribute("boardDto", boardDto);
        return "board/board_update.html";
    }

    //db에 새로운 데이터 저장
    @PostMapping("/board/edit/{no}")
    public String edit(@Valid BoardDto dto, Board board, Authentication authentication){
        String email = authentication.getName();
        boardService.save(email, board);
        /*boardService.savePost(dto);*/
        return "redirect:/board/"+dto.getId();
    }


    //삭제 경로 추가
    @PostMapping("/board/delete")
    public String delete(Long id){
        boardService.deletePost(id);
        return "redirect:/board/board_list";
    }

    //검색 경로 추가



}
