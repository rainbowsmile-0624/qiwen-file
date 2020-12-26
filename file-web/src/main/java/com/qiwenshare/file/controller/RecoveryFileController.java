package com.qiwenshare.file.controller;

import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.dto.BatchDeleteRecoveryFileDTO;
import com.qiwenshare.file.dto.DeleteRecoveryFileDTO;
import com.qiwenshare.file.service.RecoveryFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "recoveryfile", description = "文件删除后会进入回收站，该接口主要是对回收站文件进行管理")
@RestController
@Slf4j
@RequestMapping("/recoveryfile")
public class RecoveryFileController {
    @Resource
    RecoveryFileService recoveryFileService;


    public RestResult<String> batchDeleteRecoveryFile(@RequestBody BatchDeleteRecoveryFileDTO batchDeleteRecoveryFileDto, @RequestHeader("token") String token) {
        RestResult<String> restResult = new RestResult<>();

        return null;
    }

    @Operation(summary = "删除回收文件", description = "删除回收文件", tags = {"recoveryfile"})
    @RequestMapping(value = "/deleterecoveryfile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> deleteRecoveryFile(@RequestBody DeleteRecoveryFileDTO deleteRecoveryFileDTO, @RequestHeader("token") String token) {
        RestResult<String> restResult = new RestResult<String>();
        recoveryFileService.removeById(deleteRecoveryFileDTO.getRecoveryFileId());

        
        restResult.setSuccess(true);
        restResult.setData("删除成功");
        return restResult;
    }

    @Operation(summary = "回收文件列表", description = "回收文件列表", tags = {"recoveryfile"})
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<List<RecoveryFile>> getRecoveryFileList(@RequestHeader("token") String token) {
        RestResult<List<RecoveryFile>> restResult = new RestResult<List<RecoveryFile>>();
        List recoveryFileList = recoveryFileService.list();
        restResult.setData(recoveryFileList);
        restResult.setSuccess(true);

        return restResult;
    }

}