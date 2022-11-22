package com.mealkitcook.service;

import com.mealkitcook.dto.BoardDto;
import com.mealkitcook.entity.Board;
import com.mealkitcook.entity.Member;
import com.mealkitcook.repository.BoardRepository;
import com.mealkitcook.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

  /*  private static final int BLOCK_PAGE_NUM_COUNT = 5;  //불럭에 존재하는 페이지 수
    private static final int PAGE_POST_COUNT = 4;   //한 페이지에 존재하는 게시글 수*/

    public BoardService(BoardRepository boardRepository){
        this.boardRepository = boardRepository;
    }

    public Board save(String email, Board board){
        Member member = memberRepository.findByEmail(email);
        board.setMember(member);
        return boardRepository.save(board);
    }

    @Transactional
    public Long savePost(BoardDto boardDto){
        return boardRepository.save(boardDto.toEntity()).getId();
    }

    //getBoardList메소드 구현 db에 저장되어있는 전체데이터를 불러온다 repository에서 모든 데이터를 가져와 데이터만큼 반복하면서 BoardDto타입의 List데이터를 파싱하여 집어넣고 완성된 BoardDto타입의 List를 리턴
    @Transactional
    public List<BoardDto> getBoardList(Integer pageNum){

       /* Page<Board> page = boardRepository
                .findAll(PageRequest
                        .of(pageNum-1, PAGE_POST_COUNT, Sort.by(Sort.Direction.DESC, "createdDate")));*/

//        List<Board> boards = boardRepository.findAll();
        List<Board> boards = boardRepository.findAll();
        List<BoardDto> boardDtoList = new ArrayList<>();

        for(Board board : boards){
            BoardDto dto = BoardDto.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .createdDate(board.getCreatedDate())
                    .build();

            boardDtoList.add(dto);
        }
        return boardDtoList;
    }

    // 상세페이지 Repository를 통해 게시글 id값을 받아 리턴 추가
    @Transactional
    public BoardDto getPost(Long id){
        Optional<Board> boardWrapper = boardRepository.findById(id);
        if(boardWrapper.isPresent())
        {
            Board board = boardWrapper.get();

            BoardDto boardDto = BoardDto.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .createdDate(board.getCreatedDate())
                    .build();

            return boardDto;
        }

        return null;
    }

    @Transactional
    public void deletePost(Long id){
        Optional<Board> optBoard = boardRepository.findById(id);
        if(optBoard.isPresent()){
            Board board = optBoard.get();
            boardRepository.deleteById(id);
        }
    }





   /* public Integer[] getPageList(Integer curPageNum){
        Integer[] pageList = new Integer[BLOCK_PAGE_NUM_COUNT];

        //총 게시글 수
        Double postsTotalCount = Double.valueOf(this.getBoardCount());

        //총 게시글 수를 기준으로 계산한 마지막 페이지 번호 계산
        Integer totalLastPageNum = (int)(Math.ceil((postsTotalCount/PAGE_POST_COUNT)));

        //현재 페이지를 기준으로 블럭의 마지막 페이지 번호 계산
        Integer blockLastPageNum = (totalLastPageNum > curPageNum + BLOCK_PAGE_NUM_COUNT)
                ? curPageNum + BLOCK_PAGE_NUM_COUNT
                : totalLastPageNum;

        //페이지 시작 번호 조정
        curPageNum = (curPageNum<=3) ? 1 : curPageNum-2;

        //페이지 번호 할당
        for(int val=curPageNum, i=0; val<=blockLastPageNum; val++, i++){
            pageList[i] = val;
        }
        return pageList;
    }

    @Transactional
    public Long getBoardCount(){
        return boardRepository.count();
    }*/

}
