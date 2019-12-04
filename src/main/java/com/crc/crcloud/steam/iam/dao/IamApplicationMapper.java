package com.crc.crcloud.steam.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crc.crcloud.steam.iam.entity.IamApplication;
import com.crc.crcloud.steam.iam.model.dto.ApplicationSearchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface IamApplicationMapper extends BaseMapper<IamApplication> {
    /**
     * 模糊查询
     *
     * @param applicationSearchDTO
     * @return
     */
    List<IamApplication> fuzzyQuery(@Param("applicationSearchDTO") ApplicationSearchDTO applicationSearchDTO);

    /**
     * 传入application id集合，返回application 对象集合
     *
     * @param idSet
     * @return
     */
    List<IamApplication> matchId(@Param("idSet") Set<Long> idSet);

    void updateApplicationName(@Param("applicationId") Long applicationId, @Param("applicationName") String applicationName);

    void updateApplicationEnabled(@Param("applicationId") Long applicationId, @Param("enabled") int enabled);
}
