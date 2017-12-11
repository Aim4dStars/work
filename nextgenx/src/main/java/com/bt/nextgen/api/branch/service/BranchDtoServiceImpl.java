package com.bt.nextgen.api.branch.service;

import com.bt.nextgen.api.branch.model.BranchDto;
import com.bt.nextgen.api.branch.model.BranchKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.payments.repository.Bsb;
import com.bt.nextgen.payments.repository.BsbCodeRepository;
import com.bt.nextgen.service.ServiceErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;

@Service
public class BranchDtoServiceImpl implements BranchDtoService {

    @Autowired
    private BsbCodeRepository bsbCodeRepository;

    @Override
    public BranchDto find(BranchKey key, ServiceErrors serviceErrors) {
        Bsb bsb = bsbCodeRepository.load(key.getBsb());
        return bsb == null ? null : new BranchDto(new BranchKey(bsb.getBsbCode()), bsb.getBankName());
    }
}
