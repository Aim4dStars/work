/**
 * 
 */
package com.bt.nextgen.core.repository;

/**
 * @author l081050
 *
 */
public class OnboardingPartyDto {
    private int partyId;
    private Long onBaordingId;
    private String status;
    private String gcmPan;
    public String getGcmPan() {
        return gcmPan;
    }
    public void setGcmPan(String gcmPan) {
        this.gcmPan = gcmPan;
    }
    public int getPartyId() {
        return partyId;
    }
    public void setPartyId(int partyId) {
        this.partyId = partyId;
    }
    public Long getOnBaordingId() {
        return onBaordingId;
    }
    public void setOnBaordingId(Long onBaordingId) {
        this.onBaordingId = onBaordingId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
  

}
