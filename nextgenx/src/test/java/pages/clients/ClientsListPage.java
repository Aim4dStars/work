package pages.clients;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

@DefaultUrl("/secure/app/#ng/clientlist")
public class ClientsListPage extends PageObject
{
	public List <Map> arrayList = new ArrayList <Map>();
	int i = 0;

	@FindBy(name = "clientAccountName")
	private WebElement isClientSearchBox;

	@FindBy(xpath = "//button")
	private WebElement isSearchBtn;

	@FindBy(xpath = "/html/body/div/div/div/div[2]/article/div/div[2]/div/div/div[3]/div/table")
	private WebElement isRecords;

	@FindBy(how = How.CSS, using = "#table#")
	private WebElement isRecord;

	@FindBy(css = "div[class='mvc-clientlisttable']>table")
	private WebElement tablerows;

	@FindBy(css = "div[class='mvc-accountlisttable']>table tr")
	private WebElement tableRowsInsideRow;

	@FindBy(how = How.CSS, using = "#table#")
	private WebElement tablerecord;

	@FindBy(how = How.CSS, using = "a.btn-more-results > span.label")
	private WebElement showMore;

	@FindBy(css = "[class='filter-pagination-label']")
	private WebElement isRowCount;

	@FindBy(css = "div[class='view-forminputtext'] input[class='text-input']")
	private WebElement isClientNameSearchBox;

	@FindBy(css = "tr[class='table-column-headers']")
	private WebElement isClientListHeaders;

	@FindBy(css = "span[class='icon icon-arrow-expand']")
	private List <WebElement> isClientListExpandToggleIcon;

	@FindBy(css = "div[class='mvc-accountlisttable_2'] table[class='data-table-default'] tbody tr td a[class='text-link']")
	private WebElement isClientListExpandedClientAccount1;

	@FindBy(css = "span[class='icon icon-view-actions']")
	private WebElement isClientListActionIconAccountRowExpanded;

	@FindBy(css = "span[class='grid']")
	private List <WebElement> isClientListBelowDropdownList;

	@FindBy(css = "div[class='ui-inputselect-menu dropdown-menu active ui-inputselect-open'] li")
	private List <WebElement> isOptionsInDropdownValuesOfSortBy;

	@FindBy(css = "a[class='ui-inputselect ui-widget select-box ui-inputselect-dropdown default ui-corner-all'] span[class='ui-inputselect-icon ui-icon icon-arrow-expand-open ui-icon-triangle-1-s']")
	private WebElement isClientListDropdownClick;

	@FindBy(css = "span[class='icon icon-nested icon-filter'] span[class='icon-arrow-menu']")
	private WebElement isCollapsedFilterIcon;

	@FindBy(css = "div[class='view-formportfoliovalue'] span[class='ui-inputselect-icon ui-icon icon-arrow-expand-open ui-icon-triangle-1-s']")
	private WebElement isDropdownIconPortfoliaValuation;

	@FindBy(css = "div[class='view-formstate'] span[class='ui-inputselect-icon ui-icon icon-arrow-expand-open ui-icon-triangle-1-s']")
	private WebElement isDropdownIconStatesClientList;

	@FindBy(css = "div[class='view-formavailablecash'] span[class='ui-inputselect-icon ui-icon icon-arrow-expand-open ui-icon-triangle-1-s']")
	private WebElement isDropdownIconAvailableCash;

	@FindBy(css = "div[class='view-formproduct'] span[class='ui-inputselect-icon ui-icon icon-arrow-expand-open ui-icon-triangle-1-s']")
	private WebElement isDropdownIconProduct;

	@FindBy(css = "div[class='view-forminputselect'] ul[class='ui-widget ui-widget-content ui-inputselect-menu-dropdown ui-corner-bottom'] li")
	private List <WebElement> isDropdownValuesPortfolioValuation;

