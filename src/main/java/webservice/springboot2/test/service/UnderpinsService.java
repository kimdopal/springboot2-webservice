package webservice.springboot2.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webservice.springboot2.test.domain.underpins.Underpins;
import webservice.springboot2.test.domain.underpins.UnderpinsRepository;
import webservice.springboot2.test.web.dto.UnderpinsDto.UnderpinsListResponseDto;
import webservice.springboot2.test.web.dto.UnderpinsDto.UnderpinsResponseDto;
import webservice.springboot2.test.web.dto.UnderpinsDto.UnderpinsSaveRequestDto;
import webservice.springboot2.test.web.dto.UnderpinsDto.UnderpinsUpdateRequestDto;

import java.util.List;
import java.util.stream.Collectors;
/*
 *************************************************************************
 * 응원글 CRUD를 하는 클래스입니다.
 *************************************************************************
 */
@RequiredArgsConstructor    // final 붙은 변수로 생성자 생성해준다
@Service    // controller는 DAO(Repository)에 직접 접근하지 않고 service를 이용하여 요청을 처리한다
public class UnderpinsService {

    private final UnderpinsRepository underpinsRepository;

    @Transactional
    public Long save(UnderpinsSaveRequestDto requestDto) {
        return underpinsRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, UnderpinsUpdateRequestDto requestDto){
        Underpins underpins = underpinsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 응원글이 없습니다. id="+id));
        underpins.update(requestDto.getContent(), requestDto.getIsAppend());

        return id;
    }

    @Transactional
    public Long delete(Long id){
        // 존재하는  Posts인지 확인을 위해 엔티티 조회 후
        Underpins underpins = underpinsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 응원글이 없습니다. id="+id));
        // 삭제
        underpinsRepository.delete(underpins);
        return id;
    }

    public UnderpinsResponseDto findById(Long id) {
        Underpins entity = underpinsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 응원글이 없습니다. id="+id));

        return new UnderpinsResponseDto(entity);
    }


    // 트랜잭션 범위는 유지 / 조회속도개선 -> 등록,수정,삭제 없는 서비스메소드에서 사용 추천
    @Transactional(readOnly = true)
    public List<UnderpinsListResponseDto> findAllDesc(){
        return underpinsRepository.findAllDesc().stream()
                .map(UnderpinsListResponseDto::new)
                .collect(Collectors.toList());
    }

    // 적용할 응원글 모음
    @Transactional(readOnly = true)
    public List<UnderpinsListResponseDto> findByWillAppend(){
        return underpinsRepository.findByIsAppendOrderByIdDesc(1).stream()
                .map(UnderpinsListResponseDto::new)
                .collect(Collectors.toList());
    }
}