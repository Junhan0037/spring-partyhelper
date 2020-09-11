package com.partyhelper.modules.settings;

import com.partyhelper.modules.settings.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    @PostConstruct // Bean이 초기화 된 후 실행
    public void initZoneData() throws IOException {
        if (zoneRepository.count() == 0) {
//            Resource resource = new ClassPathResource("zones_kr.csv");
            String absolutePath = ResourceUtils.getFile("classpath:zones_kr.csv").getAbsolutePath();
            Path path = Paths.get(absolutePath);
            List<Zone> zoneList = Files.readAllLines(path, StandardCharsets.UTF_8).stream()
                    .map(line -> {
                        String[] split = line.split(",");
                        return Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build();
                    }).collect(Collectors.toList());
//            List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
//                    .map(line -> {
//                        String[] split = line.split(",");
//                        return Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build();
//                    }).collect(Collectors.toList());
            zoneRepository.saveAll(zoneList);
        }
    }

}
