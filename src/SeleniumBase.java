

import junit.framework.TestCase;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.io.StringWriter;

public class SeleniumBase {
	
	public static final long WAIT_TIMEOUT = 30L;
    public static final long POLLING_TIMEOUT = 250L;

	public static BrowserType browserType = null;
	private final static long MULTI_THREAD_START_UP_DELAY = 5000;
	private static List<RemoteWebDriver> RemoteWebDrivers = Collections.synchronizedList(new ArrayList<RemoteWebDriver>());
	private static ThreadLocal<RemoteWebDriver> driverForThread = new ThreadLocal<RemoteWebDriver>() {

		@Override
		protected RemoteWebDriver initialValue() {
			if (RemoteWebDrivers.size() > 0) {
				try {
					Thread.sleep(MULTI_THREAD_START_UP_DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			RemoteWebDriver driver = null;
			try {
				driver = loadRemoteWebDriver();
			} catch (MalformedURLException e) {
				
				e.printStackTrace();
			}
			RemoteWebDrivers.add(driver);
			return driver;
		}
	};

	@BeforeSuite
	public static void setUpTest() {
		//set browser property
		if (System.getProperty("ds.browser") == null) {
			System.setProperty("ds.browser", "firefox");
		}

		if (System.getProperty("ds.browser").toLowerCase().equals("ie")) {
			browserType = BrowserType.IE;
		} else if (System.getProperty("ds.browser").toLowerCase().equals("firefox")) {
			browserType = (BrowserType) BrowserType.FIREFOX;
		} else if (System.getProperty("ds.browser").toLowerCase().equals("chrome")) {
			browserType = BrowserType.CHROME;
		} else if (System.getProperty("ds.browser").toLowerCase().equals("opera")) {
			browserType = BrowserType.OPERA;
		} else if (System.getProperty("ds.browser").toLowerCase().equals("safari")) {
			browserType = BrowserType.SAFARI;
		} else if (System.getProperty("ds.browser").toLowerCase().equals("htmlunit")) {
			browserType = BrowserType.HTMLUNIT;
		}	
		
		//set host
		if (System.getProperty("ds.host") == null) {
			//System.setProperty("ds.host", "http://library.municode.com");
			System.setProperty("ds.host", "http://www.clarkcountynv.gov");
		}
		
		//set Hub
		if (System.getProperty("ds.hub") == null) {
			System.setProperty("ds.hub", "http://localhost:4444/wd/hub");
		}
		
		

	}

	@AfterSuite
	public static void tearDown() {
		for (RemoteWebDriver driver : RemoteWebDrivers) {
			driver.quit();
		}
	}

	private static RemoteWebDriver loadRemoteWebDriver() throws MalformedURLException{
		System.out.println("Current Operating System: " + System.getProperties().getProperty("os.name"));
		System.out.println("Current Architecture: " + System.getProperties().getProperty("os.arch"));
		System.out.println("Current Browser Selection: " + browserType);
		
		DesiredCapabilities abilities = DesiredCapabilities.firefox();
		//Instantiate driver object
		
		if (System.getProperty("ds.browser").toLowerCase().equals("ie")) {
			abilities = DesiredCapabilities.internetExplorer();
		} else if (System.getProperty("ds.browser").toLowerCase().equals("firefox")) {
			abilities = DesiredCapabilities.firefox();
		} else if (System.getProperty("ds.browser").toLowerCase().equals("chrome")) {
			abilities = DesiredCapabilities.chrome();
		} else if (System.getProperty("ds.browser").toLowerCase().equals("opera")) {
			abilities = DesiredCapabilities.opera();
		} else if (System.getProperty("ds.browser").toLowerCase().equals("safari")) {
			abilities = DesiredCapabilities.safari();
		} else if (System.getProperty("ds.browser").toLowerCase().equals("htmlunit")) {
			abilities = DesiredCapabilities.htmlUnit();
		}

		RemoteWebDriver driver = new RemoteWebDriver(new URL(System.getProperty("ds.hub")), abilities);

		return driver;
	}
	 protected static RemoteWebDriver getDriver() {
		 return driverForThread.get();
	 }
	 
	 public ExpectedCondition<WebElement> visibilityOfElementLocated(final By locator) {
		  return new ExpectedCondition<WebElement>() {
		    public WebElement apply(WebDriver driver) {
		      WebElement toReturn = driver.findElement(locator);
		      if (toReturn.isDisplayed()) {
		        return toReturn;
		      }
		      return null;
		    }
		  };
		}
	 
	 public Boolean isTextPresent(String text) {
			return getDriver().getPageSource().contains(text);
	 }

	 
	 public WebElement waitForAndGetElement(final By by) {
		 WebDriver driver = getDriver();
		 
		 WebDriverWait wait = new WebDriverWait(driver, WAIT_TIMEOUT, POLLING_TIMEOUT);
	       
	        wait.until(new ExpectedCondition<Boolean>() {
	            @Override
	            public Boolean apply(WebDriver webDriver) {
	                return niceFindElement(by) != null;
	            }
	        });
	        return niceFindElement(by);
	    }
	 
	 public WebElement niceFindElement(final By by) {
			return findElement(by);
		}
	 
		public WebElement findElement(final By by) {
			return getDriver().findElement(by);
		}

	public static void jQueryWait(WebDriver driver) {
		int i = 0;
		try {
			do {
		        Thread.sleep(300);
		        i++;
		        if(i == 50){
	                driver.navigate().refresh();
	            }
		    }
		    while (!((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete") && i < 100);
		} catch (InterruptedException e) {}
	}
	
	public void navigateToHome(){
		getDriver().get(System.getProperty("ds.host"));
	}
	
	public static void navigateTo(String uri){
		getDriver().get(System.getProperty("ds.host") + "/" + uri);
	}
	
	// Return the response code of a provided URL
	
	public int responseCodeOfUrl(String location) {
		try {
			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection con = (HttpURLConnection) new URL(location).openConnection();
			con.setRequestMethod("HEAD");
			return con.getResponseCode();
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}