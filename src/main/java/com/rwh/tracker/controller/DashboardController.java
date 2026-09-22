package com.rwh.tracker.controller;

import com.rwh.tracker.model.Alert;
import com.rwh.tracker.model.AlertStatus;
import com.rwh.tracker.model.Component;
import com.rwh.tracker.model.MaintenanceLog;
import com.rwh.tracker.service.SystemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final SystemService systemService;

    public DashboardController(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping
    public String dashboard(@RequestParam(value = "q", required = false) String query, Model model) {
        // Refresh alert statuses first
        List<Alert> activeAlerts = systemService.getPendingAndOverdueAlerts();
        
        long overdueCount = activeAlerts.stream()
                .filter(a -> a.getStatus() == AlertStatus.OVERDUE)
                .count();
                
        long dueSoonCount = activeAlerts.stream()
                .filter(a -> a.getStatus() == AlertStatus.PENDING) // pending but due within 7 days is filtered by service
                .count();

        List<Component> allComponents = systemService.getAllComponents();
        long totalCount = allComponents.size();
        long healthyCount = totalCount - overdueCount - dueSoonCount;
        
        List<Component> displayComponents = systemService.searchComponents(query);
        
        model.addAttribute("overdueCount", overdueCount);
        model.addAttribute("dueSoonCount", dueSoonCount);
        model.addAttribute("healthyCount", healthyCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("components", displayComponents);
        model.addAttribute("query", query);
        
        return "dashboard/dashboard";
    }

    @GetMapping("/components/{id}")
    public String componentHistory(@PathVariable Long id, Model model) {
        Component component = systemService.getComponent(id);
        List<MaintenanceLog> history = systemService.getComponentHistory(id);
        
        model.addAttribute("component", component);
        model.addAttribute("history", history);
        
        return "dashboard/component-history";
    }
}