	@FindBy(css = "div[class='view-formproduct'] ul[class='ui-widget ui-widget-content ui-inputselect-menu-dropdown ui-corner-bottom'] li")
	private List <WebElement> isDropdownValuesProduct;

	@FindBy(css = "div[class='view-formstate'] ul[class='ui-widget ui-widget-content ui-inputselect-menu-dropdown ui-corner-bottom'] li")
	private List <WebElement> isDropdownValuesStatesClientList;

	@FindBy(css = "div[class='view-formavailablecash'] ul[class='ui-widget ui-widget-content ui-inputselect-menu-dropdown ui-corner-bottom'] li")
	private List <WebElement> isDropdownValuesAvailableCash;

	@FindBy(css = "span[class='view-updatebutton'] button[class=' btn- btn-action-secondary']")
	private WebElement isUpdateButtonClientList;

	@FindBy(css = "div[class='mvc-clientlisttable'] span[class='message'] p")
	private WebElement isErrorMessagePortfolio;

	@FindBy(css = "div[class='view-formportfoliovalue'] span[class='ui-inputselect-status label']")
	private WebElement isResetDefaultSelectionPortfolio;

	@FindBy(css = "div[class='view-formavailablecash'] span[class='ui-inputselect-status label']")
	private WebElement isResetDefaultSelectionAvailableCash;

	@FindBy(css = "div[class='view-formproduct'] span[class='ui-inputselect-status label']")
	private WebElement isResetDefaultSelectionProduct;

	@FindBy(css = "div[class='view-formstate'] span[class='ui-inputselect-status label']")
	private WebElement isResetDefaultSelectionStates;

	@FindBy(css = "span[class='view-button_2'] button[class=' btn- btn-action-tertiary'] span[class='label-content']")
	private WebElement isResetButtonClientList;

	@FindBy(css = "span[class='view-button_3'] button[class=' btn- btn-action-tertiary'] span[class='label-content']")
	private WebElement isCancelButtonClientList;

	@FindBy(css = "div[class='view-clientaccountname'] input[class='text-input']")
	private WebElement isGhostTextClientSearch;

	@FindBy(css = "h1[class='heading-five panel-header']")
	private WebElement isAccountOverviewScreen;

	@FindBy(css = "h1[class='heading-five panel-header']")
	private WebElement isClientListScreenHeader;

	@FindBy(css = "div[class='mvc-clientlistfilter'] [class='view-inputcheckbox'] span[class='inputcheckbox'] input")
	private WebElement isClientListActiveStatusBox;

	@FindBy(css = "div[class='mvc-clientlistfilter'] [class='view-inputcheckbox'] span[class='inputcheckbox'] label[class='selected']")
	private WebElement isDefaultClientListActiveStatusBox;

	@FindBy(css = "div[class='mvc-clientlistfilter'] [class='view-inputcheckbox_2'] span[class='inputcheckbox'] input")
	private WebElement isClientListActiveClosedAccountsBox;

	@FindBy(css = "div[class='mvc-clientlistfilter'] [class='view-inputcheckbox_3'] span[class='inputcheckbox'] input")
	private WebElement isClientListActivePendingRegistrationBox;

	@FindBy(css = "div[class='mvc-clientlistfilter'] [class='view-inputcheckbox']")
	private WebElement isClientListActiveStatusBoxClick;

	@FindBy(css = "div[class='mvc-clientlistfilter'] [class='view-inputcheckbox_2']")
	private WebElement isClientListActiveClosedAccountsBoxClick;

	@FindBy(css = "div[class='mvc-clientlistfilter'] [class='view-inputcheckbox_3']")
	private WebElement isClientListActivePendingRegistrationBoxClick;

	@FindBy(css = "td > span.filter-results-label > span.form-element-wrapper > strong")
	private WebElement isFilteredByMessage;

	@FindBy(css = "div[class='mvc-tableoptions'] span[class='view-button'] a[class='btn-action-tertiary'] span [class='label-content']")
	private WebElement isAddClientButtonClientList;

