package com.crc.crcloud.steam.iam.api.feign.callback;

import com.crc.crcloud.steam.iam.api.feign.FileFeignClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description 文件服务
 */
@Component
public class FileFeignClientFallback implements FileFeignClient {

    @Override
    public ResponseEntity<String> uploadFile(String bucketName, String fileName, MultipartFile multipartFile) {
        throw new CommonException("error.file.upload");
    }

    @Override
    public ResponseEntity deleteFile(String bucketName, String url) {
        throw new CommonException("error.file.delete");
    }
}
