package com.example.sheabutterledgersystem.service;

import com.example.sheabutterledgersystem.model.Batch;
import com.example.sheabutterledgersystem.model.TraceabilityEvent;
import com.example.sheabutterledgersystem.repository.BatchRepository;
import com.example.sheabutterledgersystem.repository.TraceabilityEventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TraceabilityEventService {

    private final TraceabilityEventRepository eventRepository;
    private final BatchRepository batchRepository;

    @Transactional
    public TraceabilityEvent createEvent(TraceabilityEvent event) {
        return eventRepository.save(event);
    }

    @Transactional
    public TraceabilityEvent createEvent(UUID batchId, String eventType, String location, String description) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new EntityNotFoundException("Batch not found with id: " + batchId));

        TraceabilityEvent event = new TraceabilityEvent();
        event.setBatch(batch);
        event.setEventType(eventType);
        event.setLocation(location);
        event.setDescription(description);

        return eventRepository.save(event);
    }

    public TraceabilityEvent getEventById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
    }

    public List<TraceabilityEvent> getEventsForBatch(UUID batchId) {
        return eventRepository.findByBatchIdOrderByEventDateDesc(batchId);
    }

    public Page<TraceabilityEvent> getEventsForBatch(UUID batchId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").descending());
        return eventRepository.findByBatchId(batchId, pageable);
    }

    public List<TraceabilityEvent> getBatchTimeline(UUID batchId) {
        return eventRepository.getBatchTimeline(batchId);
    }

    public List<TraceabilityEvent> getEventsByType(String eventType) {
        return eventRepository.findByEventTypeOrderByEventDateDesc(eventType);
    }

    public List<TraceabilityEvent> getEventsByPerformer(UUID userId) {
        return eventRepository.findByPerformedById(userId);
    }

    public List<TraceabilityEvent> getEventsInDateRange(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByEventDateBetween(start, end);
    }

    public TraceabilityEvent getLatestEventForBatch(UUID batchId) {
        Pageable pageable = PageRequest.of(0, 1);
        List<TraceabilityEvent> events = eventRepository.findLatestEvent(batchId, pageable);
        return events.isEmpty() ? null : events.get(0);
    }

    public List<Object[]> getEventTypeStatistics() {
        return eventRepository.countByEventType();
    }

    @Transactional
    public TraceabilityEvent updateEvent(UUID id, TraceabilityEvent eventDetails) {
        TraceabilityEvent event = getEventById(id);

        event.setEventType(eventDetails.getEventType());
        event.setLocation(eventDetails.getLocation());
        event.setDescription(eventDetails.getDescription());
        event.setPerformedBy(eventDetails.getPerformedBy());

        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(UUID id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }
}