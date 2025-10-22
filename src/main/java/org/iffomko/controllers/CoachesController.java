package org.iffomko.controllers;

import org.iffomko.domain.Coach;
import org.iffomko.services.CoachService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.iffomko.domain.ControllerNames.COACHES_URL;

@RestController
@RequestMapping(COACHES_URL)
public class CoachesController {
    private final CoachService coachService;

    public CoachesController(CoachService coachService) {
        this.coachService = coachService;
    }

    @GetMapping
    public List<Coach> loadAll() {
        return coachService.findAll();
    }
    
    @DeleteMapping("/{id}")
    public void deleteCoach(@PathVariable("id") int coachId) {
        coachService.deleteCoachWithReservations(coachId);
    }
}
