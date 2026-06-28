package com.factor.infrastructure.persistence.jpa;

import com.factor.domain.factor.FactorCategoryNode;
import com.factor.domain.factor.repository.FactorCategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

import org.springframework.context.annotation.Profile;
@Profile("jpa")
@Repository
public class JpaFactorCategoryRepository implements FactorCategoryRepository {

    private final FactorCategoryJpaRepository jpa;

    public JpaFactorCategoryRepository(FactorCategoryJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<FactorCategoryNode> findTree() {
        List<FactorCategoryEntity> all = jpa.findAll();
        // 构建 parentId → children 映射
        Map<String, List<FactorCategoryEntity>> childMap = new HashMap<>();
        List<FactorCategoryEntity> roots = new ArrayList<>();
        for (FactorCategoryEntity e : all) {
            if (e.getParentId() == null) {
                roots.add(e);
            } else {
                childMap.computeIfAbsent(e.getParentId(), k -> new ArrayList<>()).add(e);
            }
        }
        // 递归构建树
        return roots.stream().map(e -> buildNode(e, childMap)).toList();
    }

    private FactorCategoryNode buildNode(FactorCategoryEntity entity,
                                          Map<String, List<FactorCategoryEntity>> childMap) {
        List<FactorCategoryEntity> kids = childMap.getOrDefault(entity.getId(), List.of());
        List<FactorCategoryNode> children = kids.stream()
                .map(k -> buildNode(k, childMap))
                .toList();
        return toDomain(entity, children);
    }

    @Override
    public Optional<FactorCategoryNode> findById(String id) {
        return jpa.findById(id).map(e -> toDomain(e, findChildren(e.getId())));
    }

    private List<FactorCategoryNode> findChildren(String parentId) {
        return jpa.findByParentId(parentId).stream()
                .map(e -> toDomain(e, findChildren(e.getId())))
                .toList();
    }

    @Override
    public FactorCategoryNode save(FactorCategoryNode category) {
        FactorCategoryEntity entity = toEntity(category);
        if (entity.getId() == null) entity.setId(UUID.randomUUID().toString());
        jpa.save(entity);
        return toDomain(entity, List.of());
    }

    private FactorCategoryNode toDomain(FactorCategoryEntity e, List<FactorCategoryNode> children) {
        return new FactorCategoryNode(
                e.getId(), e.getParentId(), e.getName(), e.getCatLevel(),
                e.getSortNo(), e.getDescription(), e.isEnabled(), children
        );
    }

    private FactorCategoryEntity toEntity(FactorCategoryNode n) {
        FactorCategoryEntity e = new FactorCategoryEntity();
        e.setId(n.id()); e.setParentId(n.parentId()); e.setName(n.name());
        e.setCatLevel(n.level()); e.setSortNo(n.sortNo());
        e.setDescription(n.description()); e.setEnabled(n.enabled());
        return e;
    }
}
