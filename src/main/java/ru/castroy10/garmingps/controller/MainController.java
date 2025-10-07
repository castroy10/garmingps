package ru.castroy10.garmingps.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
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
        return gpsCreatorService.createGpxTrack(coordinate);
    }

    @PostMapping("/text")
    public ResponseEntity<byte[]> createFromText(@RequestBody final List<String> text) {
        return gpsCreatorService.createGpxTrackFromText(text);
    }
}
