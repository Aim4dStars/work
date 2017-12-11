package com.bt.nextgen.core.service;

public class ErrorConstants 
{
	//Faults for username
	
	/**Status Code: 309,00012 : User Alias is already registered for an existing user. */
	public static final String ALIAS_IN_USE_FAULT = "309,00012";

    /**Status Code: 309,00012 : User Alias is already registered for an existing user. */
    public static final String ALIAS_IN_USE = "aliasInUseFault";

	//Common Faults for username and password
	
	/**Status Code: 300,00000 : General error from provider system. */
	public static final String UNWILLING_TO_PERFORM_FAULT = "unwillingToPerformFault";
	
	/**Status Code: 300,00000 : General error from provider system. */
	public static final String EAM_SERVICES_FAULT = "eamServicesFault";
	
	/**Status Code: 300,00010 : No Credential found. */
	public static final String INVALID_ACCOUNT_FAULT = "invalidAccountFault";
	
	/**Status Code: 300,99998 : Unexpected error from provider system. */
	public static final String SCHEMA_VALIDATION_FAULT = "schemaValidationFault";
	
	//Faults for password
	
	/**Status Code: 309,00006 : The supplied password does not meet the password policy requirements. */
	public static final String PWD_POLICY_VALIDATION_FAULT = "pwdPolicyValidationFault";
	
	/**Status Code: 309,00009 : Invalid username/password. */
	public static final String AUTHENTICATE_USER_FAULT = "authenticateUserFault";
	
	/**Status Code: 309,00047 : The password is not allowed to match user alias. */
	public static final String PASSWORD_MATCHES_ALIAS_FAULT = "passwordMatchesAliasFault";
	
	/**Status Code: 300,00000 : General error from provider system. */
	public static final String INPUT_VALIDATION_FAULT = "inputValidationFault";
	
	/**Status Code: 309,00048 : Invalid key map. */
	public static final String KEY_MAP_EXPIRED_FAULT = "keyMapExpiredFault";
	
	/**Status Code: 309,00049 : The password does not meet the minimum length policy requirement. */
	public static final String PWD_POLICY_MIN_LENGTH_FAULT = "pwdPolicyMinLengthFault";
	
	/**Status Code: 309,00050 : The password does not meet the minimum number of alphabetic characters policy requirement. */
	public static final String PWD_POLICY_MIN_ALPHA_CHARS_FAULT = "pwdPolicyMinAlphaCharsFault";
	
	/**Status Code: 309,00051 : The password does not meet the minimum number of non-alphabetic characters policy requirement. */
	public static final String PWD_POLICY_MIN_OTHER_CHARS_FAULT = "pwdPolicyMinOtherCharsFault";
	
	/**Status Code: 309,00052 : The password does not meet the maximum allowed consecutive repeated characters policy requirement. */
	public static final String PWD_POLICY_MAX_CONSECUTIVE_REPEATED_CHARS_FAULT = "pwdPolicyMaxConsecutiveRepeatedCharsFault";
	
	/**Status Code: 309,00053 : The password does not meet the reuse of previous passwords policy requirement. */
	public static final String PWD_POLICY_IN_HISTORY_FAULT = "pwdPolicyInHistoryFault";
	
	/**Status Code: 306,00025 : The user is in an invalid state to perform this  operation. */
	public static final String INVALID_STATE_FAULT = "invalidStateFault";


}
