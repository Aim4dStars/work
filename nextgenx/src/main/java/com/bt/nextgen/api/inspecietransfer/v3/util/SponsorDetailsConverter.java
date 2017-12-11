package com.bt.nextgen.api.inspecietransfer.v3.util;

import com.bt.nextgen.api.inspecietransfer.v3.model.SponsorDetailsDto;
import com.bt.nextgen.service.avaloq.transfer.transfergroup.SponsorDetailsImpl;
import com.bt.nextgen.service.integration.transfer.SponsorDetails;
import com.bt.nextgen.service.integration.transfer.TransferType;

public final class SponsorDetailsConverter {

    private SponsorDetailsConverter() {
        // hide public constructor
    }

    public static SponsorDetails fromDto(SponsorDetailsDto sponsorDetailsDto, String transferType) {
        SponsorDetailsImpl sponsorDetails = new SponsorDetailsImpl();
        switch (TransferType.forDisplay(transferType)) {
            case LS_BROKER_SPONSORED:
                sponsorDetails.setSponsorId(sponsorDetailsDto.getPid());
                sponsorDetails.setSponsorName(sponsorDetailsDto.getPidName());
                sponsorDetails.setInvestmentId(sponsorDetailsDto.getHin());
                break;
            case LS_OTHER:
                sponsorDetails.setSponsorId(sponsorDetailsDto.getPid());
                sponsorDetails.setSponsorName(sponsorDetailsDto.getPidName());
                sponsorDetails.setInvestmentId(sponsorDetailsDto.getHin());
                sponsorDetails.setPlatformId(sponsorDetailsDto.getCustodian());
                break;
            case LS_ISSUER_SPONSORED:
                sponsorDetails.setInvestmentId(sponsorDetailsDto.getSrn());
                break;
            case OTHER_PLATFORM:
                setOtherPlatformSponsorDetails(sponsorDetailsDto, sponsorDetails);
                break;
            case MANAGED_FUND:
                sponsorDetails.setPlatformId(sponsorDetailsDto.getCustodian());
                sponsorDetails.setInvestmentId(sponsorDetailsDto.getAccNumber());
                break;
            default:
                break;
        }
        return sponsorDetails;
    }

    public static SponsorDetailsDto toDto(SponsorDetails sponsorDetails, TransferType transferType) {
        if (sponsorDetails == null || transferType == null) {
            return null;
        }

        SponsorDetailsDto sponsorDetailsDto = null;
        switch (transferType) {
            case LS_BROKER_SPONSORED:
                sponsorDetailsDto = new SponsorDetailsDto(sponsorDetails.getSponsorId(), sponsorDetails.getSponsorName(),
                        sponsorDetails.getInvestmentId());
                break;
            case LS_OTHER:
                sponsorDetailsDto = new SponsorDetailsDto(sponsorDetails.getSponsorId(), sponsorDetails.getSponsorName(),
                        sponsorDetails.getInvestmentId(), sponsorDetails.getPlatformId());
                break;
            case LS_ISSUER_SPONSORED:
                sponsorDetailsDto = new SponsorDetailsDto(sponsorDetails.getInvestmentId());
                break;
            case OTHER_PLATFORM:
                sponsorDetailsDto = getOtherPlatformSponsorDetails(sponsorDetails);
                break;
            case MANAGED_FUND:
                sponsorDetailsDto = new SponsorDetailsDto(sponsorDetails.getPlatformId(), sponsorDetails.getInvestmentId());
                break;
            default:
                break;
        }
        return sponsorDetailsDto;
    }

    private static void setOtherPlatformSponsorDetails(SponsorDetailsDto sponsorDetailsDto, SponsorDetailsImpl sponsorDetails) {
        if (sponsorDetailsDto.getHin() != null) {
            sponsorDetails.setSponsorId(sponsorDetailsDto.getPid());
            sponsorDetails.setSponsorName(sponsorDetailsDto.getPidName());
            sponsorDetails.setInvestmentId(sponsorDetailsDto.getHin());
            sponsorDetails.setPlatformId(sponsorDetailsDto.getCustodian());
        } else if (sponsorDetailsDto.getAccNumber() != null) {
            sponsorDetails.setPlatformId(sponsorDetailsDto.getCustodian());
            sponsorDetails.setInvestmentId(sponsorDetailsDto.getAccNumber());
        }
    }

    private static SponsorDetailsDto getOtherPlatformSponsorDetails(SponsorDetails sponsorDetails) {
        if (sponsorDetails.getSponsorId() != null && !sponsorDetails.getSponsorId().isEmpty()) {
            return new SponsorDetailsDto(sponsorDetails.getSponsorId(),
                    sponsorDetails.getSponsorName(),
                    sponsorDetails.getInvestmentId(), sponsorDetails.getPlatformId());
        } else {
            return new SponsorDetailsDto(sponsorDetails.getPlatformId(), sponsorDetails.getInvestmentId());
        }
    }
}
