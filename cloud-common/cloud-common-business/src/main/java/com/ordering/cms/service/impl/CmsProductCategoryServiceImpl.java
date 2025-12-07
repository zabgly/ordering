package com.ordering.cms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ordering.cms.domain.CmsProductCategory;
import com.ordering.cms.mapper.CmsProductCategoryMapper;
import com.ordering.cms.service.ICmsProductCategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜品分类Service业务层处理
 * 
 * @author ordering
 * @date 2025-03-15
 */
@Service
public class CmsProductCategoryServiceImpl extends ServiceImpl<CmsProductCategoryMapper, CmsProductCategory> implements ICmsProductCategoryService {

    /**
     * 根据 parentId 构建树结构
     */
    public List<CmsProductCategory> buildTree(List<CmsProductCategory> categories) {
        // 使用 Map 存储所有节点，方便根据 id 快速查找
        Map<String, CmsProductCategory> categoryMap = new HashMap<>();
        for (CmsProductCategory category : categories) {
            categoryMap.put(category.getId(), category);
        }
        // 构建树结构
        List<CmsProductCategory> tree = new ArrayList<>();
        for (CmsProductCategory category : categories) {
            String parentId = category.getParentId();
            if (parentId == null || parentId.isEmpty()) {
                // 如果 parentId 为空，说明是根节点
                tree.add(category);
            } else {
                // 找到父节点，并将当前节点添加到父节点的子节点列表中
                CmsProductCategory parent = categoryMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(category);
                }
            }
        }
        return tree;
    }
    /**
     * 获取树结构中的所有叶子节点
     */
    public List<CmsProductCategory> getLeafNodes(List<CmsProductCategory> tree) {
        List<CmsProductCategory> leafNodes = new ArrayList<>();
        findLeafNodes(tree, leafNodes);
        return leafNodes;
    }
    /**
     * 递归查找叶子节点
     */
    public void findLeafNodes(List<CmsProductCategory> categories, List<CmsProductCategory> leafNodes) {
        for (CmsProductCategory category : categories) {
            if (category.getChildren() == null || category.getChildren().isEmpty()) {
                // 如果没有子节点，说明是叶子节点
                leafNodes.add(category);
            } else {
                // 递归查找子节点的叶子节点
                findLeafNodes(category.getChildren(), leafNodes);
            }
        }
    }
    /**
     * 主方法：获取所有叶子节点
     */
    public List<CmsProductCategory> getAllLeafNodes(List<CmsProductCategory> categories) {
        // 构建树结构
        List<CmsProductCategory> tree = buildTree(categories);
        // 获取叶子节点
        return getLeafNodes(tree);
    }

}
