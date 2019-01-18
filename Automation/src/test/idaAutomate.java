package test;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class idaAutomate {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		idaAutomate obj = new idaAutomate();
		obj.FDG();
		obj.clusterAlgo();
		obj.barGraph();

	}
	
	public void FDG()
	{
		System.setProperty("webdriver.gecko.driver", "E:\\Jars\\geckodriver-v0.23.0-win64\\geckodriver.exe");
		WebDriver driver = new FirefoxDriver();
		
		driver.get("http://131.234.28.84:4200/");
		driver.manage().window().maximize();
		
		// Loading city dataset
		driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("I would like you to load the city dataset");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Send Enterkey
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		 // click city distanse
		 driver.findElement(By.xpath("//mat-panel-title[contains(text(),'City Distance')]")).click();

		
		 
		 try {
				Thread.sleep(1000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 // click the table for city schema
		driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[1]/app-data-view-container[1]/mat-tab-group[1]/div[1]/mat-tab-body[1]/div[1]/app-tab-view[1]/app-datatable-detail[1]/div[1]/mat-accordion[1]/mat-expansion-panel[2]/div[1]/div[1]/div[1]/div[2]/div[1]/button[1]")).click();
		 
		 try {
				Thread.sleep(1000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		 // FDG 
		driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("I would like a force directed graph visualization for the current table");
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("Source node is city1");
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("Target node is city2");
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("Strength between the nodes should be represented by distance");
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 try {
				Thread.sleep(2000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 //driver.quit();
		 driver.close();
		
	}
	
	public void clusterAlgo()
	{
		System.setProperty("webdriver.gecko.driver", "E:\\Jars\\geckodriver-v0.23.0-win64\\geckodriver.exe");
		WebDriver driver = new FirefoxDriver();
		
		driver.get("http://131.234.28.84:4200/");
		driver.manage().window().maximize();
		
		// Loading city dataset
		driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("I would like you to load the city dataset");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Send Enterkey
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 // click cost of living table
		 driver.findElement(By.xpath("//mat-panel-title[contains(text(),'Cost Of Living')]")).click();
		 try {
				Thread.sleep(1000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 // click the table for movehubcost of living
		driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[1]/app-data-view-container[1]/mat-tab-group[1]/div[1]/mat-tab-body[1]/div[1]/app-tab-view[1]/app-datatable-detail[1]/div[1]/mat-accordion[1]/mat-expansion-panel[3]/div[1]/div[1]/div[1]/div[2]/div[1]/button[1]")).click();
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//clustering queries
		driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("What are the available clustering algorithms?");
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("I would like to run the KMeans algorithm on the current table");
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("Optional parameters should be init and n_init");
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("Set n_clusters as 5");
		 
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("Set n_jobs as 8");
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("Set init as random");
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("Set n_init as 5");
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("Clustering features are wine, cinema and gasoline");
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("Label feature should be city");
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 try {
				Thread.sleep(3000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 driver.close();
	}
	
	

	public void barGraph()
	{
		System.setProperty("webdriver.gecko.driver", "E:\\Jars\\geckodriver-v0.23.0-win64\\geckodriver.exe");
		WebDriver driver = new FirefoxDriver();
		
		driver.get("http://131.234.28.84:4200/");
		driver.manage().window().maximize();
		
		// Loading city dataset
		driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("I would like you to load the city dataset");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Send Enterkey
		 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
		 try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 // click cost of living table
		 driver.findElement(By.xpath("//mat-panel-title[contains(text(),'Cost Of Living')]")).click();
		 try {
				Thread.sleep(1000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 // click the table for movehubcost of living
		driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[1]/app-data-view-container[1]/mat-tab-group[1]/div[1]/mat-tab-body[1]/div[1]/app-tab-view[1]/app-datatable-detail[1]/div[1]/mat-accordion[1]/mat-expansion-panel[3]/div[1]/div[1]/div[1]/div[2]/div[1]/button[1]")).click();
		 try {
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 // Bar diagram queries
			driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("I want a bar-graph visualisation for the current table");
			 try {
					Thread.sleep(500);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
			 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("x-axis is city");
			 try {
					Thread.sleep(500);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
			 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("y-axis is wine");
			 try {
					Thread.sleep(500);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
			 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys("Top 10 records, sorted descending on wine");
			 try {
					Thread.sleep(500);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 driver.findElement(By.xpath("//textarea[@id='mat-input-0']")).sendKeys(Keys.ENTER);
			 try {
					Thread.sleep(3000);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 //driver.quit();
			 driver.close();
	}

}
