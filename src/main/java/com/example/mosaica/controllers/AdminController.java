package com.example.mosaica.controllers;

import com.example.mosaica.entity.AccessCode;
import com.example.mosaica.service.AccessCodeService;
import com.example.mosaica.service.ConvertService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AccessCodeService accessCodeService;
    private final ConvertService convertService;

    public AdminController(AccessCodeService accessCodeService,
                           ConvertService convertService) {
        this.accessCodeService = accessCodeService;
        this.convertService = convertService;
    }

    @GetMapping
    public String adminFirstPage() {
        return "redirect:/admin/page/0";
    }

    @GetMapping("/page/{pageNumber}")
    public String adminPage(Model model, @PathVariable Integer pageNumber) {
        Page<AccessCode> page = accessCodeService.getPage(pageNumber);
        int totalPages = page.getTotalPages();
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageContent", page.getContent());
        model.addAttribute("currentPage", pageNumber);
        return "admin";
    }

    @GetMapping("/delete/{uuid}")
    public String delete(@PathVariable String uuid) {
        accessCodeService.deleteAccessCode(uuid);
        return "redirect:/admin";
    }

    @PostMapping("/new")
    public String newCode(@RequestParam("code") String code) {
        if (code == null || code.isEmpty())
            return "redirect:/admin";

        accessCodeService.createAccessCode(code);
        return "redirect:/admin";
    }

    @PostMapping("/many-new")
    public String newCode(@RequestParam("amount") Integer amount) {
        if (amount == null || amount <= 0 || amount > 9999)
            return "redirect:/admin";

        accessCodeService.createAccessCodes(amount);
        return "redirect:/admin";
    }

    @GetMapping("/imgs/{code}")
    public String getImgs(Model model, @PathVariable String code) {
        return accessCodeService.getCodeId(code).map(codeId -> {
            var images = convertService.getImagesForCodeId(codeId);
            model.addAttribute("images", images);
            return "view-images";
        }).orElse("redirect:/admin");
    }

    @ModelAttribute("allCodeStates")
    public List<AccessCode.State> allCodeStates() {
        return Arrays.asList(AccessCode.State.values());
    }
}
