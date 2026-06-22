package com.factor.application.factor;

import com.factor.common.model.PageResult;
import com.factor.domain.factor.Factor;
import com.factor.domain.factor.FactorCategory;
import com.factor.domain.factor.repository.FactorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FactorApplicationServiceImpl implements FactorApplicationService {

    private final FactorRepository factorRepository;

    public FactorApplicationServiceImpl(FactorRepository factorRepository) {
        this.factorRepository = factorRepository;
    }

    @Override
    public PageResult<Factor> listFactors(FactorCategory category, long page, long size) {
        List<Factor> factors = category == null ? factorRepository.findAll() : factorRepository.findByCategory(category);
        return new PageResult<>(factors, page, size, factors.size());
    }

    @Override
    public Optional<Factor> getFactor(String id) {
        return factorRepository.findById(id);
    }
}
