package com.rwh.tracker.controller;

import com.rwh.tracker.model.Component;
import com.rwh.tracker.model.MaintenanceLog;
import com.rwh.tracker.service.SystemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Dashboard controller — shows summary cards, component list with search,
 * and per-component maintenance history.
 */
@Controller
public class DashboardController {

    private final SystemService systemService;

    public DashboardController(SystemService systemService) {
        this.systemService = systemService;
    }

    // ── Dashboard with search and summary ─────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(name = "q", required = false) String query,
                            Model model) {
        List<Component> components = systemService.searchComponents(query);

        // Compute summary counts
        LocalDate today = LocalDate.now();
        long healthyCount = 0;
        long dueSoonCount = 0;
        long overdueCount = 0;

        for (Component c : components) {
            if (c.getNextDueDate() == null) {
                // No due date — treat as healthy (unknown)
                healthyCount++;
            } else {
                long daysLeft = ChronoUnit.DAYS.between(today, c.getNextDueDate());
                if (daysLeft < 0) {
                    overdueCount++;
                } else if (daysLeft <= 7) {
                    dueSoonCount++;
                } else {
                    healthyCount++;
                }
            }
        }

        model.addAttribute("components", components);
        model.addAttribute("query", query);
        model.addAttribute("healthyCount", healthyCount);
        model.addAttribute("dueSoonCount", dueSoonCount);
        model.addAttribute("overdueCount", overdueCount);
        model.addAttribute("totalCount", (long) components.size());

        return "dashboard/dashboard";
    }

    // ── Component maintenance history ─────────────────────────────────────────

    @GetMapping("/dashboard/components/{id}")
    public String componentHistory(@PathVariable Long id, Model model) {
        Component component = systemService.getComponent(id);
        List<MaintenanceLog> history = systemService.getComponentHistory(id);
        model.addAttribute("component", component);
        model.addAttribute("history", history);
        return "dashboard/component-history";
    }
}
