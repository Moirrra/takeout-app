package com.sky.controller.admin;

import com.sky.constant.FilePathConstant;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    /**
     * 本地文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) { // file参数名与前端保持一致
        log.info("文件上传：{}", file);
        try {
            // 原始文件名
            String originalFileName = file.getOriginalFilename();
            // 原始文件后缀
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            // 构造新文件名
            String objectName = UUID.randomUUID().toString() + extension;
            // 本地保存路径
            String savePath = FilePathConstant.UPLOAD_DIR + objectName;
            // 返回路径
            String returnImagePath = FilePathConstant.IMAGE_DIR + objectName;
            // 保存文件到本地
            file.transferTo(new File(savePath));

            return Result.success(returnImagePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

    // todo 阿里云文件上传
}