	@FindBy(css = "tbody")
	private WebElement isClientListRecordsList;

	@FindBy(css = "a[class='btn-more-results'] span[class='label']")
	private WebElement isSeeMoreButton;

	@FindBy(css = "a[class='btn-action-tertiary'] span[class='label-content']")
	private WebElement isAddClientButton;

	@FindBy(css = "div[class='mvc-clientlisttable'] thead :nth-of-type(1) th")
	private List <WebElement> isClientListHeadersList;

	@FindBy(css = "div.mvc-clientlisttable > table > tbody > tr:nth-child(1) td")
	private List <WebElement> isClientListTableFirstRecordDetails;

	/*//@FindBy(css = "tr > td[class='push-left-1  column-13  ']")
	@FindBy(xpath = "//tr[contains(@class,'expandable-row')]")
	private List <WebElement> istestAvailableCash;

	public List <WebElement> getIstestAvailableCash()
	{
		return istestAvailableCash;
	}
	*/
	public ClientsListPage(WebDriver driver)
	{
		super(driver);
	}

	public List <WebElement> getIsClientListTableFirstRecordDetails()
	{

		return isClientListTableFirstRecordDetails;
	}

	public WebElement getIsClientListHeader()
	{
		return isClientListScreenHeader;
	}

	public List <WebElement> getIsClientListExpandToggleIcon()
	{

		return isClientListExpandToggleIcon;
	}

	public WebElement enterClientSearch()
	{
		return isClientSearchBox;
	}

	public WebElement getTableRow()
	{
		return tablerows;
	}

	public WebElement getTableRowsInsideRow()
	{
		return tableRowsInsideRow;
	}

	public WebElement getSearch()
	{
		return isSearchBtn;
	}

	public WebElement getRecords()
	{
		return isRecords;
	}

	public WebElement clickShowMore()
	{
		return showMore;
	}

	public WebElement getIsRowCount()
	{
		return isRowCount;
	}

	public WebElement getIsClientSearchBox()
	{
		return isClientNameSearchBox;
	}

	public WebElement getIsClientListHeaders()
	{
		return isClientListHeaders;
	}

	public WebElement getIsClientListExpandedClientAccount1()
	{
		return isClientListExpandedClientAccount1;
	}

	public WebElement getIsClientListActionIconAccountRowExpanded()
	{
		return isClientListActionIconAccountRowExpanded;
	}

	public List <WebElement> getisOptionsInDropdownValuesOfSortBy()
	{
		isClientListDropdownClick.click();
		return isOptionsInDropdownValuesOfSortBy;
	}

	public List <WebElement> getIsDropdownValuesPortfolioValuation()
	{
		isDropdownIconPortfoliaValuation.click();

		return isDropdownValuesPortfolioValuation;
	}

	public List <WebElement> getIsDropdownValuesProduct()
	{
		isDropdownIconProduct.click();

		return isDropdownValuesProduct;
	}

	public List <WebElement> getIsDropdownValuesAvailableCash()
	{
		isDropdownIconAvailableCash.click();

		return isDropdownValuesAvailableCash;
	}

	public List <WebElement> getIsDropdownValuesStatesClientList()
	{
		isDropdownIconStatesClientList.click();

		return isDropdownValuesStatesClientList;
	}

	public List <WebElement> getIsClientListHeadersList()
	{
		return isClientListHeadersList;
	}

	public WebElement getIsCollapsedFilterIcon()
	{
		return isCollapsedFilterIcon;
	}

	public WebElement getIsUpdateButtonClientList()
	{
		return isUpdateButtonClientList;
	}

	public WebElement getIsErrorMessagePortfolio()
	{
		return isErrorMessagePortfolio;
	}

	public WebElement getIsResetDefaultSelectionPortfolio()
	{
		return isResetDefaultSelectionPortfolio;
	}

	public WebElement getIsResetDefaultSelectionAvailableCash()
	{
		return isResetDefaultSelectionAvailableCash;
	}

