package com.example.sheabutterledgersystem.controller;

import com.example.sheabutterledgersystem.dto.response.TraceabilityEventResponse;
import com.example.sheabutterledgersystem.dto.response.PageResponse;
import com.example.sheabutterledgersystem.dto.mapper.TraceabilityEventMapper;
import com.example.sheabutterledgersystem.model.TraceabilityEvent;
import com.example.sheabutterledgersystem.service.TraceabilityEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/traceability")
@RequiredArgsConstructor
@Tag(name = "Traceability Event Management", description = "Endpoints for managing traceability events")
@CrossOrigin(origins = "*")
public class TraceabilityEventController {

    private final TraceabilityEventService eventService;
    private final TraceabilityEventMapper eventMapper;

    @GetMapping("/events/{id}")
    @Operation(summary = "Get event by ID")
    public ResponseEntity<TraceabilityEventResponse> getEventById(@PathVariable UUID id) {
        TraceabilityEvent event = eventService.getEventById(id);
        return ResponseEntity.ok(eventMapper.toResponse(event));
    }

    @GetMapping("/batches/{batchId}/events")
    @Operation(summary = "Get events for a batch")
    public ResponseEntity<PageResponse<TraceabilityEventResponse>> getEventsForBatch(
            @PathVariable UUID batchId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<TraceabilityEvent> eventPage = eventService.getEventsForBatch(batchId, page, size);
        Page<TraceabilityEventResponse> responsePage = eventPage.map(eventMapper::toResponse);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/batches/{batchId}/timeline")
    @Operation(summary = "Get full timeline for a batch")
    public ResponseEntity<List<TraceabilityEventResponse>> getBatchTimeline(@PathVariable UUID batchId) {
        List<TraceabilityEvent> events = eventService.getBatchTimeline(batchId);
        List<TraceabilityEventResponse> responses = events.stream()
                .map(eventMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/events/type/{eventType}")
    @Operation(summary = "Get events by type")
    public ResponseEntity<List<TraceabilityEventResponse>> getEventsByType(@PathVariable String eventType) {
        List<TraceabilityEvent> events = eventService.getEventsByType(eventType);
        List<TraceabilityEventResponse> responses = events.stream()
                .map(eventMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/events/performer/{userId}")
    @Operation(summary = "Get events by performer")
    public ResponseEntity<List<TraceabilityEventResponse>> getEventsByPerformer(@PathVariable UUID userId) {
        List<TraceabilityEvent> events = eventService.getEventsByPerformer(userId);
        List<TraceabilityEventResponse> responses = events.stream()
                .map(eventMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/events/date-range")
    @Operation(summary = "Get events by date range")
    public ResponseEntity<List<TraceabilityEventResponse>> getEventsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<TraceabilityEvent> events = eventService.getEventsInDateRange(start, end);
        List<TraceabilityEventResponse> responses = events.stream()
                .map(eventMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/batches/{batchId}/events/latest")
    @Operation(summary = "Get latest event for a batch")
    public ResponseEntity<TraceabilityEventResponse> getLatestEventForBatch(@PathVariable UUID batchId) {
        TraceabilityEvent event = eventService.getLatestEventForBatch(batchId);
        return event != null ? ResponseEntity.ok(eventMapper.toResponse(event)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/events/stats/by-type")
    @Operation(summary = "Get event statistics by type")
    public ResponseEntity<List<Map<String, Object>>> getEventTypeStatistics() {
        List<Object[]> stats = eventService.getEventTypeStatistics();
        List<Map<String, Object>> result = stats.stream()
                .map(stat -> Map.of(
                        "eventType", stat[0],
                        "count", stat[1]
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}