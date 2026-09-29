package com.rwh.tracker.controller;

import com.rwh.tracker.model.Component;
import com.rwh.tracker.model.ComponentType;
import com.rwh.tracker.model.RwhSystem;
import com.rwh.tracker.service.SystemService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Handles CRUD operations for RWH Systems and adding components to systems.
 */
@Controller
public class SystemController {

    private final SystemService systemService;

    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    // ── List all systems ──────────────────────────────────────────────────────

    @GetMapping("/systems")
    public String listSystems(Model model) {
        List<RwhSystem> systems = systemService.listSystems();
        model.addAttribute("systems", systems);
        return "systems/systems-list";
    }

    // ── New system form ───────────────────────────────────────────────────────

    @GetMapping("/systems/new")
    public String newSystemForm(Model model) {
        model.addAttribute("system", new RwhSystem());
        return "systems/system-form";
    }

    // ── Create system ─────────────────────────────────────────────────────────

    @PostMapping("/systems")
    public String createSystem(@Valid @ModelAttribute("system") RwhSystem system,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "systems/system-form";
        }
        RwhSystem saved = systemService.createSystem(system);
        redirectAttributes.addFlashAttribute("successMessage",
                "System '" + saved.getName() + "' registered successfully!");
        return "redirect:/systems";
    }

    // ── System detail (view + add component form) ─────────────────────────────

    @GetMapping("/systems/{id}")
    public String viewSystem(@PathVariable Long id, Model model) {
        RwhSystem system = systemService.getSystem(id);
        List<Component> components = systemService.listComponents(id);
        model.addAttribute("system", system);
        model.addAttribute("components", components);
        model.addAttribute("newComponent", new Component());
        model.addAttribute("componentTypes", ComponentType.values());
        return "systems/system-detail";
    }

    // ── Add component to system ───────────────────────────────────────────────

    @PostMapping("/systems/{id}/components")
    public String addComponent(@PathVariable Long id,
                               @Valid @ModelAttribute("newComponent") Component component,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            RwhSystem system = systemService.getSystem(id);
            model.addAttribute("system", system);
            model.addAttribute("components", systemService.listComponents(id));
            model.addAttribute("componentTypes", ComponentType.values());
            return "systems/system-detail";
        }
        Component saved = systemService.addComponent(id, component);
        redirectAttributes.addFlashAttribute("successMessage",
                "Component '" + saved.getName() + "' added successfully!");
        return "redirect:/systems/" + id;
    }

    // ── Root redirect ─────────────────────────────────────────────────────────

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}
