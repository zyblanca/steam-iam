package com.crc.crcloud.steam.iam.common.config;

import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.UserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 如果请求上传了文件
 * 进行文件校验
 */
@Slf4j
public class FileInterceptor extends HandlerInterceptorAdapter {
    /**
     * 非安全后缀名
     */
    private static final String[] SUFFIX_NAME = {"bat", "cmd", "com", "exe", "sh", "php", "java", "js", "py", "int", "sys", "dll", "adt", "iso"};

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(request instanceof MultipartRequest)) {
            return true;
        }
        MultipartRequest multipartRequest = ((MultipartRequest) request);
        Collection<List<MultipartFile>> files = multipartRequest.getMultiFileMap().values();
        if (CollectionUtils.isEmpty(files)) {
            return true;
        }

        files.forEach(v ->
                v.forEach(this::checkFileSafe)
        );
        return true;
    }

    /**
     * 对文件类型进行校验
     * 1.文件后缀名在非安全范围内
     * 2.特殊文件进行文件头比对(当前不做处理)
     *
     * @param file 文件信息
     */
    private void checkFileSafe(MultipartFile file) {
        String fileName;
        if (Objects.isNull(fileName = file.getOriginalFilename())) {
            return;
        }
        fileName = fileName.trim().toLowerCase(Locale.US);

        for (String endName : SUFFIX_NAME) {
            if (fileName.endsWith(endName)) {
                log.warn("用户{}，上传特殊文件{}", UserDetail.getUserId(), fileName);
                throw new IamAppCommException("error.upload.file.type");
            }
        }

    }
}
