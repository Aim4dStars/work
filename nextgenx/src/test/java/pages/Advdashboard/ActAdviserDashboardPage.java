package pages.Advdashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@DefaultUrl("/secure/app/#ng/dashboard?c=FC622CC21BFFE81A42F70BB718A6A91D1D9470BB4FB2631D")
public class ActAdviserDashboardPage extends PageObject
{

	private WebDriver driver;
	public List <Map> arrayList = new ArrayList <Map>();

	@FindBy(css = "div[class='heading-four'] a[class='text-link']")
	private WebElement isDraftApplicationHeader;

	@FindBy(css = "div[class='mvc-draftapplications'] tr[class='default-row  '] strong a[class='text-link']")
	private List <WebElement> isDraftApplicationAccountName;

	@FindBy(css = "p[class='draftapplications']")
	private WebElement isResultCount;

	@FindBy(css = "div[class='mvc-draftapplications'] tr[class='default-row  '] p")
	private List <WebElement> isDraftApplicationAccountType;

	@FindBy(css = "div[class='mvc-draftapplications'] tr[class='default-row  '] td[class='push-left-1  '] div")
	private List <WebElement> isDraftApplicationDate;

	@FindBy(css = "div[class='mvc-draftapplications'] tr[class='no-results-row'] td[colspan='2']")
	private WebElement isAltScenarioDraftApplicationText;

	@FindBy(css = "div[class='mvc-draftapplications'] tr[class='no-results-row'] td[colspan='2']")
	private WebElement isAltScenarioDraftApplicationButton;

	@FindBy(css = "div:nth-child(1) > div.heading-four")
	private WebElement isSavedOrderHeader;

	@FindBy(css = "div[class='mvc-savedorders'] tr[class='default-row  '] strong a[class='text-link']")
	private List <WebElement> isSavedOrdersAccountName;

	@FindBy(css = "p[class='savedorders']")
	private WebElement isSavedOrderResultCount;

	@FindBy(css = "div[class='mvc-savedorders'] tr[class='default-row  '] p")
	private List <WebElement> isSavedOrdersDescriptionText;

	@FindBy(css = "div[class='mvc-savedorders'] tr[class='default-row  '] div")
	private List <WebElement> isSavedOrdersDate;

	@FindBy(css = "div[class='mvc-savedorders'] tr[class='no-results-row'] td[colspan='2']")
	private WebElement isAltScenarionSavedOrdersText;

	@FindBy(css = "div[class='view-btnsavedorders'] span[class='label-content']")
	private WebElement isAltScenarioSavedOrdersButton;

	@FindBy(css = "div[class='heading-four'] a[class='text-link  maturingtdheader']")
	private WebElement isMaturingTermsHeader;

	@FindBy(css = "div[class='mvc-maturingtermdeposits'] tr[class='default-row  '] strong a[class='text-link']")
	private List <WebElement> isMaturingTermsAccountName;

	@FindBy(css = "p[class='maturingtermdeposits']")
	private WebElement isMaturingTermssResultCount;

	@FindBy(css = "div[class='mvc-maturingtermdeposits'] tr[class='default-row  '] p")
	private List <WebElement> isMaturingTermsDescriptionText;

	@FindBy(css = "div[class='mvc-maturingtermdeposits'] tr[class='default-row  '] td :nth-child(2)")
	private List <WebElement> isMaturingTermsDaysLeft;

	@FindBy(css = "p[class='maturitydata']")
	private List <WebElement> isMaturingTermsDate;

	@FindBy(css = "div[class='view-btntdcalculator']")
	private WebElement isMaturingTDCalculator;

	@FindBy(css = "div[class='mvc-maturingtermdeposits'] tr[class='no-results-row'] td[colspan='2']")
	private WebElement isMaturingAltScenario1Text;

	@FindBy(css = "div[class='mvc-maturingtermdeposits'] tr[class='no-results-row'] td[colspan='2']")
	private WebElement isMaturingAltScenario2Text;

	@FindBy(css = "span[class='default-tooltip-trigger icon icon-support-help BTBlue '")
	private WebElement isKeyActivityHelp;

