package com.apiTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class BasicAuthTests {
	Properties properties;
	InputStream input;
	FileWriter outputfile;
	CSVWriter csvWriter; 
	CSVReader csvReader;
	FileReader filereader;

	String resultfileCSV;
	String credfileCSV;
	String[][] strObj = null;

	@BeforeSuite
	public void setUp() throws IOException {
		String filePath = System.getProperty("user.dir")+File.separator+"src"+File.separator+"/test/resources/config.properties";

		properties =  new Properties();
		input = new FileInputStream(filePath);



		properties.load(input);

		RestAssured.baseURI = properties.getProperty("host");


		credfileCSV = System.getProperty("user.dir")+File.separator+"src"+File.separator+"/test/resources/dataSet.csv";
		resultfileCSV = System.getProperty("user.dir")+File.separator+"src"+File.separator+"/test/resources/resultSet.csv";

		CSVParser parser = new CSVParserBuilder().withSeparator(';').build(); 

		outputfile = new FileWriter(resultfileCSV);
		filereader = new FileReader(credfileCSV);

		csvWriter = new CSVWriter(outputfile); 
		csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).withCSVParser(parser).build();




	}



	@BeforeTest
	public void beforeexec() throws IOException{

		String[] header = { "Host", "apiPath", "auth-userName:password","actualResponse" ,"statusCode","AnyMSG"};

		if(csvReader.readNext() != null){ //reading a header rows
			csvWriter.writeNext(header);
			System.out.println("Header already exist..");
		}else{
			System.out.println("New Header creating.... ");

			csvWriter.writeNext(header);
		}

	}




	@DataProvider(name="credentialDataSet")
	public String[][] createTestDataRecords() throws IOException {
		List<String[]> allRows = csvReader.readAll();
		int i = 0;
		int j=0;


		try{

			/*while(!allRows.isEmpty()){
				System.out.println("Obj creting..");
				strObj = new String[allRows.size()][allRows.iterator().next().length];
				System.out.println("Obj creted...");	

			}*/

			strObj = new String[][]{
					{"revell938@gmail.com","Cybage@123"},
					{"revell938@gmail.com","123"},
					{"revell938@gmail","Cybage@123"},
					{" "," "},

			};

			/*
			for (String[] oneRow : allRows) { 
				for (String cellDT : oneRow) {
					strObj[i][j]=cellDT;
					System.out.print(cellDT + "\t"); 
					j++;
				} 
				System.out.println(); 
				i++;
			}*/
		}catch(Exception exc){
			System.out.println("problem to read and witere");
		}

		return strObj; 

	}



	@Test(dataProvider="credentialDataSet")
	public void basicAuthorization(String usrNm , String pass){
		SoftAssert softassert =  new SoftAssert();

		Response resp = RestAssured.given()
				.auth().preemptive().basic(usrNm.toString(), pass.toString())
				.when()
				.get("/v1/organizations/revell938-trial");

		resp.then().log().all();
		
		int responsecode = resp.statusCode();

		System.out.println("status code: "+responsecode+ " -->with massage : "+resp.statusLine());

		softassert.assertEquals(responsecode,200, "same how bad request or credentials worng ");

		//"Host", "apiPath", "auth-userName:password","actualResponse","status code","anyMSg"
		String[] resultRowData= {RestAssured.baseURI,RestAssured.basePath,usrNm.concat(":").concat(pass),resp.body().asString(),Integer.toString(responsecode),resp.statusLine()};

		csvWriter.writeNext(resultRowData);

		softassert.assertAll();


	}



	@AfterSuite
	public void closeSessions() throws IOException {
		// TODO Auto-generated method stub
		properties.clear();
		csvReader.close();
		csvWriter.close();


	}

}
