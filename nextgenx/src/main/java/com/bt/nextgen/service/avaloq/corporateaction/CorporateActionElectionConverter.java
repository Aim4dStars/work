package com.bt.nextgen.service.avaloq.corporateaction;

import com.avaloq.abs.bb.fld_def.TextFld;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionValidationError;
import com.btfin.abs.err.v1_0.Err;
import com.btfin.abs.trxservice.secevt2applydecsn.v1_0.Data;
import com.btfin.abs.trxservice.secevt2applydecsn.v1_0.Decsn;
import com.btfin.abs.trxservice.secevt2applydecsn.v1_0.Pos;
import com.btfin.abs.trxservice.secevt2applydecsn.v1_0.PosList;
import com.btfin.abs.trxservice.secevt2applydecsn.v1_0.Secevt2ApplyDecsnReq;
import com.btfin.abs.trxservice.secevt2applydecsn.v1_0.Secevt2ApplyDecsnRsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bt.nextgen.service.AvaloqGatewayUtil.createTextVal;

@Service
public class CorporateActionElectionConverter {
	private static final Logger logger = LoggerFactory.getLogger(CorporateActionElectionConverter.class);
	private static final Pattern patternPositionId = Pattern.compile("\\[([0-9]*)\\]$");

	public Secevt2ApplyDecsnReq toSaveElectionGroupRequest(CorporateActionElectionGroup electionGroup) {
		Secevt2ApplyDecsnReq req = AvaloqObjectFactory.getCorporateActionApplyElectionObjectFactory().createSecevt2ApplyDecsnReq();
		req.setHdr(AvaloqGatewayUtil.createHdr());

		Data data = AvaloqObjectFactory.getCorporateActionApplyElectionObjectFactory().createData();
		data.setDocId(AvaloqGatewayUtil.createNumberVal(electionGroup.getOrderNumber()));
		req.setData(data);

		PosList posList = AvaloqObjectFactory.getCorporateActionApplyElectionObjectFactory().createPosList();
		data.setPosList(posList);

		// Add all applicable position ID's
		for (CorporateActionPosition position : electionGroup.getPositions()) {
			Pos pos = new Pos();
			pos.setPosId(AvaloqGatewayUtil.createIdVal(position.getId().toString()));
			posList.getPos().add(pos);
		}

		// Add election decision
		Decsn decsn = new Decsn();
		setDecisions(decsn, electionGroup);

		data.setDecsn(decsn);

		return req;
	}

	private void setDecisions(Decsn decsn, CorporateActionElectionGroup electionGroup) {
		try {
			for (int i = 1; i <= electionGroup.getOptions().size(); i++) {
				CorporateActionOption option = electionGroup.getOptions().get(i - 1);

				// Use reflection since Avaloq object data structure is terrible
				final Method setKey = Decsn.class.getDeclaredMethod("setKey" + i, TextFld.class);
				final Method setVal = Decsn.class.getDeclaredMethod("setVal" + i, TextFld.class);

				setKey.invoke(decsn, createTextVal(option.getKey()));
				setVal.invoke(decsn, createTextVal(option.getValue()));
			}
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			logger.error("Unable to invoke method to set corporate action decision", e);
		}
	}

	public CorporateActionElectionGroup toSaveElectionResponse(Secevt2ApplyDecsnRsp response) {
		CorporateActionElectionGroupImpl electionGroup = new CorporateActionElectionGroupImpl();

		electionGroup.setOrderNumber(response.getData().getDocId().getVal().toString());

		// Pick up election errors
		if (response.getRsp() != null && response.getRsp().getValid() != null && response.getRsp().getValid().getErrList() != null &&
				!response.getRsp().getValid().getErrList().getErr().isEmpty()) {
			List<CorporateActionValidationError> errors = new ArrayList<>();

			for (Err error : response.getRsp().getValid().getErrList().getErr()) {
				errors.add(new CorporateActionValidationError(parsePositionId(error), error.getExtlKey(), error.getErrMsg()));
			}

			electionGroup.setElectionErrors(errors);
		}

		return electionGroup;
	}

	public CorporateActionElectionGroup toSaveElectionResponseForIm(Secevt2ApplyDecsnRsp response) {
		CorporateActionElectionGroupImpl electionGroup = new CorporateActionElectionGroupImpl();

		electionGroup.setOrderNumber(response.getData().getDocId().getVal().toString());

		// TODO: Verify that the error nodes are valid.  If they are the same, use toSaveElectionResponse() instead
		// Pick up election errors
		if (response.getRsp() != null && response.getRsp().getValid() != null && response.getRsp().getValid().getErrList() != null &&
				!response.getRsp().getValid().getErrList().getErr().isEmpty()) {
			List<CorporateActionValidationError> errors = new ArrayList<>();

			for (Err error : response.getRsp().getValid().getErrList().getErr()) {
				errors.add(new CorporateActionValidationError(parsePositionId(error), error.getExtlKey(), error.getErrMsg()));
			}

			electionGroup.setElectionErrors(errors);
		}

		return electionGroup;
	}

	private String parsePositionId(Err error) {
		Matcher matcher = patternPositionId.matcher(error.getLocList().getLoc().iterator().next());

		return matcher.find() ? matcher.group(1) : "";
	}
}
