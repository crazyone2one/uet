package cn.master.uet.controller;

import cn.master.uet.service.ResolveExcelService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

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

    @RequestMapping(value = "/downloadTemplate", method = RequestMethod.GET)
    @ResponseBody
    public void downloadTemplate(HttpServletResponse response) {
        InputStream inputStream = null;
        ServletOutputStream servletOutputStream = null;
        try {
            Resource resource = new DefaultResourceLoader().getResource("classpath:test-case-template.xlsx");
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + "test-case-template.xlsx");
            inputStream = resource.getInputStream();
            servletOutputStream = response.getOutputStream();
            IOUtils.copy(inputStream, servletOutputStream);
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (Objects.nonNull(servletOutputStream)) {
                    servletOutputStream.flush();
                    servletOutputStream.close();
                }
                if (Objects.nonNull(inputStream)) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
