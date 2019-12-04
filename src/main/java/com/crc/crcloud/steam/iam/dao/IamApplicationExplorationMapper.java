package com.crc.crcloud.steam.iam.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crc.crcloud.steam.iam.common.enums.ApplicationCategory;
import com.crc.crcloud.steam.iam.entity.IamApplicationExploration;
import org.apache.ibatis.annotations.Param;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public interface IamApplicationExplorationMapper extends BaseMapper<IamApplicationExploration> {


    /**
     * 根据应用id集合查询该节点所有后代，包含自己
     *
     * @param idSet
     * @return
     */
    List selectDescendantByApplicationIds(@Param("idSet") Set<Long> idSet);

    /**
     * 根据应用id集合删除该节点所有的，包含自己
     *
     * @param idSet
     */
    void deleteDescendantByApplicationIds(@Param("idSet") Set<Long> idSet);

    /**
     * 根据应用id查询该节点所有后代，包含自己
     *
     * @param path
     * @return
     */
    List selectDescendantByPath(@Param("path") String path);

    /**
     * 根据应用id查询该节点所有祖先，包含自己
     *
     * @param id
     * @return
     */
    List selectAncestorByApplicationId(@Param("id") Long id);

    /**
     * 根据应用id删除该节点所有的，包含自己
     *
     * @param id
     */
    void deleteDescendantByApplicationId(@Param("id") Long id);


    /**
     * 根据应用id和父id删除所有子节点，包含自己
     *
     * @param idSet
     * @param parentId
     */
    void deleteDescendantByApplicationIdsAndParentId(@Param("idSet") Set<Long> idSet, @Param("parentId") Long parentId);

    /**
     * 根据应用id查询该节点的下一层级的后代
     *
     * @param id
     * @return
     */
    List selectDirectDescendantByApplicationId(@Param("id") Long id);

    /**
     * 根据应用节点id集合和该节点的父id查询该节点下的所有节点，包含自己
     *
     * @param idSet
     * @param parentId
     */
    List selectDescendantByApplicationIdsAndParentId(@Param("idSet") HashSet<Long> idSet, @Param("parentId") Long parentId);

    /**
     * 根据应用节点id和该节点的父id查询该节点下的所有节点，包含自己
     *
     * @param id
     * @param parentId
     * @return
     */
    List selectDescendantByApplicationIdAndParentId(@Param("id") Long id, @Param("parentId") Long parentId);

    /**
     * 查询组合应用下指定类型的应用{@link ApplicationCategory}
     *
     * @param path
     * @param category
     * @param code
     * @param name
     * @return
     */
    List selectDescendantApplications(@Param("path") String path, @Param("category") String category,
                                      @Param("name") String name, @Param("code") String code);

    /**
     * 根据应用id查询子代，包含自己，带上应用和项目信息
     *
     * @param path "/"+id+"/"
     * @return
     */
    List<IamApplicationExploration> selectDescendants(@Param("path") String path);
}