	@FindBy(css = "div[class='mvc-keyactivities'] tr[class='default-row  '] td[class='push-left-1  column-10  '] strong a[class='text-link']")
	private List <WebElement> isKeyActivityType;

	@FindBy(css = "div[class='mvc-keyactivities'] tr[class='default-row  '] td[class='push-left-1  column-6  '] strong a[class='text-link']")
	private List <WebElement> isKeyActivityAccountName;

	@FindBy(css = "div[class='mvc-keyactivities'] tr[class='default-row  '] div")
	private List <WebElement> isKeyActivityDescriptionText;

	@FindBy(css = "div[class='mvc-keyactivities'] tr[class='default-row  '] span[class='icon icon-notification-fail red ']")
	private List <WebElement> isKeyActivityPriorityIndicator;

	@FindBy(css = "div[class='mvc-keyactivities'] tr[class='default-row  '] div[class='deemphasis']")
	private List <WebElement> isKeyActivityDateAndTimeStamp;

	@FindBy(css = "span[class='margin-bottom-2 view-onboardtracking'] button[class=' btn- btn-action-tertiary'] span[class='label-content']")
	private WebElement isKeyActivityOnBoardingButton;

	@FindBy(css = "span[class='view-orderstatus'] a[class='btn-action-tertiary'] span[class='label-content']")
	private WebElement isKeyActivityOrderStatusButton;

	@FindBy(css = "heading[class='float-right']")
	private List <WebElement> isRightDateHeading;

	@FindBy(css = "div[class='mvc-keyactivities'] tr[class='no-results-row'] td[colspan='5']")
	private WebElement isAltScenarionKeyActivityText;

	@FindBy(css = "div[class='view-messagedisclaimer_2'] span[class='disclaimer-basic']")
	private WebElement isDisclaimersText;

	@FindBy(css = "div[data-component-name='sectionkeyactivities'] table[class='data-table-default'] tbody")
	private List <WebElement> tableRecKeyActivity;

	public WebElement getIsDraftApplicationHeader()
	{

		return isDraftApplicationHeader;

	}

	public List <WebElement> getIsDraftApplicationAccountName()
	{

		return isDraftApplicationAccountName;

	}

	public WebElement getIsResultCount()
	{

		return isResultCount;

	}

	public List <WebElement> getIsDraftApplicationAccountType()
	{

		return isDraftApplicationAccountType;

	}

	public List <WebElement> getIsDraftApplicationDate()
	{

		return isDraftApplicationDate;

	}

	public WebElement getIsAlternateScenarioDraftApplicationText()
	{

		return isAltScenarioDraftApplicationText;

	}

	public WebElement getIsAlternateScenarioDraftApplicationButton()
	{

		return isAltScenarioDraftApplicationButton;

	}

	public WebElement getIsSavedOrderHeader()
	{

		return isSavedOrderHeader;

	}

	public List <WebElement> getIsSavedOrdersAccountName()
	{

		return isSavedOrdersAccountName;

	}

	public WebElement getIsSavedOrderResultCount()
	{

		return isSavedOrderResultCount;

	}

	public List <WebElement> getIsSavedOrdersDescriptionText()
	{

		return isSavedOrdersDescriptionText;

	}

	public List <WebElement> getIsSavedOrdersDate()
	{

		return isSavedOrdersDate;

	}

	public WebElement getIsAlternateScenarioSavedOrdersText()
	{

		return isAltScenarionSavedOrdersText;

	}

	public WebElement getIsAlternateScenarioSavedOrdersButton()
	{

		return isAltScenarioSavedOrdersButton;

	}

	public WebElement getIsMaturingTermsHeader()
	{

		return isMaturingTermsHeader;

	}

	public List <WebElement> getIsMaturingTermsAccountName()
	{

		return isMaturingTermsAccountName;

	}

	public WebElement getIsMaturingTermssResultCount()
	{

		return isMaturingTermssResultCount;

	}

	public List <WebElement> getIsMaturingTermsDescriptionText()
	{

		return isMaturingTermsDescriptionText;

	}

