import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

public class CityDataScraper extends SeleniumBase {
	
	@Test
	public void clickOnSomething() throws InterruptedException, IOException
	{

        WebDriver driver = getDriver();

        navigateTo("Depts/clerk/Services/Pages/CodeAmendments.aspx");
        jQueryWait(driver);
  
        FileOutputStream f = new FileOutputStream("/Users/macodari/CodeAmendments.txt", true);
        PrintStream p = new PrintStream(f); 
        for (int i = 2; i < 12; i++) {
            
			String txt = driver.findElement(By.xpath("/html/body/form/div[3]/div[3]/div[2]/div[3]/div/div[2]/div/div[2]/div[4]/table/tbody/tr[" + i + "]/td[2]")).getText();
			System.out.println(txt);
			p.println(txt + "\n");
        }
        f.close();
	}
}
