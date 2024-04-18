package com.example.mosaica.controllers;

import com.example.mosaica.service.AccessCodeService;
import com.example.mosaica.service.ConvertService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class ConvertController {

    private final ConvertService convertService;
    private final AccessCodeService accessCodeService;

    public ConvertController(ConvertService convertService,
                             AccessCodeService accessCodeService) {
        this.convertService = convertService;
        this.accessCodeService = accessCodeService;
    }

    @GetMapping("/")
    public String mosaica() {
        return "enter-code";
    }

    @PostMapping("/mosaica")
    public String mosaica(
            Model model,
            @RequestParam("image") MultipartFile file,
            @RequestParam("size") String size,
            @RequestParam("palette") String palette,
            @RequestParam("codeId") String codeId) throws IOException {
        if (file == null || file.isEmpty() || codeId == null || codeId.isEmpty())
            return "enter-code";

        var images = convertService.tryConvertImageUsingCodeId(codeId, file, size, palette);

        if (images == null)
            return "enter-code";

        model.addAttribute("images", images);
        model.addAttribute("code", images.code());
        return "view-images";
    }

    @PostMapping("/code")
    public String proceed(Model model, @RequestParam("code") String code) {
        if (code == null || code.isEmpty())
            return "redirect:/enter-code";

        return accessCodeService.getCodeId(code).map(codeId -> {
            if (accessCodeService.isCodeUsedById(codeId)) {
                var images = convertService.getImagesForCodeId(codeId);
                model.addAttribute("images", images);
                model.addAttribute("code", code);
                return "view-images";
            }

            model.addAttribute("codeId", codeId);

            return "generate";
        }).orElseGet(() -> {
            model.addAttribute("invalidCode", true);
            return "enter-code";
        });
    }

    @GetMapping("/code/{code}/blocks")
    public String blocks(Model model, @PathVariable String code) {
        List<List<Integer>> blocks = convertService.getBlocksForCode(code);
        model.addAttribute("blocks", blocks);
        return "blocks";
    }

    @GetMapping("/code/{code}/block")
    public String colors(Model model,
                         @PathVariable String code,
                         @RequestParam(name = "n", defaultValue = "0") Integer n) {
        return convertService.getColorsForBlock(code, n).map(res -> {
            model.addAttribute("totalBlocks", res.totalBlocks());
            model.addAttribute("colors", res.blockColors());
            model.addAttribute("palette", res.palette());
            model.addAttribute("currentBLock", n);
            return "block";
        }).orElse("redirect:/");
    }

    @GetMapping(value = "/palette/{palette}.css", produces = "text/css")
    @ResponseBody
    public String palette(@PathVariable String palette) {
        return convertService.getPaletteCss(palette);
    }
}
