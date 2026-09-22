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

/**
 * MVC controller for all System & Component Registration routes.
 */
@Controller
@RequestMapping("/systems")
public class SystemController {

    private final SystemService systemService;

    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    // ── GET /systems ──────────────────────────────────────────────────────────

    /** Lists all registered systems. */
    @GetMapping
    public String listSystems(Model model) {
        model.addAttribute("systems", systemService.listSystems());
        return "systems/systems-list";
    }

    // ── GET /systems/new ──────────────────────────────────────────────────────

    /** Shows the blank system creation form. */
    @GetMapping("/new")
    public String newSystemForm(Model model) {
        model.addAttribute("system", new RwhSystem());
        return "systems/system-form";
    }

    // ── POST /systems ─────────────────────────────────────────────────────────

    /** Handles system creation form submission. */
    @PostMapping
    public String createSystem(@Valid @ModelAttribute("system") RwhSystem system,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "systems/system-form";
        }
        RwhSystem saved = systemService.createSystem(system);
        redirectAttributes.addFlashAttribute("successMessage",
                "System \"" + saved.getName() + "\" registered successfully!");
        return "redirect:/systems/" + saved.getId();
    }

    // ── GET /systems/{id} ─────────────────────────────────────────────────────

    /** Shows system details and an inline form to add a new component. */
    @GetMapping("/{id}")
    public String viewSystem(@PathVariable Long id, Model model) {
        RwhSystem system = systemService.getSystem(id);
        model.addAttribute("system", system);
        model.addAttribute("components", systemService.listComponents(id));
        model.addAttribute("componentTypes", ComponentType.values());

        // Provide a blank component form object if not already present (e.g. after validation error)
        if (!model.containsAttribute("newComponent")) {
            model.addAttribute("newComponent", new Component());
        }
        return "systems/system-detail";
    }

    // ── POST /systems/{id}/components ─────────────────────────────────────────

    /** Handles inline component addition. */
    @PostMapping("/{id}/components")
    public String addComponent(@PathVariable Long id,
                               @Valid @ModelAttribute("newComponent") Component component,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            // Re-render the detail page with validation errors
            RwhSystem system = systemService.getSystem(id);
            model.addAttribute("system", system);
            model.addAttribute("components", systemService.listComponents(id));
            model.addAttribute("componentTypes", ComponentType.values());
            return "systems/system-detail";
        }
        systemService.addComponent(id, component);
        redirectAttributes.addFlashAttribute("successMessage",
                "Component \"" + component.getName() + "\" added successfully!");
        return "redirect:/systems/" + id;
    }
}
