package ru.castroy10.garmingps.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.castroy10.garmingps.model.Coordinate;
import ru.castroy10.garmingps.service.GpsCreatorService;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final GpsCreatorService gpsCreatorService;

    @GetMapping()
    public String index() {
        return "index";
    }

    @PostMapping("/create")
    public ResponseEntity<byte[]> create(@RequestBody final List<Coordinate> coordinate) {
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"output.xlsx\"")
                             .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                             .body(gpsCreatorService.getGpsTrack(coordinate));
    }
}