	public WebElement getIsResetDefaultSelectionProduct()
	{
		return isResetDefaultSelectionProduct;
	}

	public WebElement getIsResetDefaultSelectionStates()
	{
		return isResetDefaultSelectionStates;
	}

	public WebElement getIsResetButtonClientList()
	{
		return isResetButtonClientList;
	}

	public WebElement getIsCancelButtonClientList()
	{
		return isCancelButtonClientList;
	}

	public WebElement getIsGhostTextClientSearch()
	{
		return isGhostTextClientSearch;
	}

	public WebElement getIsAccountOverviewScreen()
	{
		return isAccountOverviewScreen;
	}

	public WebElement getIsFilteredByMessage()
	{
		return isFilteredByMessage;
	}

	public WebElement getIsAddClientButtonClientList()
	{
		return isAddClientButtonClientList;
	}

	public List <WebElement> getIsClientListBelowDropdownList()
	{
		isClientListActionIconAccountRowExpanded.click();
		return isClientListBelowDropdownList;
	}

	public WebElement getIsClientListActiveStatusBox()
	{
		//System.out.println("fasdf");
		return isClientListActiveStatusBox;
	}

	public WebElement getIsDefaultClientListActiveStatusBox()
	{

		return isDefaultClientListActiveStatusBox;
	}

	public WebElement getIsClientListActiveClosedAccountsBox()
	{
		return isClientListActiveClosedAccountsBox;
	}

	public WebElement getIsClientListActivePendingRegistrationBox()
	{
		return isClientListActivePendingRegistrationBox;
	}

	public WebElement getIsClientListActiveStatusBoxClick()
	{
		//System.out.println("fasdf");
		return isClientListActiveStatusBoxClick;
	}

	public WebElement getIsClientListActiveClosedAccountsBoxClick()
	{
		return isClientListActiveClosedAccountsBoxClick;
	}

	public WebElement getIsClientListActivePendingRegistrationBoxClick()
	{
		return isClientListActivePendingRegistrationBoxClick;
	}

	public WebElement getIsSeeMoreButton()
	{
		//System.out.println("INgetIsSeeMoreButton::" + isSeeMoreButton.toString());
		return isSeeMoreButton;
	}

	public WebElement getIsAddClientButton()
	{
		return isAddClientButton;
	}

	public WebElement getIsSeeMoreButtonClick()
	{
		String seeMoreText = getIsSeeMoreButton().getText();
		WebElement button = this.element(By.linkText(seeMoreText));
		return button;
	}

	public List <WebElement> getIsClientListRecordsList()
	{

		//WebElement tableProp = isClientListRecordsList;

		//List <WebElement> tr_collection = tableProp.findElements(By.tagName("tr").cssSelector("td.push-left-1"));

		List <WebElement> tr_collection = isClientListRecordsList.findElements(By.tagName("tr")
			.xpath("//tr[contains(@class,'expandable-row')]"));

		/*System.out.println("inside hkhk cssselect:" + tr_collection.size());

		System.out.println("inside hkhkcssselectortr>td::" + tr_collection.size());

		System.out.println("inside hkhk-istestAvailableCash::" + istestAvailableCash.size());*/

		return tr_collection;
	}

	public List <WebElement> getTableRecords()
	{
		WebElement table = this.tablerecord;
		return table.findElements(By.xpath("//*[@id='rootContainer']/div/div/div[2]/article/div/div[2]/div/div/div/div/div[3]/div/table/tbody/tr"));
	}

	public void notableCont() throws Throwable
	{
		assertEquals("No statements available for this period",
			this.find(By.cssSelector("td > div.view-messagealert > div.response-message.helpful-information  > span.message > p"))
				.getText());

	}

