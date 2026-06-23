package com.factor.infrastructure.persistence;

import com.factor.domain.factor.FactorCategoryNode;
import com.factor.domain.factor.repository.FactorCategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryFactorCategoryRepository implements FactorCategoryRepository {

    private final List<FactorCategoryNode> storage = new ArrayList<>(List.of(
            new FactorCategoryNode("cat-1", null, "费率水平", 1, 1, "费率相关因子分类", true, List.of(
                    new FactorCategoryNode("cat-1-1", "cat-1", "管理费率", 2, 1, "管理费率", true, List.of()),
                    new FactorCategoryNode("cat-1-2", "cat-1", "运作费率", 2, 2, "运作费率", true, List.of()),
                    new FactorCategoryNode("cat-1-3", "cat-1", "托管费率", 2, 3, "托管费率", true, List.of())
            )),
            new FactorCategoryNode("cat-2", null, "规模与仓位", 1, 2, "规模和仓位因子分类", true, List.of(
                    new FactorCategoryNode("cat-2-1", "cat-2", "最新规模", 2, 1, "最新规模", true, List.of()),
                    new FactorCategoryNode("cat-2-2", "cat-2", "最新份额", 2, 2, "最新份额", true, List.of()),
                    new FactorCategoryNode("cat-2-3", "cat-2", "最新仓位", 2, 3, "最新仓位", true, List.of())
            ))
    ));

    @Override
    public List<FactorCategoryNode> findTree() {
        return List.copyOf(storage);
    }

    @Override
    public Optional<FactorCategoryNode> findById(String id) {
        return storage.stream().flatMap(node -> flatten(node).stream()).filter(node -> node.id().equals(id)).findFirst();
    }

    @Override
    public FactorCategoryNode save(FactorCategoryNode category) {
        FactorCategoryNode stored = category.id() == null ? new FactorCategoryNode(UUID.randomUUID().toString(), category.parentId(), category.name(), category.level(), category.sortNo(), category.description(), category.enabled(), category.children()) : category;
        storage.removeIf(item -> item.id().equals(stored.id()));
        storage.add(stored);
        return stored;
    }

    private List<FactorCategoryNode> flatten(FactorCategoryNode root) {
        List<FactorCategoryNode> result = new ArrayList<>();
        result.add(root);
        result.addAll(root.children());
        return result;
    }
}
