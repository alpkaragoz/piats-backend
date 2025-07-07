package com.piats.backend.services;

import com.piats.backend.dto.ConfessionRequestDto;
import com.piats.backend.dto.ConfessionResponseDto;
import com.piats.backend.exceptions.BadRequestException;
import com.piats.backend.models.Confession;
import com.piats.backend.repos.ConfessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfessionService {

    private final ConfessionRepository confessionRepository;

    public List<ConfessionResponseDto> getAllConfessions() {
        List<Confession> confessionList;
        try {
            confessionList = confessionRepository.findAll();
        } catch(Exception e) {
            throw new BadRequestException("Something went wrong retrieving data.");
        }
        return confessionList.stream().map(this::convertConfessionResponseDto).toList();
    }

    public ConfessionRequestDto createConfession(ConfessionRequestDto confessionRequestDto) {
        if(confessionRequestDto.getNickname().length() > 50) {
            throw new BadRequestException("err");
        } else if(confessionRequestDto.getConfessionText().length() > 256) {
            throw new BadRequestException("err");
        }
        Confession confession = new Confession();
        confession.setDepartment(confessionRequestDto.getDepartment());
        confession.setConfessionText(confessionRequestDto.getConfessionText());
        confession.setNickname(confessionRequestDto.getNickname());
        try {
            confessionRepository.save(confession);
        }
        catch (Exception e) {
            throw new BadRequestException("Something went wrong creating confession.");
        }

        return confessionRequestDto;
    }

    private ConfessionResponseDto convertConfessionResponseDto(Confession c) {
        ConfessionResponseDto confessionRequestDto = new ConfessionResponseDto();
        confessionRequestDto.setDepartment(c.getDepartment());
        confessionRequestDto.setNickname(c.getNickname());
        confessionRequestDto.setConfessionText(c.getConfessionText());
        confessionRequestDto.setCreatedAt(c.getCreatedAt());
        return confessionRequestDto;
    }
}
