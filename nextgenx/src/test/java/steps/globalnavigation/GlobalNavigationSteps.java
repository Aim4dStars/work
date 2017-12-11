package steps.globalnavigation;

import pages.nav.TopNavPage;

public class GlobalNavigationSteps
{

	public void do_logout()
	{
		onTopNavPage().do_logout();
	}

	public void select_accounts()
	{
		onTopNavPage().select_accounts();
	}

	public void select_clients()
	{
		onTopNavPage().select_clients();
	}

	private TopNavPage onTopNavPage()
	{
		//		return getPages().get(TopNavPage.class);
		return null;
	}

}