	public List <WebElement> getIsMaturingTermsDaysLeft()
	{

		return isMaturingTermsDaysLeft;

	}

	public List <WebElement> getIsMaturingTermsDate()
	{

		return isMaturingTermsDate;

	}

	public WebElement getIsMaturingTDCalculator()
	{

		return isMaturingTDCalculator;

	}

	public WebElement getIsMaturingAltScenario1Text()
	{

		return isMaturingAltScenario1Text;

	}

	public WebElement getIsMaturingAltScenario2Text()
	{

		return isMaturingAltScenario2Text;

	}

	public WebElement getIsKeyActivityHelp()
	{

		return isKeyActivityHelp;

	}

	public List <WebElement> getIsKeyActivityType()
	{

		return isKeyActivityType;

	}

	public List <WebElement> getIsKeyActivityAccountName()
	{

		return isKeyActivityAccountName;

	}

	public List <WebElement> getIsKeyActivityDescriptionText()
	{

		return isKeyActivityDescriptionText;

	}

	public List <WebElement> getIsKeyActivityPriorityIndicator()
	{

		return isKeyActivityPriorityIndicator;

	}

	public List <WebElement> getIsKeyActivityDateAndTimeStamp()
	{

		return isKeyActivityDateAndTimeStamp;

	}

	public WebElement getIsKeyActivityOnBoardingButton()
	{

		return isKeyActivityOnBoardingButton;

	}

	public WebElement getIsKeyActivityOrderStatusButton()
	{

		return isKeyActivityOrderStatusButton;

	}

	public List <WebElement> getIsRightDateHeading()
	{

		return isRightDateHeading;

	}

	public WebElement getIsAltScenarionKeyActivityText()
	{

		return isAltScenarionKeyActivityText;

	}

	public WebElement getisDisclaimersText()
	{

		return isDisclaimersText;

	}

	public List <WebElement> getTableRecKeyActivity()
	{
		return tableRecKeyActivity;
	}

	public ActAdviserDashboardPage(WebDriver driver)
	{
		super(driver);
	}

	public List <Map> tableContTest(List <String> headerName, List <WebElement> tableProp) throws Throwable
	{

		for (WebElement tbody_collection : tableProp)
		{

			List <WebElement> tr_collection = tbody_collection.findElements(By.tagName("tr"));

			int row_num, col_num;
			row_num = 1;
			int no_of_rec = headerName.size();

			for (WebElement trElement : tr_collection)
			{
				java.util.List <WebElement> td_collection = trElement.findElements(By.xpath("td"));
				Map <String, String> actual = new HashMap <String, String>();

				col_num = 1;

				if (td_collection.size() > 1)
				{

					for (WebElement tdElement : td_collection)
					{

						if (no_of_rec == 4)
						{
							switch (col_num)
							{
								case 1:
									actual.put(headerName.get(0), tdElement.getText());
								case 2:
									actual.put(headerName.get(1), tdElement.getText());
									break;
								case 3:
									actual.put(headerName.get(2), tdElement.getText());
									break;
								case 4:
									actual.put(headerName.get(3), tdElement.getText());
									break;

							}
						}
						else if (no_of_rec == 5)
						{
							switch (col_num)
							{
								case 1:
									actual.put(headerName.get(0), tdElement.getText());
									break;
								case 2:
									actual.put(headerName.get(1), tdElement.getText());
									break;
								case 3:
									actual.put(headerName.get(2), tdElement.getText());
									break;
								case 4:
									actual.put(headerName.get(3), tdElement.getText());
									break;
								case 5:
									actual.put(headerName.get(4), tdElement.getText());
									break;

							}
						}
						else if (no_of_rec == 3)
						{
							switch (col_num)
							{
								case 1:
									actual.put(headerName.get(0), tdElement.getText());
									break;
								case 2:
									actual.put(headerName.get(1), tdElement.getText());
									break;
								case 3:
									actual.put(headerName.get(2), tdElement.getText());
									break;
							}
						}

						col_num++;

					}

					arrayList.add(actual);
				}

				row_num++;

			}

		}
		return arrayList;

	}

}
