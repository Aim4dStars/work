package com.bt.nextgen.service.prm.pojo;

/**
 * Created by l081361 on 5/07/2016.
 */
public enum PrmEventType {



    DISABLED("DISABLED","Non-Value SMS 2FA Disable"), // DESCOPED
    ACTIVE("ACTIVE","Non-Value SMS 2FA Active"),
    REGISTRATION("REGISTRATION","Non-Value Registration"),
    MOBILECHANGE("MOBILECHANGE","Non-value 2FA Mobile Number Change"),
    PWDCHANGE("PWDCHANGE","Non-Value Password Change"),
    TMPPWDISSUE("TMPPWDISSUE","Non-Value Issue Temp Password"),
    TMPPWDCHANGE("TMPPWDCHANGE","Non-Value Temp Password Change"),// To be Handled by EAM
    ACCESSBLOCK("ACCESSBLOCK","Non-Value Operator Locked Customer"),
    ACCESSUNBLOCK("ACCESSUNBLOCK","Non-Value Customer Sign In Unlocked"),
    ADDLINKED("ADDLINKED","Non-Value Add Linked Account"),
    ADDACCOUNT("ADDACCOUNT","Non-Value Add Payee"),
    ADDBPAY("ADDBPAY","Non-Value Add Payee"),
    UPDATELINKED("UPDATELINKED","Non-Value Update Linked Account"),
    UPDATEACCOUNT("UPDATEACCOUNT","Non-Value Update Payee"),
    UPDATEBPAY("UPDATEBPAY","Non-Value Update Payee"),
    DELETELINKED("DELETELINKED","Non-Value Delete Linked Account"),
    DELETEACCOUNT("DELETEACCOUNT","Non-Value Delete Payee"),
    DELETEBPAY("DELETEBPAY","Non-Value Delete Payee"),
    INCLIMIT("INCLIMIT","Non-Value Increase Daily Limit"),
    PRMLOGOUT("PRMLOGOUT","Non-Value Log Off"),
    SIGNIN("SIGNIN","Non-Value Sign In"),
    FORGOTPASSWORD("FORGOTPASSWORD","Non-Value Forgotten Password" )
    ;

    private final String eventTypeValue;
    private final String eventTypeText;

    public String getEventTypeValue() {
        return eventTypeValue;
    }

    public String getEventTypetext() {
        return eventTypeText;
    }

    PrmEventType(String eventTypeValue, String eventTypeText) {
        this.eventTypeValue = eventTypeValue;
        this.eventTypeText = eventTypeText;
    }
}


