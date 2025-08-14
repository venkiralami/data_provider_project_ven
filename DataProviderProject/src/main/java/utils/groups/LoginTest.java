package utils.groups;

import java.util.List;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTest {

   
    public Object[][] getLoginData1(ITestContext context) {
        String filePath = "src/main/java/utils/groups/testdata.xlsx";
        String sheetName = "Sheet1";
        String[] includedGroups = context.getIncludedGroups();
        System.out.println(" \n Grups is Suite: " + String.join(", ", includedGroups));
        String targetGroup = includedGroups.length > 0 ? includedGroups[0] : "";
        System.out.println(" Target Group: " + targetGroup +"\n");
        List<Map<String, String>> excelData = ExcelUtils.getData(filePath, sheetName);
        System.out.println(" Total rows : " + excelData.size());
		if (targetGroup.isEmpty()) {
			System.out.println("No specific group provided, returning all data.");
			return excelData.stream()
					.filter(row -> row.get("Run").equalsIgnoreCase("Yes"))
					.map(row -> new Object[]{row.get("Username"), row.get("Password"), row.get("Group")})
					.toArray(Object[][]::new);
		}
        return excelData.stream()
                .filter(row -> row.get("Run").equalsIgnoreCase("Yes"))
                .filter(row -> row.get("Group").equalsIgnoreCase(targetGroup))
                .map(row -> new Object[]{row.get("Username"), row.get("Password"), row.get("Group")})
                .toArray(Object[][]::new);
    }
    
    @DataProvider(name = "loginData")
    public Object[][] getLoginData(ITestContext context) {
        String filePath = "src/main/java/utils/groups/testdata.xlsx";
        String sheetName = "Sheet1";
        String[] includedGroups = context.getIncludedGroups();

        List<Map<String, String>> excelData = ExcelUtils.getData(filePath, sheetName);

        return excelData.stream()
                .filter(row -> row.get("Run").equalsIgnoreCase("Yes"))
                .filter(row -> {
                    if (includedGroups.length == 0) return true; // no group filter
                    String[] rowGroups = row.get("Group").split(","); // handles multiple groups in Excel
                    // Check if any Excel group matches any included group
                    for (String rg : rowGroups) {
                        for (String ig : includedGroups) {
                            if (rg.trim().equalsIgnoreCase(ig.trim())) return true;
                        }
                    }
                    return false;
                })
                .map(row -> new Object[]{row.get("Username"), row.get("Password"), row.get("Group")})
                .toArray(Object[][]::new);
    }


    @Test(dataProvider = "loginData", groups = {"smoke"})
    public void smokeLoginTest(String username, String password, String groupName) {
    	System.out.println("[Smoke] :: Username: " + username + " | Password: " + password+ " | GroupName: " + groupName);
        
    }

    @Test(dataProvider = "loginData", groups = {"regression"})
    public void regressionLoginTest(String username, String password, String groupName) {
    	System.out.println("[Regression] :: Username: " + username + " | Password: " + password+ " | GroupName: " + groupName);
    }

    @Test(dataProvider = "loginData", groups = {"sanity"})
    public void sanityLoginTest(String username, String password, String groupName) {
    	System.out.println("[Sanity] :: Username: " + username + " | Password: " + password+ " | GroupName: " + groupName);
    }
    
    @Test(dataProvider = "loginData", groups = {"smoke","regression"})
    public void sanityRegressionLoginTest(String username, String password, String groupName) {
    	System.out.println("[Smoke or Regression] :: Username: " + username + " | Password: " + password+ " | GroupName: " + groupName);
    }
}
