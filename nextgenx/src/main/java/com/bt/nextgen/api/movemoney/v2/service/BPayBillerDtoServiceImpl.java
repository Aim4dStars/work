package com.bt.nextgen.api.movemoney.v2.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.movemoney.v2.model.BpayBillerDto;
import com.bt.nextgen.payments.repository.BpayBiller;
import com.bt.nextgen.payments.repository.BpayBillerCodeRepository;
import com.bt.nextgen.service.ServiceErrors;

@Service("BPAYBillerDtoServiceV2")
public class BPayBillerDtoServiceImpl implements BPayBillerDtoService {

    @Autowired
    private BpayBillerCodeRepository bpayBillerCodeRepository;

    @Override
    public List<BpayBillerDto> findAll(ServiceErrors serviceErrors) {
        return loadBillers(bpayBillerCodeRepository);
    }

    private List<BpayBillerDto> loadBillers(BpayBillerCodeRepository bpayBillerCodeRepository) {
        Collection<BpayBiller> billers = bpayBillerCodeRepository.loadAllBillers();
        List<BpayBillerDto> billerDtos = new ArrayList<>();
        for (BpayBiller biller : billers) {
            billerDtos.add(new BpayBillerDto(biller.getBillerCode(), biller.getBillerName()));
        }
        return billerDtos;
    }

    @Override
    public BpayBillerDto validate(BpayBillerDto billerCode, ServiceErrors serviceErrors) {
        BpayBiller response = bpayBillerCodeRepository.load(billerCode.getKey().getKey());
        BpayBillerDto dto = new BpayBillerDto();
        dto.setKey(billerCode.getKey());
        if (response != null) {
            dto.setBillerName(response.getBillerName().trim());
            dto.setBillerCode(response.getBillerCode());
        }
        return dto;
    }
}
