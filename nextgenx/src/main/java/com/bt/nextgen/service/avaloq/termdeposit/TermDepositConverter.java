package com.bt.nextgen.service.avaloq.termdeposit;

import com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_pos_fidd.Bp;
import com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_pos_fidd.BpHead;
import com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_pos_fidd.Pos;
import com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_pos_fidd.PosHead;
import com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_pos_fidd.Rep;
import com.bt.nextgen.core.mapping.AbstractMappingConverter;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.btfin.panorama.core.mapping.Mapper;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.account.Account;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TermDepositConverter extends AbstractMappingConverter
{
	@Autowired
	@Qualifier("avaloqAccountIntegrationService")
	private AccountIntegrationService accountIntegrationService;

	public List <TermDepositImpl> toModel(Rep report, ServiceErrors serviceErrors)
	{
		//AccountIntegrationService accountIntegrationService = new AvaloqAccountIntegrationServiceImpl();
		List <TermDepositImpl> termDeposits = new ArrayList <>();
		Mapper mapper = getMapper();

		if (report.getData().getReport().getBpList()!=null){
            List <Bp> bpList = report.getData().getReport().getBpList().getBp();
            for (Bp bp : bpList)
            {
                String accountName = null;
                String accountId = null;

                BpHead bpHead = bp.getBpHeadList().getBpHead().get(0);
                if (bpHead.getAccNr() != null)
                {
                    Account account = accountIntegrationService.loadWrapAccountWithoutContainers(
                            AccountKey.valueOf(AvaloqGatewayUtil.asString(bpHead.getAccId())), serviceErrors);
                    if (account != null)
                    {
                        accountId = AvaloqGatewayUtil.asString(bpHead.getAccId());
                        accountName = account.getAccountName();
                    }
                }
                List <Pos> posList = bp.getPosList().getPos();
                for (Pos pos : posList)
                {
                    PosHead posHead = pos.getPosHeadList().getPosHead().get(0);
                    TermDepositImpl termDepositImpl = mapper.map(posHead, TermDepositImpl.class, serviceErrors);
                    termDepositImpl.setAccountName(accountName);
                    termDepositImpl.setAccountId(accountId);
                    termDeposits.add(termDepositImpl);
                }
            }
        }

		return termDeposits;
	}
}
