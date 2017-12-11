package com.bt.nextgen.config;

import com.bt.nextgen.service.avaloq.userinformation.UserExperience;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(
{
	METHOD, TYPE
})
@Retention(RUNTIME)
public @interface SecureTestContext

{
	/***********************************
	 * Methods to return default information usually provided by the SAML token
	 *
	 *
	 **************************************/
	/**
	 * Value to substitute into the SAML token as the users username
	 * @return
	 */
	String username() default "adviser";

	/**
	 * Value substitued into the SAML token as the customer Id
	 * @return
	 */
	String customerId() default "";

	/**
	 * Site roles which the user has been granted
	 * @return
	 */
	String[] authorities() default {"ROLE_ADVISER"};

	/***********************************
	 * Methods to load specific Job Profile information
	 **************************************/
	/**
	 * The profile ID of the user (this is the sec user id)
	 * NOTE if the jobId and jobRole are not specified this will be the preferred profile ID and the code may fail if
	 * a jobProfile without this profile id does not come back from avaloq
	 * @return
	 */
	String profileId() default "";


	/**
	 * Methods needed to specify a complete Job Profile
	 */
	/**
	 * The Role of the user, should be the name of a JobRole
	 * Must be provided with a jobId and profileId otherwise it will be ignored
	 * @return
	 */
	String jobRole() default "";
	/**
	 * The Job Id which will be a job key in the oe hierarchy
	 * Must be provided with a valid jobRole and profileId otherwise it will be ignored
	 * @return
	 */
	String jobId() default "";

	/**
	 * The user experience attached to the JobProfile.
	 * Must be provided with a jobId and profileId otherwise it will be ignored
	 * @return
	 */
	UserExperience userExperience() default UserExperience.ADVISED;

	/**
	 * The Profile Id of the User who is being emulated
	 * @return
	 */
	String emulatedProfileId() default "";

	/**
	 * the password of the user, this should not be necessary.
	 * @return
	 */
	String password() default "";

}