	public String navFee(String advisername) throws Throwable
	{

		this.getDriver().findElement(By.cssSelector("a.btn-tool.btn-filter")).click();
		Thread.sleep(2000);
		this.getDriver().findElement(By.name("formadviser")).clear();

		this.getDriver().findElement(By.name("formadviser")).sendKeys(advisername);
		Thread.sleep(2000);
		this.getDriver().findElement(By.cssSelector("strong > strong")).click();

		this.getDriver().findElement(By.xpath("//button[@type='button']")).click();
		Thread.sleep(2000);

		this.getDriver().findElement(By.cssSelector("span.icon.icon-arrow-expand")).click();

		this.getDriver().findElement(By.cssSelector("span.icon.icon-view-actions")).click();

		String availCash = this.getDriver()
			.findElements(By.cssSelector("div.mvc-clientlisttable > table > tbody > tr:nth-child(1) td"))
			.get(2)
			.getText();
		Thread.sleep(2000);
		this.getDriver().findElements(By.cssSelector("span[class='grid']")).get(3).click();

		return availCash;

	}

	public void navFeeschedulefee(String adviser) throws Throwable
	{

		Thread.sleep(2000);
		this.getDriver().findElement(By.cssSelector("a.btn-tool.btn-filter")).click();
		Thread.sleep(2000);
		this.getDriver().findElement(By.name("formadviser")).clear();
		Thread.sleep(2000);
		this.getDriver().findElement(By.name("formadviser")).sendKeys(adviser);
		Thread.sleep(2000);
		this.getDriver().findElement(By.cssSelector("a[class='ui-corner-all'] span")).click();
		Thread.sleep(2000);
		this.getDriver().findElement(By.xpath("//button[@type='button']")).click();
		Thread.sleep(2000);

		this.getDriver().findElement(By.cssSelector("span.icon.icon-arrow-expand")).click();
		Thread.sleep(2000);
		this.getDriver().findElement(By.cssSelector("span.icon.icon-view-actions")).click();

		Thread.sleep(2000);
		this.getDriver().findElements(By.cssSelector("span[class='grid']")).get(4).click();

		//return availCash;
	}

	public void loadTimeCont() throws Throwable
	{

		this.getDriver().manage().timeouts().pageLoadTimeout(300, TimeUnit.SECONDS);
		this.getDriver().manage().timeouts().setScriptTimeout(300, TimeUnit.SECONDS);
		this.getDriver().manage().timeouts().implicitlyWait(300, TimeUnit.SECONDS);
		//WebDriverWait some_element = new WebDriverWait(this.getDriver(), 100);
		//some_element.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.btn-more-results > span.label")));
		//this.getDriver().findElement(By.cssSelector("span.icon-arrow-chevron-open"));
		long startTime = System.currentTimeMillis();

		while (true)
		{
			try
			{
				if (this.getDriver().findElement(By.cssSelector("span.icon-arrow-chevron-open")).isDisplayed())
				{
					System.out.println("Time to load in seconds:" + (System.currentTimeMillis() - startTime) / 1000);
					break;
				}
				else
				{
					//this.getDriver().wait();
					Thread.sleep(1000);

				}
			}
			catch (InterruptedException e)
			{ /* Do nothing */

				e.printStackTrace();
			}

		}

	}

	public List <Map> tableContTest1(List <String> headerName, WebElement tableProp) throws Throwable
	{

		int row_num, col_num;
		row_num = 1;
		int no_of_rec = headerName.size();

		java.util.List <WebElement> td_collection = tableRowsInsideRow.findElements(By.xpath("td"));
		Map <String, String> actual = new HashMap <String, String>();

		col_num = 1;

		for (WebElement tdElement : td_collection)
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
			col_num++;
		}

		arrayList.add(actual);
		return arrayList;
	}

	public void waitForClientListPageToLoad() throws Throwable

	{

		for (int count = 0; count < 50; count++)
		{
			//Thread.sleep(2000);
			getDriver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			if (getIsRowCount().isDisplayed())
			{
				break;
			}

			count++;
		}

	}

}
