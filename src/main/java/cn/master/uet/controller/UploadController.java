package cn.master.uet.controller;

import cn.master.uet.service.ResolveExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author by 11's papa on 2021年05月28日
 * @version 1.0.0
 */
@Controller
public class UploadController {
    @Autowired
    private ResolveExcelService resolveExcelService;

    @GetMapping("/")
    public String uploadPage() {
        return "upload";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "文件不能为空";
        }
        return resolveExcelService.uploadCases(resolveExcelService.resolveExcel(file));
    }
}
