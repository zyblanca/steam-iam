package com.crc.crcloud.steam.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crc.crcloud.steam.iam.entity.Application;
import com.crc.crcloud.steam.iam.model.dto.ApplicationSearchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ApplicationMapper extends BaseMapper<Application> {
    /**
     * 模糊查询
     *
     * @param applicationSearchDTO
     * @return
     */
    List<Application> fuzzyQuery(@Param("applicationSearchDTO") ApplicationSearchDTO applicationSearchDTO);

    /**
     * 传入application id集合，返回application 对象集合
     *
     * @param idSet
     * @return
     */
    List<Application> matchId(@Param("idSet") Set<Long> idSet);

    void updateApplicationName(@Param("applicationId") Long applicationId, @Param("applicationName") String applicationName);

    void updateApplicationEnabled(@Param("applicationId") Long applicationId, @Param("enabled") int enabled);
}
